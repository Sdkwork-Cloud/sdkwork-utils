use regex::Regex;
use std::sync::OnceLock;

fn email_re() -> &'static Regex {
    static RE: OnceLock<Regex> = OnceLock::new();
    RE.get_or_init(|| {
        Regex::new(r"^[A-Za-z0-9._%+\-]+@[A-Za-z0-9.\-]+\.[A-Za-z]{2,}$").expect("email regex")
    })
}

fn uuid_re() -> &'static Regex {
    static RE: OnceLock<Regex> = OnceLock::new();
    RE.get_or_init(|| {
        Regex::new(
            r"^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
        )
        .expect("uuid regex")
    })
}

fn url_re() -> &'static Regex {
    static RE: OnceLock<Regex> = OnceLock::new();
    RE.get_or_init(|| Regex::new(r"^https?://[^\s/$.?#].[^\s]*$").expect("url regex"))
}

pub fn is_email(value: &str) -> bool {
    email_re().is_match(value.trim())
}

pub fn is_uuid(value: &str) -> bool {
    uuid_re().is_match(value.trim())
}

pub fn is_url(value: &str) -> bool {
    url_re().is_match(value.trim())
}

pub fn is_numeric(value: &str) -> bool {
    value.trim().parse::<f64>().is_ok()
}

fn ipv4_re() -> &'static Regex {
    static RE: OnceLock<Regex> = OnceLock::new();
    RE.get_or_init(|| {
        Regex::new(r"^(25[0-5]|2[0-4]\d|1?\d?\d)(\.(25[0-5]|2[0-4]\d|1?\d?\d)){3}$")
            .expect("ipv4 regex")
    })
}

pub fn is_ipv4(value: &str) -> bool {
    ipv4_re().is_match(value.trim())
}

fn e164_re() -> &'static Regex {
    static RE: OnceLock<Regex> = OnceLock::new();
    RE.get_or_init(|| Regex::new(r"^\+[1-9]\d{1,14}$").expect("e164 regex"))
}

fn is_ipv6_shape(value: &str) -> bool {
    if value.is_empty() || !value.chars().all(|ch| ch.is_ascii_hexdigit() || ch == ':') {
        return false;
    }
    if value.matches("::").count() > 1 {
        return false;
    }
    let is_valid_part = |part: &str| !part.is_empty() && part.len() <= 4;
    if let Some((left, right)) = value.split_once("::") {
        let left_parts: Vec<&str> = left.split(':').filter(|part| !part.is_empty()).collect();
        let right_parts: Vec<&str> = right.split(':').filter(|part| !part.is_empty()).collect();
        if !left_parts.iter().all(|part| is_valid_part(part))
            || !right_parts.iter().all(|part| is_valid_part(part))
        {
            return false;
        }
        return left_parts.len() + right_parts.len() < 8;
    }
    let parts: Vec<&str> = value.split(':').collect();
    parts.len() == 8 && parts.iter().all(|part| is_valid_part(part))
}

pub fn is_ipv6(value: &str) -> bool {
    is_ipv6_shape(value.trim())
}

pub fn is_phone_e164(value: &str) -> bool {
    e164_re().is_match(value.trim())
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn validators() {
        assert!(is_email("user@example.com"));
        assert!(is_uuid("550e8400-e29b-41d4-a716-446655440000"));
        assert!(is_url("https://example.com/path"));
        assert!(is_numeric("12.5"));
    }
}
