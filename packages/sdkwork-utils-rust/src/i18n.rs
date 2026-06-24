use chrono::{DateTime, Utc};

use crate::datetime::parse_datetime;
use crate::number::round;

struct LocaleSeparators {
    decimal: char,
    grouping: char,
}

fn separators(locale: &str) -> LocaleSeparators {
    if locale.eq_ignore_ascii_case("de-DE") {
        LocaleSeparators {
            decimal: ',',
            grouping: '.',
        }
    } else {
        LocaleSeparators {
            decimal: '.',
            grouping: ',',
        }
    }
}

pub fn format_number_locale(value: f64, locale: &str, decimals: u32) -> String {
    let rounded = round(value, Some(decimals));
    let negative = rounded < 0.0;
    let absolute = round(rounded.abs(), Some(decimals));
    let decimals_usize = decimals as usize;
    let text = format!("{absolute:.decimals_usize$}");
    let parts: Vec<&str> = text.split('.').collect();
    let integer = parts[0];
    let fraction = parts.get(1).copied().unwrap_or("");
    let sep = separators(locale);

    let mut grouped = String::new();
    for (index, ch) in integer.chars().rev().enumerate() {
        if index > 0 && index % 3 == 0 {
            grouped.push(sep.grouping);
        }
        grouped.push(ch);
    }
    let grouped: String = grouped.chars().rev().collect();
    let mut result = grouped;
    if decimals > 0 {
        result.push(sep.decimal);
        result.push_str(fraction);
    }
    if negative {
        format!("-{result}")
    } else {
        result
    }
}

pub fn format_datetime_locale(value: DateTime<Utc>, locale: &str) -> String {
    let locale = locale.to_ascii_lowercase();
    if locale.starts_with("de") {
        value.format("%d.%m.%Y %H:%M").to_string()
    } else if locale.starts_with("zh") {
        value.format("%Y-%m-%d %H:%M").to_string()
    } else {
        value.format("%m/%d/%Y %H:%M").to_string()
    }
}

pub fn format_datetime_locale_str(value: &str, locale: &str) -> Option<String> {
    parse_datetime(value, None).map(|instant| format_datetime_locale(instant, locale))
}

pub fn parse_number_locale(input: &str, locale: &str) -> Option<f64> {
    let trimmed = input.trim();
    if trimmed.is_empty() {
        return None;
    }
    let sep = separators(locale);
    let normalized = trimmed.replace(sep.grouping, "").replace(sep.decimal, ".");
    normalized.parse::<f64>().ok()
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn i18n_helpers() {
        assert_eq!(format_number_locale(1234.5, "en-US", 2), "1,234.50");
        assert_eq!(format_number_locale(1234.5, "de-DE", 2), "1.234,50");
        assert_eq!(parse_number_locale("1,234.50", "en-US"), Some(1234.5));
        assert_eq!(parse_number_locale("1.234,50", "de-DE"), Some(1234.5));
        let formatted = format_datetime_locale_str("2024-06-15T14:30:00.000Z", "en-US").unwrap();
        assert!(formatted.contains("2024"));
    }
}
