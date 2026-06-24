import base64
import binascii
from urllib.parse import quote, unquote


def base64_encode(value: bytes | str) -> str:
    data = value.encode("utf-8") if isinstance(value, str) else value
    return base64.b64encode(data).decode("ascii")


def base64_decode(value: str) -> bytes | None:
    try:
        return base64.b64decode(value.strip(), validate=True)
    except (binascii.Error, ValueError):
        return None


def hex_encode(value: bytes) -> str:
    return value.hex()


def hex_decode(value: str) -> bytes | None:
    trimmed = value.strip()
    if len(trimmed) % 2 != 0:
        return None
    try:
        return bytes.fromhex(trimmed)
    except ValueError:
        return None


def url_encode(value: str) -> str:
    return quote(value, safe="")


def url_decode(value: str) -> str | None:
    try:
        return unquote(value)
    except ValueError:
        return None


def base64url_encode(value: bytes | str) -> str:
    data = value.encode("utf-8") if isinstance(value, str) else value
    return base64.urlsafe_b64encode(data).decode("ascii").rstrip("=")


def base64url_decode(value: str) -> bytes | None:
    trimmed = value.strip()
    if not trimmed:
        return None
    padding = "=" * ((4 - len(trimmed) % 4) % 4)
    try:
        return base64.urlsafe_b64decode(trimmed + padding)
    except (binascii.Error, ValueError):
        return None
