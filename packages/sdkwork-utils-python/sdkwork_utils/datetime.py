from datetime import datetime, timedelta, timezone

DEFAULT_PATTERN = "iso8601"


def now() -> datetime:
    return datetime.now(timezone.utc)


def format_datetime(value: datetime, pattern: str = DEFAULT_PATTERN) -> str:
    if pattern != DEFAULT_PATTERN:
        raise ValueError(f"Unsupported datetime pattern: {pattern}")
    utc = value.astimezone(timezone.utc)
    millis = utc.microsecond // 1000
    return utc.strftime(f"%Y-%m-%dT%H:%M:%S.{millis:03d}Z")


def parse_datetime(value: str, pattern: str = DEFAULT_PATTERN) -> datetime | None:
    if pattern != DEFAULT_PATTERN:
        return None
    text = value.strip().replace("Z", "+00:00")
    try:
        parsed = datetime.fromisoformat(text)
    except ValueError:
        return None
    if parsed.tzinfo is None:
        parsed = parsed.replace(tzinfo=timezone.utc)
    return parsed.astimezone(timezone.utc)


def add_days(value: datetime, days: int) -> datetime:
    return value + timedelta(days=days)


def add_hours(value: datetime, hours: int) -> datetime:
    return value + timedelta(hours=hours)


def add_minutes(value: datetime, minutes: int) -> datetime:
    return value + timedelta(minutes=minutes)


def diff_millis(earlier: datetime, later: datetime) -> int:
    return int((later - earlier).total_seconds() * 1000)


def is_before(first: datetime, second: datetime) -> bool:
    return first < second


def is_after(first: datetime, second: datetime) -> bool:
    return first > second


def start_of_day_utc(value: datetime) -> datetime:
    utc = value.astimezone(timezone.utc)
    return datetime(utc.year, utc.month, utc.day, tzinfo=timezone.utc)


def end_of_day_utc(value: datetime) -> datetime:
    start = start_of_day_utc(value)
    return start + timedelta(days=1) - timedelta(milliseconds=1)


def to_unix_millis(value: datetime) -> int:
    return int(value.timestamp() * 1000)


def from_unix_millis(value: int) -> datetime | None:
    try:
        return datetime.fromtimestamp(value / 1000, tz=timezone.utc)
    except (OSError, OverflowError, ValueError):
        return None


def is_same_instant(first: datetime, second: datetime) -> bool:
    return to_unix_millis(first) == to_unix_millis(second)
