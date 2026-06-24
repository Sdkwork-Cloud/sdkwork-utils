use base64::{
    engine::general_purpose::{STANDARD, URL_SAFE_NO_PAD},
    Engine as _,
};

pub fn base64_encode(value: &[u8]) -> String {
    STANDARD.encode(value)
}

pub fn base64_decode(value: &str) -> Option<Vec<u8>> {
    STANDARD.decode(value.trim()).ok()
}

pub fn hex_encode(value: &[u8]) -> String {
    value.iter().map(|byte| format!("{byte:02x}")).collect()
}

pub fn hex_decode(value: &str) -> Option<Vec<u8>> {
    let trimmed = value.trim();
    if trimmed.len() % 2 != 0 {
        return None;
    }
    (0..trimmed.len())
        .step_by(2)
        .map(|index| u8::from_str_radix(&trimmed[index..index + 2], 16).ok())
        .collect::<Option<Vec<u8>>>()
}

pub fn url_encode(value: &str) -> String {
    urlencoding::encode(value).into_owned()
}

pub fn url_decode(value: &str) -> Option<String> {
    urlencoding::decode(value)
        .ok()
        .map(|decoded| decoded.into_owned())
}

pub fn base64url_encode(value: &[u8]) -> String {
    URL_SAFE_NO_PAD.encode(value)
}

pub fn base64url_decode(value: &str) -> Option<Vec<u8>> {
    URL_SAFE_NO_PAD.decode(value.trim()).ok()
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn encoding_helpers() {
        let bytes = b"hello";
        assert_eq!(base64_decode(&base64_encode(bytes)).unwrap(), bytes);
        assert_eq!(hex_decode(&hex_encode(bytes)).unwrap(), bytes);
    }
}
