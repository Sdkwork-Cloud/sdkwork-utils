//! Trusted-proxy client IP extraction for gateway rate limiting and abuse controls.
//!
//! Only honours `X-Forwarded-For` / `X-Real-IP` when trusted proxies are configured.
//! Configure via `SDKWORK_IM_GATEWAY_TRUSTED_PROXIES` (comma-separated IPs).

use std::collections::hash_map::DefaultHasher;
use std::hash::{Hash, Hasher};
use std::net::IpAddr;

const TRUSTED_PROXIES_ENV: &str = "SDKWORK_IM_GATEWAY_TRUSTED_PROXIES";

/// Trusted reverse-proxy allowlist controlling forwarded header parsing.
#[derive(Clone, Debug, Default)]
pub struct TrustedProxyConfig {
    trusted_proxies: Vec<IpAddr>,
}

impl TrustedProxyConfig {
    /// Load from `SDKWORK_IM_GATEWAY_TRUSTED_PROXIES`.
    pub fn from_env() -> Self {
        let raw = std::env::var(TRUSTED_PROXIES_ENV).unwrap_or_default();
        let trusted_proxies = raw
            .split(',')
            .map(str::trim)
            .filter(|segment| !segment.is_empty())
            .filter_map(|segment| segment.parse::<IpAddr>().ok())
            .collect();
        Self { trusted_proxies }
    }

    pub fn is_trusted(&self, ip: &IpAddr) -> bool {
        self.trusted_proxies.iter().any(|trusted| trusted == ip)
    }

    pub fn is_empty(&self) -> bool {
        self.trusted_proxies.is_empty()
    }
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
        assert_eq!(ip, "203.0.113.10".parse().expect("peer"));
    }

    #[test]
    fn parses_forwarded_chain_from_trusted_proxy_peer() {
        let config = TrustedProxyConfig {
            trusted_proxies: vec!["10.0.0.1".parse().expect("proxy")],
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
        assert_eq!(ip, "203.0.113.10".parse().expect("client"));
    }
}
