//! HTTP JSON 边界的 uint64-as-string 透明 serde helper。
//!
//! 遵循 `sdkwork-specs/API_SPEC.md` 与 `SDK_SPEC.md`：
//! - Rust 内部领域模型保持原生 `u64`。
//! - 浏览器 / TypeScript SDK / OpenAPI JSON 边界必须用字符串表示 uint64，
//!   避免 JavaScript 精度丢失。
//!
//! 用法：
//! ```ignore
//! # use serde::{Deserialize, Serialize};
//! #[derive(Serialize, Deserialize)]
//! struct Timestamp {
//!     #[serde(with = "sdkwork_utils_rust::serde_uint64")]
//!     pub millis: u64,
//! }
//! ```
//!
//! 反序列化时严格校验十进制字符串，空串、负号与非数字字符会被拒绝。

use serde::{Deserialize, Deserializer, Serializer};

const ERR_EMPTY: &str = "uint64 string must not be empty";
const ERR_INVALID: &str = "uint64 string must match ^[0-9]+$";
const ERR_OUT_OF_RANGE: &str = "uint64 string out of u64 range";

/// 序列化 `u64` → 十进制字符串。
pub fn serialize<S>(value: &u64, serializer: S) -> Result<S::Ok, S::Error>
where
    S: Serializer,
{
    serializer.collect_str(value)
}

/// 反序列化十进制字符串 → `u64`，严格校验格式与范围。
pub fn deserialize<'de, D>(deserializer: D) -> Result<u64, D::Error>
where
    D: Deserializer<'de>,
{
    let raw = String::deserialize(deserializer)?;
    parse_strict(&raw).map_err(serde::de::Error::custom)
}

/// `Option<u64>` 的伴生 helper：序列化 / 反序列化 None 与 Some(u64)。
pub mod option {
    use super::parse_strict;
    use serde::de::Error as _;
    use serde::{Deserialize, Deserializer, Serializer};

    pub fn serialize<S>(value: &Option<u64>, serializer: S) -> Result<S::Ok, S::Error>
    where
        S: Serializer,
    {
        match value {
            Some(v) => serializer.collect_str(v),
            None => serializer.serialize_none(),
        }
    }

    pub fn deserialize<'de, D>(deserializer: D) -> Result<Option<u64>, D::Error>
    where
        D: Deserializer<'de>,
    {
        let raw: Option<String> = Option::deserialize(deserializer)?;
        match raw {
            None => Ok(None),
            Some(text) => parse_strict(&text).map(Some).map_err(D::Error::custom),
        }
    }
}

/// 严格解析十进制字符串为 `u64`，不支持负号；空串与非数字字符一律拒绝。
fn parse_strict(raw: &str) -> Result<u64, &'static str> {
    if raw.is_empty() {
        return Err(ERR_EMPTY);
    }

    let bytes = raw.as_bytes();

    // Reject negative sign — u64 cannot be negative.
    if bytes.first() == Some(&b'-') {
        return Err(ERR_INVALID);
    }

    // Strip optional leading '+'.
    let digits = if bytes.first() == Some(&b'+') {
        &bytes[1..]
    } else {
        bytes
    };

    if digits.is_empty() || !digits.iter().all(|b| b.is_ascii_digit()) {
        return Err(ERR_INVALID);
    }

    u64::from_str_radix(std::str::from_utf8(digits).map_err(|_| ERR_INVALID)?, 10)
        .map_err(|_| ERR_OUT_OF_RANGE)
}

#[cfg(test)]
mod tests {
    use serde::{Deserialize, Serialize};

    #[derive(Serialize, Deserialize, PartialEq, Debug)]
    struct Wrapper {
        #[serde(with = "super")]
        value: u64,
    }

    #[derive(Serialize, Deserialize, PartialEq, Debug)]
    struct OptionalWrapper {
        #[serde(with = "super::option", default)]
        value: Option<u64>,
    }

    #[test]
    fn serializes_positive_as_decimal_string() {
        let wrapper = Wrapper { value: 123_456 };
        let json = serde_json::to_string(&wrapper).unwrap();
        assert_eq!(json, r#"{"value":"123456"}"#);
    }

    #[test]
    fn serializes_zero_as_decimal_string() {
        let wrapper = Wrapper { value: 0 };
        let json = serde_json::to_string(&wrapper).unwrap();
        assert_eq!(json, r#"{"value":"0"}"#);
    }

    #[test]
    fn serializes_max_uint64_as_decimal_string() {
        let wrapper = Wrapper { value: u64::MAX };
        let json = serde_json::to_string(&wrapper).unwrap();
        assert_eq!(
            json,
            r#"{"value":"18446744073709551615"}"#
        );
    }

    #[test]
    fn deserializes_decimal_string_to_u64() {
        let json = r#"{"value":"9876543210"}"#;
        let wrapper: Wrapper = serde_json::from_str(json).unwrap();
        assert_eq!(wrapper.value, 9_876_543_210);
    }

    #[test]
    fn deserializes_zero_string() {
        let json = r#"{"value":"0"}"#;
        let wrapper: Wrapper = serde_json::from_str(json).unwrap();
        assert_eq!(wrapper.value, 0);
    }

    #[test]
    fn rejects_non_numeric_input() {
        let json = r#"{"value":"abc"}"#;
        let result: Result<Wrapper, _> = serde_json::from_str(json);
        assert!(result.is_err());
    }

    #[test]
    fn rejects_empty_string() {
        let json = r#"{"value":""}"#;
        let result: Result<Wrapper, _> = serde_json::from_str(json);
        assert!(result.is_err());
    }

    #[test]
    fn rejects_negative_value() {
        let json = r#"{"value":"-1"}"#;
        let result: Result<Wrapper, _> = serde_json::from_str(json);
        assert!(result.is_err());
    }

    #[test]
    fn rejects_overflow_value() {
        let json = r#"{"value":"18446744073709551616"}"#;
        let result: Result<Wrapper, _> = serde_json::from_str(json);
        assert!(result.is_err());
    }

    #[test]
    fn optional_round_trips_some() {
        let wrapper = OptionalWrapper { value: Some(42) };
        let json = serde_json::to_string(&wrapper).unwrap();
        assert_eq!(json, r#"{"value":"42"}"#);
        let parsed: OptionalWrapper = serde_json::from_str(&json).unwrap();
        assert_eq!(parsed, wrapper);
    }

    #[test]
    fn optional_round_trips_none() {
        let wrapper = OptionalWrapper { value: None };
        let json = serde_json::to_string(&wrapper).unwrap();
        assert_eq!(json, r#"{"value":null}"#);
        let parsed: OptionalWrapper = serde_json::from_str(&json).unwrap();
        assert_eq!(parsed, wrapper);
    }
}
