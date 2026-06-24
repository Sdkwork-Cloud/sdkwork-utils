import re

EMAIL_RE = re.compile(r"^[A-Za-z0-9._%+\-]+@[A-Za-z0-9.\-]+\.[A-Za-z]{2,}$")
UUID_RE = re.compile(
    r"^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$"
)
URL_RE = re.compile(r"^https?://[^\s/$.?#].[^\s]*$")
IPV4_RE = re.compile(r"^(25[0-5]|2[0-4]\d|1?\d?\d)(\.(25[0-5]|2[0-4]\d|1?\d?\d)){3}$")
E164_RE = re.compile(r"^\+[1-9]\d{1,14}$")


def _is_ipv6_shape(value: str) -> bool:
    if not value or not re.fullmatch(r"[0-9a-fA-F:]+", value):
        return False
    if value.count("::") > 1:
        return False

    def is_valid_part(part: str) -> bool:
        return 0 < len(part) <= 4

    if "::" in value:
        left, right = value.split("::", 1)
        left_parts = [part for part in left.split(":") if part]
        right_parts = [part for part in right.split(":") if part]
        if not all(is_valid_part(part) for part in left_parts + right_parts):
            return False
        return len(left_parts) + len(right_parts) < 8
    parts = value.split(":")
    return len(parts) == 8 and all(is_valid_part(part) for part in parts)


def is_email(value: str) -> bool:
    return bool(EMAIL_RE.match(value.strip()))


def is_uuid(value: str) -> bool:
    return bool(UUID_RE.match(value.strip()))


def is_url(value: str) -> bool:
    return bool(URL_RE.match(value.strip()))


def is_numeric(value: str) -> bool:
    try:
        float(value.strip())
    except ValueError:
        return False
    return True


def is_ipv4(value: str) -> bool:
    return bool(IPV4_RE.match(value.strip()))


def is_ipv6(value: str) -> bool:
    return _is_ipv6_shape(value.strip())


def is_phone_e164(value: str) -> bool:
    return bool(E164_RE.match(value.strip()))
