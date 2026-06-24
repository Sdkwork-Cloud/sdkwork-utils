pub fn parse_bool(value: &str) -> Option<bool> {
    match value.trim().to_ascii_lowercase().as_str() {
        "true" | "1" | "yes" | "on" => Some(true),
        "false" | "0" | "no" | "off" => Some(false),
        _ => None,
    }
}

pub fn is_truthy(value: Option<&str>) -> bool {
    match value {
        None => false,
        Some(text) if text.trim().is_empty() => false,
        Some(text) => !matches!(
            text.trim().to_ascii_lowercase().as_str(),
            "false" | "0" | "no" | "off"
        ),
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn boolean_helpers() {
        assert_eq!(parse_bool("true"), Some(true));
        assert_eq!(parse_bool("0"), Some(false));
        assert_eq!(parse_bool("maybe"), None);
        assert!(is_truthy(Some("yes")));
        assert!(!is_truthy(Some("false")));
        assert!(!is_truthy(Some("  ")));
    }
}
