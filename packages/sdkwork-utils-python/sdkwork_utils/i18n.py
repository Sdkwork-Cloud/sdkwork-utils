from datetime import datetime

from sdkwork_utils.datetime import parse_datetime
from sdkwork_utils.number import round_number


def _separators(locale: str) -> tuple[str, str]:
    if locale.lower() == "de-de":
        return ",", "."
    return ".", ","


def format_number_locale(value: float, locale: str, decimals: int = 0) -> str:
    rounded = round_number(abs(value), decimals)
    decimal_sep, grouping_sep = _separators(locale)
    integer_part, _, fraction_part = f"{rounded:.{decimals}f}".partition(".")
    grouped = ""
    for index, digit in enumerate(reversed(integer_part)):
        if index > 0 and index % 3 == 0:
            grouped = grouping_sep + grouped
        grouped = digit + grouped
    if decimals > 0:
        formatted = f"{grouped}{decimal_sep}{fraction_part.ljust(decimals, '0')}"
    else:
        formatted = grouped
    return f"-{formatted}" if value < 0 else formatted


def format_datetime_locale(value: datetime, locale: str) -> str:
    normalized = locale.lower()
    if normalized.startswith("de"):
        return value.strftime("%d.%m.%Y %H:%M")
    if normalized.startswith("zh"):
        return value.strftime("%Y-%m-%d %H:%M")
    return value.strftime("%m/%d/%Y %H:%M")


def format_datetime_locale_str(value: str, locale: str) -> str | None:
    parsed = parse_datetime(value)
    return format_datetime_locale(parsed, locale) if parsed is not None else None


def parse_number_locale(input: str, locale: str) -> float | None:
    trimmed = input.strip()
    if not trimmed:
        return None
    decimal_sep, grouping_sep = _separators(locale)
    normalized = trimmed.replace(grouping_sep, "").replace(decimal_sep, ".")
    try:
        return float(normalized)
    except ValueError:
        return None
