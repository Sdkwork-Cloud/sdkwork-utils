//! Trusted-proxy client IP extraction for gateway rate limiting and abuse controls.
//!
//! Only honours `X-Forwarded-For` / `X-Real-IP` when trusted proxies are configured.
//! Configure via `SDKWORK_IM_GATEWAY_TRUSTED_PROXIES` (comma-separated IPs or
//! CIDR networks such as `10.0.0.0/8`).

use std::collections::hash_map::DefaultHasher;
use std::hash::{Hash, Hasher};
use std::net::{IpAddr, Ipv4Addr, Ipv6Addr};

const TRUSTED_PROXIES_ENV: &str = "SDKWORK_IM_GATEWAY_TRUSTED_PROXIES";

/// Trusted reverse-proxy allowlist controlling forwarded header parsing.
///
/// Entries may be exact IP addresses or CIDR networks. An IPv4 CIDR never
/// matches an IPv6 address and vice versa.
#[derive(Clone, Debug, Default)]
pub struct TrustedProxyConfig {
    trusted_proxies: Vec<IpAddr>,
    trusted_cidrs: Vec<(IpAddr, u8)>,
}

impl TrustedProxyConfig {
    /// Load from `SDKWORK_IM_GATEWAY_TRUSTED_PROXIES`.
    pub fn from_env() -> Self {
        let raw = std::env::var(TRUSTED_PROXIES_ENV).unwrap_or_default();
        Self::from_entries(raw.split(',').map(str::trim))
    }

    /// Build from an explicit CIDR/IP entry list (e.g. gateway
    /// `trusted_proxy_cidrs` configuration).
    pub fn from_cidrs<'a, I>(entries: I) -> Self
    where
        I: IntoIterator<Item = &'a str>,
    {
        Self::from_entries(entries.into_iter())
    }

    fn from_entries<'a>(entries: impl Iterator<Item = &'a str>) -> Self {
        let mut config = Self::default();
        for segment in entries.filter(|segment| !segment.is_empty()) {
            if let Some((cidr, prefix)) = parse_cidr(segment) {
                config.trusted_cidrs.push((cidr, prefix));
            } else if let Ok(ip) = segment.parse::<IpAddr>() {
                config.trusted_proxies.push(ip);
            }
        }
        config
    }

    pub fn is_trusted(&self, ip: &IpAddr) -> bool {
        self.trusted_proxies.iter().any(|trusted| trusted == ip)
            || self
                .trusted_cidrs
                .iter()
                .any(|(network, prefix)| ip_in_cidr(*ip, *network, *prefix))
    }

    pub fn is_empty(&self) -> bool {
        self.trusted_proxies.is_empty() && self.trusted_cidrs.is_empty()
    }
}

fn parse_cidr(value: &str) -> Option<(IpAddr, u8)> {
    let (address, prefix) = value.split_once('/')?;
    let prefix = prefix.parse::<u8>().ok()?;
    let ip = address.trim().parse::<IpAddr>().ok()?;
    let max_prefix = if ip.is_ipv4() { 32 } else { 128 };
    if prefix > max_prefix {
        return None;
    }
    Some((ip, prefix))
}

fn ip_in_cidr(ip: IpAddr, network: IpAddr, prefix: u8) -> bool {
    match (ip, network) {
        (IpAddr::V4(ip), IpAddr::V4(network)) => {
            let mask = if prefix == 0 {
                0
            } else {
                u32::MAX << (32 - u32::from(prefix))
            };
            (u32::from(ip) & mask) == (u32::from(network) & mask)
        }
        (IpAddr::V6(ip), IpAddr::V6(network)) => {
            let mask = if prefix == 0 {
                0
            } else {
                u128::MAX << (128 - u128::from(prefix))
            };
            (u128::from(ip) & mask) == (u128::from(network) & mask)
        }
        _ => false,
    }
}

/// Parse a comma/space separated list into CIDR entries for validation tests.
#[doc(hidden)]
pub fn parse_cidr_entries(input: &str) -> Vec<(IpAddr, u8)> {
    input
        .split(',')
        .map(str::trim)
        .filter(|segment| !segment.is_empty())
        .filter_map(parse_cidr)
        .collect()
}

#[allow(dead_code)]
fn _ipv4(a: u8, b: u8, c: u8, d: u8) -> IpAddr {
    IpAddr::V4(Ipv4Addr::new(a, b, c, d))
}

#[allow(dead_code)]
fn _ipv6() -> IpAddr {
    IpAddr::V6(Ipv6Addr::LOCALHOST)
}

/// Resolve the client IP from optional direct peer IP and HTTP headers.
pub fn extract_client_ip(
    peer_ip: Option<IpAddr>,
    get_header: impl Fn(&str) -> Option<String>,
    config: &TrustedProxyConfig,
) -> IpAddr {
    if let Some(peer) = peer_ip {
        if config.is_empty() {
            return peer;
        }
        if config.is_trusted(&peer) {
            if let Some(ip) = parse_forwarded_for_trusted(&get_header, config) {
                return ip;
            }
            if let Some(ip) = parse_header_ip(&get_header, "x-real-ip") {
                return ip;
            }
        }
        return peer;
    }

    if !config.is_empty() {
        if let Some(ip) = parse_forwarded_for_trusted(&get_header, config) {
            return ip;
        }
        if let Some(ip) = parse_header_ip(&get_header, "x-real-ip") {
            return ip;
        }
    }

    fallback_ip_from_headers(&get_header)
}

/// Header-only extraction when the TCP peer is unavailable (e.g. post-upgrade tasks).
pub fn extract_client_ip_from_headers(get_header: impl Fn(&str) -> Option<String>) -> IpAddr {
    let config = TrustedProxyConfig::from_env();
    if !config.is_empty() {
        if let Some(ip) = parse_forwarded_for_trusted(&get_header, &config) {
            return ip;
        }
        if let Some(ip) = parse_header_ip(&get_header, "x-real-ip") {
            return ip;
        }
    }
    fallback_ip_from_headers(&get_header)
}

fn parse_forwarded_for_trusted(
    get_header: &impl Fn(&str) -> Option<String>,
    config: &TrustedProxyConfig,
) -> Option<IpAddr> {
    let raw = get_header("x-forwarded-for")?;
    let chain: Vec<&str> = raw
        .split(',')
        .map(str::trim)
        .filter(|segment| !segment.is_empty())
        .collect();
    if chain.is_empty() {
        return None;
    }
    for entry in chain.iter().rev() {
        if let Ok(ip) = entry.parse::<IpAddr>() {
            if !config.is_trusted(&ip) {
                return Some(ip);
            }
        }
    }
    chain.first().and_then(|entry| entry.parse::<IpAddr>().ok())
}

fn parse_header_ip(get_header: &impl Fn(&str) -> Option<String>, name: &str) -> Option<IpAddr> {
    get_header(name)
        .as_deref()
        .and_then(|value| value.trim().parse::<IpAddr>().ok())
}

/// Differentiate unknown clients instead of collapsing into one rate-limit bucket.
fn fallback_ip_from_headers(get_header: &impl Fn(&str) -> Option<String>) -> IpAddr {
    let mut hasher = DefaultHasher::new();
    if let Some(user_agent) = get_header("user-agent") {
        user_agent.hash(&mut hasher);
    }
    if let Some(language) = get_header("accept-language") {
        language.hash(&mut hasher);
    }
    let time_bucket = std::time::SystemTime::now()
        .duration_since(std::time::UNIX_EPOCH)
        .unwrap_or_default()
        .as_secs()
        / 10;
    time_bucket.hash(&mut hasher);
    let hash = hasher.finish();
    let octet3 = ((hash >> 8) & 0xFF) as u8;
    let octet4 = (hash & 0xFF) as u8;
    IpAddr::V4(std::net::Ipv4Addr::new(198, 51, octet3, octet4))
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn ignores_forwarded_headers_without_trusted_proxies() {
        let config = TrustedProxyConfig::default();
        let ip = extract_client_ip(
            Some("203.0.113.10".parse().expect("peer")),
            |name| {
                if name == "x-forwarded-for" {
                    Some("198.51.100.20".to_owned())
                } else {
                    None
                }
            },
            &config,
        );
        assert_eq!(ip, "203.0.113.10".parse::<IpAddr>().expect("peer"));
    }

    #[test]
    fn parses_forwarded_chain_from_trusted_proxy_peer() {
        let config = TrustedProxyConfig {
            trusted_proxies: vec!["10.0.0.1".parse().expect("proxy")],
            trusted_cidrs: Vec::new(),
        };
        let ip = extract_client_ip(
            Some("10.0.0.1".parse().expect("peer")),
            |name| {
                if name == "x-forwarded-for" {
                    Some("203.0.113.10, 10.0.0.1".to_owned())
                } else {
                    None
                }
            },
            &config,
        );
        assert_eq!(ip, "203.0.113.10".parse::<IpAddr>().expect("client"));
    }

    #[test]
    fn cidr_entries_match_member_addresses() {
        let config =
            TrustedProxyConfig::from_cidrs(["10.0.0.0/8", "192.168.1.0/24", "2001:db8::/32"]);
        assert!(config.is_trusted(&"10.1.2.3".parse().expect("in cidr")));
        assert!(config.is_trusted(&"192.168.1.200".parse().expect("in cidr")));
        assert!(config.is_trusted(&"2001:db8:1::5".parse().expect("in cidr")));
        assert!(!config.is_trusted(&"11.0.0.1".parse().expect("out cidr")));
        assert!(!config.is_trusted(&"192.168.2.1".parse().expect("out cidr")));
        assert!(!config.is_trusted(&"2001:db9::1".parse().expect("out cidr")));
        // IPv4 CIDR never matches IPv6 and vice versa (IPv4-mapped form included).
        assert!(!config.is_trusted(&"::ffff:10.0.0.1".parse::<IpAddr>().expect("v6-mapped")));
    }

    #[test]
    fn forwarded_chain_honours_cidr_trusted_proxies() {
        let config = TrustedProxyConfig::from_cidrs(["172.16.0.0/12"]);
        let ip = extract_client_ip(
            Some("172.16.5.9".parse().expect("peer")),
            |name| {
                if name == "x-forwarded-for" {
                    Some("198.51.100.7, 172.16.5.9".to_owned())
                } else {
                    None
                }
            },
            &config,
        );
        assert_eq!(ip, "198.51.100.7".parse::<IpAddr>().expect("client"));
    }
}
