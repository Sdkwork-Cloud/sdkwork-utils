use regex::Regex;
use std::sync::OnceLock;

fn word_split_re() -> &'static Regex {
    static RE: OnceLock<Regex> = OnceLock::new();
    RE.get_or_init(|| Regex::new(r"[^a-zA-Z0-9]+").expect("word split regex"))
}

fn camel_parts(input: &str) -> Vec<String> {
    let mut normalized = String::with_capacity(input.len() + 8);
    let chars: Vec<char> = input.trim().chars().collect();
    for (index, ch) in chars.iter().enumerate() {
        if index > 0 {
            let prev = chars[index - 1];
            let next = chars.get(index + 1).copied();
            let boundary_before_upper = prev.is_ascii_lowercase() && ch.is_ascii_uppercase();
            let boundary_in_acronym = prev.is_ascii_uppercase()
                && ch.is_ascii_uppercase()
                && next
                    .map(|value| value.is_ascii_lowercase())
                    .unwrap_or(false);
            if boundary_before_upper || boundary_in_acronym {
                normalized.push(' ');
            }
        }
        normalized.push(*ch);
    }

    word_split_re()
        .split(&normalized)
        .filter(|part| !part.is_empty())
        .map(|part| part.to_lowercase())
        .collect()
}

pub fn is_blank(value: Option<&str>) -> bool {
    match value {
        None => true,
        Some(text) => text.trim().is_empty(),
    }
}

pub fn trim(value: &str) -> String {
    value.trim().to_string()
}

pub fn truncate(value: &str, max_len: usize, suffix: Option<&str>) -> String {
    if max_len == 0 {
        return String::new();
    }
    let suffix = suffix.unwrap_or("...");
    if value.chars().count() <= max_len {
        return value.to_string();
    }
    if suffix.chars().count() >= max_len {
        return suffix.chars().take(max_len).collect();
    }
    let keep = max_len - suffix.chars().count();
    format!("{}{}", value.chars().take(keep).collect::<String>(), suffix)
}

pub fn capitalize(value: &str) -> String {
    let mut chars = value.chars();
    match chars.next() {
        None => String::new(),
        Some(first) => {
            let mut result = first.to_uppercase().collect::<String>();
            result.push_str(&chars.as_str().to_lowercase());
            result
        }
    }
}

pub fn camel_case(value: &str) -> String {
    let parts = camel_parts(value);
    if parts.is_empty() {
        return String::new();
    }
    let mut result = parts[0].clone();
    for part in parts.iter().skip(1) {
        result.push_str(&capitalize(part));
    }
    result
}

pub fn snake_case(value: &str) -> String {
    camel_parts(value).join("_")
}

pub fn kebab_case(value: &str) -> String {
    camel_parts(value).join("-")
}

pub fn slugify(value: &str) -> String {
    kebab_case(value)
        .chars()
        .filter(|ch| ch.is_ascii_alphanumeric() || *ch == '-')
        .collect::<String>()
        .trim_matches('-')
        .to_string()
}

pub fn mask(
    value: &str,
    visible_start: usize,
    visible_end: usize,
    mask_char: Option<char>,
) -> String {
    let mask_char = mask_char.unwrap_or('*');
    let chars: Vec<char> = value.chars().collect();
    let len = chars.len();
    if visible_start + visible_end >= len {
        return value.to_string();
    }
    let mut result = String::with_capacity(len);
    for (index, ch) in chars.iter().enumerate() {
        if index < visible_start || index >= len - visible_end {
            result.push(*ch);
        } else {
            result.push(mask_char);
        }
    }
    result
}

pub fn pad_start(value: &str, target_len: usize, pad_char: Option<char>) -> String {
    let pad_char = pad_char.unwrap_or(' ');
    let current = value.chars().count();
    if current >= target_len {
        return value.to_string();
    }
    let pad_count = target_len - current;
    format!("{}{}", pad_char.to_string().repeat(pad_count), value)
}

pub fn pad_end(value: &str, target_len: usize, pad_char: Option<char>) -> String {
    let pad_char = pad_char.unwrap_or(' ');
    let current = value.chars().count();
    if current >= target_len {
        return value.to_string();
    }
    let pad_count = target_len - current;
    format!("{}{}", value, pad_char.to_string().repeat(pad_count))
}

pub fn starts_with(value: &str, prefix: &str) -> bool {
    value.starts_with(prefix)
}

pub fn ends_with(value: &str, suffix: &str) -> bool {
    value.ends_with(suffix)
}

pub fn contains(value: &str, substring: &str) -> bool {
    value.contains(substring)
}

pub fn replace_all(value: &str, search: &str, replacement: &str) -> String {
    value.replace(search, replacement)
}

pub fn split(value: &str, delimiter: &str, trim_parts: Option<bool>) -> Vec<String> {
    let trim_parts = trim_parts.unwrap_or(true);
    value
        .split(delimiter)
        .map(|part| {
            if trim_parts {
                part.trim().to_string()
            } else {
                part.to_string()
            }
        })
        .filter(|part| !trim_parts || !part.is_empty())
        .collect()
}

pub fn join(parts: &[&str], separator: &str) -> String {
    parts.join(separator)
}

pub fn repeat(value: &str, count: usize) -> String {
    value.repeat(count)
}

pub fn normalize_whitespace(value: &str) -> String {
    value.split_whitespace().collect::<Vec<_>>().join(" ")
}

pub fn template(pattern: &str, values: &std::collections::HashMap<&str, &str>) -> String {
    static TEMPLATE_KEY: std::sync::LazyLock<regex::Regex> =
        std::sync::LazyLock::new(|| regex::Regex::new(r"\{([a-zA-Z_][a-zA-Z0-9_]*)\}").unwrap());
    TEMPLATE_KEY
        .replace_all(pattern, |caps: &regex::Captures| {
            let key = &caps[1];
            values
                .get(key)
                .map(|value| (*value).to_string())
                .unwrap_or_else(|| caps[0].to_string())
        })
        .into_owned()
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn blank_and_trim() {
        assert!(is_blank(None));
        assert!(is_blank(Some("  \t")));
        assert_eq!(trim("  hello  "), "hello");
    }

    #[test]
    fn case_conversions() {
        assert_eq!(camel_case("hello_world"), "helloWorld");
        assert_eq!(snake_case("HelloWorld"), "hello_world");
        assert_eq!(kebab_case("Hello World"), "hello-world");
        assert_eq!(slugify("Hello, SDKWork!"), "hello-sdk-work");
    }

    #[test]
    fn truncate_and_mask() {
        assert_eq!(truncate("abcdef", 5, None), "ab...");
        assert_eq!(mask("1234567890", 2, 2, None), "12******90");
    }
}
