use crate::string::is_blank;

pub fn coalesce<'a>(values: &[Option<&'a str>]) -> Option<&'a str> {
    for value in values {
        if let Some(text) = value {
            if !is_blank(Some(text)) {
                return Some(text.trim());
            }
        }
    }
    None
}

pub fn default_if_blank(value: Option<&str>, default: &str) -> String {
    match value {
        Some(text) if !is_blank(Some(text)) => text.trim().to_string(),
        _ => default.to_string(),
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn optional_helpers() {
        assert_eq!(
            coalesce(&[None, Some(""), Some("  "), Some("ok")]),
            Some("ok")
        );
        assert_eq!(default_if_blank(Some("  "), "fallback"), "fallback");
    }
}
