def clamp(value: float, minimum: float, maximum: float) -> float:
    return min(maximum, max(minimum, value))


def round_number(value: float, decimals: int = 0) -> float:
    return round(value, decimals)


def format_number(value: float, decimals: int = 0) -> str:
    return f"{value:.{decimals}f}"


def parse_number(value: str) -> float | None:
    try:
        return float(value.strip())
    except ValueError:
        return None


def is_integer(value: float) -> bool:
    return float(value).is_integer()


def parse_int(value: str) -> int | None:
    trimmed = value.strip()
    if not trimmed.lstrip("-").isdigit():
        return None
    try:
        return int(trimmed)
    except ValueError:
        return None


def percent_format(value: float, decimals: int = 0) -> str:
    return f"{format_number(value * 100, decimals)}%"


def in_range(value: float, minimum: float, maximum: float) -> bool:
    return minimum <= value <= maximum


def abs(value: float) -> float:
    return value if value >= 0 else -value
