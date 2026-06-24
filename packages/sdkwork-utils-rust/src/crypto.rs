use crate::encoding::base64url_encode;
use hmac::{Hmac, Mac};
use sha2::{Digest, Sha256};

type HmacSha256 = Hmac<Sha256>;

pub fn sha256_hash(value: &[u8]) -> String {
    let digest = Sha256::digest(value);
    digest.iter().map(|byte| format!("{byte:02x}")).collect()
}

pub fn hmac_sha256(value: &[u8], secret: &[u8]) -> String {
    let mut mac = HmacSha256::new_from_slice(secret).expect("hmac key");
    mac.update(value);
    mac.finalize()
        .into_bytes()
        .iter()
        .map(|byte| format!("{byte:02x}"))
        .collect()
}

pub fn hmac_sha256_base64url(value: &[u8], secret: &[u8]) -> String {
    let mut mac = HmacSha256::new_from_slice(secret).expect("hmac key");
    mac.update(value);
    base64url_encode(&mac.finalize().into_bytes())
}

pub fn verify_hmac_sha256_base64url(value: &[u8], secret: &[u8], signature: &[u8]) -> bool {
    let mut mac = HmacSha256::new_from_slice(secret).expect("hmac key");
    mac.update(value);
    mac.verify_slice(signature).is_ok()
}

pub fn secure_compare(left: &str, right: &str) -> bool {
    if left.len() != right.len() {
        return false;
    }
    let mut result = 0u8;
    for (left_byte, right_byte) in left.bytes().zip(right.bytes()) {
        result |= left_byte ^ right_byte;
    }
    result == 0
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::encoding::base64url_decode;

    #[test]
    fn crypto_helpers() {
        assert_eq!(
            sha256_hash(b"hello"),
            "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824"
        );
        assert_eq!(
            hmac_sha256(b"payload", b"secret"),
            "b82fcb791acec57859b989b430a826488ce2e479fdf92326bd0a2e8375a42ba4"
        );
        let signature = hmac_sha256_base64url(b"payload", b"secret");
        assert!(verify_hmac_sha256_base64url(
            b"payload",
            b"secret",
            &base64url_decode(signature.as_str()).expect("signature")
        ));
    }
}
