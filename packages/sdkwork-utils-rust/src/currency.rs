use std::collections::HashMap;

use crate::i18n::format_number_locale;
use crate::number::round;

const KNOWN: &[(&str, u32, &str)] = &[
    ("USD", 2, "$"),
    ("EUR", 2, "€"),
    ("GBP", 2, "£"),
    ("CNY", 2, "¥"),
    ("JPY", 0, "¥"),
    ("KRW", 0, "₩"),
    ("HKD", 2, "HK$"),
    ("TWD", 2, "NT$"),
    ("CHF", 2, "CHF"),
    ("CAD", 2, "CA$"),
    ("AUD", 2, "A$"),
    ("INR", 2, "₹"),
    ("BHD", 3, "BHD"),
    ("KWD", 3, "KWD"),
];

fn lookup(code: &str) -> Option<(&str, u32, &str)> {
    let normalized = code.trim();
    if normalized.len() != 3 || !normalized.chars().all(|ch| ch.is_ascii_uppercase()) {
        return None;
    }
    KNOWN
        .iter()
        .copied()
        .find(|(known, _, _)| *known == normalized)
}

pub fn is_currency_code(value: &str) -> bool {
    lookup(value).is_some()
}

pub fn minor_unit_exponent(code: &str) -> Option<u32> {
    lookup(code).map(|(_, exponent, _)| exponent)
}

pub fn to_minor_units(amount: f64, code: &str) -> Option<i64> {
    let (_, exponent, _) = lookup(code)?;
    let factor = 10_f64.powi(exponent as i32);
    Some((round(amount * factor, Some(0))) as i64)
}

pub fn from_minor_units(minor: i64, code: &str) -> Option<f64> {
    let (_, exponent, _) = lookup(code)?;
    let factor = 10_f64.powi(exponent as i32);
    Some(minor as f64 / factor)
}

fn suffix_locale(locale: &str) -> bool {
    let normalized = locale.to_ascii_lowercase();
    normalized.starts_with("de")
        || normalized.starts_with("fr")
        || normalized.starts_with("it")
        || normalized.starts_with("es")
}

pub fn format_currency(amount: f64, code: &str, locale: &str) -> Option<String> {
    let (_, exponent, symbol) = lookup(code)?;
    let formatted = format_number_locale(amount, locale, exponent);
    if suffix_locale(locale) {
        Some(format!("{formatted} {symbol}"))
    } else {
        Some(format!("{symbol}{formatted}"))
    }
}

pub fn currency_metadata() -> HashMap<&'static str, (u32, &'static str)> {
    KNOWN
        .iter()
        .map(|(code, exponent, symbol)| (*code, (*exponent, *symbol)))
        .collect()
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn currency_helpers() {
        assert!(is_currency_code("USD"));
        assert!(!is_currency_code("usd"));
        assert_eq!(minor_unit_exponent("JPY"), Some(0));
        assert_eq!(to_minor_units(12.34, "USD"), Some(1234));
        assert_eq!(from_minor_units(1234, "USD"), Some(12.34));
        assert_eq!(
            format_currency(1234.5, "USD", "en-US"),
            Some("$1,234.50".to_string())
        );
        assert_eq!(
            format_currency(1234.5, "EUR", "de-DE"),
            Some("1.234,50 €".to_string())
        );
    }
}
