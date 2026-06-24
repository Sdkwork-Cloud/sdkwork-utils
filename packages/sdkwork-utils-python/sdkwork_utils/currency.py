from sdkwork_utils.i18n import format_number_locale
from sdkwork_utils.number import round_number

_KNOWN: dict[str, tuple[int, str]] = {
    "USD": (2, "$"),
    "EUR": (2, "€"),
    "GBP": (2, "£"),
    "CNY": (2, "¥"),
    "JPY": (0, "¥"),
    "KRW": (0, "₩"),
    "HKD": (2, "HK$"),
    "TWD": (2, "NT$"),
    "CHF": (2, "CHF"),
    "CAD": (2, "CA$"),
    "AUD": (2, "A$"),
    "INR": (2, "₹"),
    "BHD": (3, "BHD"),
    "KWD": (3, "KWD"),
}


def _lookup(code: str) -> tuple[int, str] | None:
    normalized = code.strip()
    if len(normalized) != 3 or normalized != normalized.upper() or not normalized.isalpha():
        return None
    return _KNOWN.get(normalized)


def is_currency_code(value: str) -> bool:
    return _lookup(value) is not None


def minor_unit_exponent(code: str) -> int | None:
    meta = _lookup(code)
    return meta[0] if meta else None


def to_minor_units(amount: float, code: str) -> int | None:
    meta = _lookup(code)
    if meta is None:
        return None
    exponent, _ = meta
    factor = 10**exponent
    return int(round_number(amount * factor, 0))


def from_minor_units(minor: int, code: str) -> float | None:
    meta = _lookup(code)
    if meta is None:
        return None
    exponent, _ = meta
    factor = 10**exponent
    return minor / factor


def _suffix_locale(locale: str) -> bool:
    normalized = locale.lower()
    return normalized.startswith(("de", "fr", "it", "es"))


def format_currency(amount: float, code: str, locale: str) -> str | None:
    meta = _lookup(code)
    if meta is None:
        return None
    exponent, symbol = meta
    formatted = format_number_locale(amount, locale, exponent)
    if _suffix_locale(locale):
        return f"{formatted} {symbol}"
    return f"{symbol}{formatted}"
