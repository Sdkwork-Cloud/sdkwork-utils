import hashlib
import hmac


def sha256_hash(value: bytes | str) -> str:
    data = value.encode("utf-8") if isinstance(value, str) else value
    return hashlib.sha256(data).hexdigest()


def hmac_sha256(value: bytes | str, secret: bytes | str) -> str:
    data = value.encode("utf-8") if isinstance(value, str) else value
    key = secret.encode("utf-8") if isinstance(secret, str) else secret
    return hmac.new(key, data, hashlib.sha256).hexdigest()


def secure_compare(left: str, right: str) -> bool:
    left_bytes = left.encode("utf-8")
    right_bytes = right.encode("utf-8")
    if len(left_bytes) != len(right_bytes):
        return False
    result = 0
    for left_byte, right_byte in zip(left_bytes, right_bytes, strict=True):
        result |= left_byte ^ right_byte
    return result == 0
