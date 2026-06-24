import re

_WORD_SPLIT = re.compile(r"[^a-zA-Z0-9]+")
_CAMEL_BOUNDARY = re.compile(r"(?x)(?<=[a-z0-9])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])")


def _camel_parts(value: str) -> list[str]:
    normalized = _CAMEL_BOUNDARY.sub(" ", value.strip())
    return [part.lower() for part in _WORD_SPLIT.split(normalized) if part]


def is_blank(value: str | None) -> bool:
    return value is None or value.strip() == ""


def trim(value: str) -> str:
    return value.strip()


def truncate(value: str, max_len: int, suffix: str = "...") -> str:
    if max_len <= 0:
        return ""
    if len(value) <= max_len:
        return value
    if len(suffix) >= max_len:
        return suffix[:max_len]
    return f"{value[: max_len - len(suffix)]}{suffix}"


def capitalize(value: str) -> str:
    if not value:
        return ""
    return value[0].upper() + value[1:].lower()


def camel_case(value: str) -> str:
    parts = _camel_parts(value)
    if not parts:
        return ""
    return parts[0] + "".join(capitalize(part) for part in parts[1:])


def snake_case(value: str) -> str:
    return "_".join(_camel_parts(value))


def kebab_case(value: str) -> str:
    return "-".join(_camel_parts(value))


def slugify(value: str) -> str:
    slug = re.sub(r"[^a-z0-9-]", "", kebab_case(value))
    return slug.strip("-")


def mask(value: str, visible_start: int, visible_end: int, mask_char: str = "*") -> str:
    if visible_start + visible_end >= len(value):
        return value
    hidden = len(value) - visible_start - visible_end
    return f"{value[:visible_start]}{mask_char * hidden}{value[-visible_end:]}"


def pad_start(value: str, target_len: int, pad_char: str = " ") -> str:
    if len(value) >= target_len:
        return value
    return f"{pad_char * (target_len - len(value))}{value}"


def pad_end(value: str, target_len: int, pad_char: str = " ") -> str:
    if len(value) >= target_len:
        return value
    return f"{value}{pad_char * (target_len - len(value))}"


def starts_with(value: str, prefix: str) -> bool:
    return value.startswith(prefix)


def ends_with(value: str, suffix: str) -> bool:
    return value.endswith(suffix)


def contains(value: str, substring: str) -> bool:
    return substring in value


def replace_all(value: str, search: str, replacement: str) -> str:
    return value.replace(search, replacement)


def split(value: str, delimiter: str, trim_parts: bool = True) -> list[str]:
    parts = value.split(delimiter)
    if trim_parts:
        parts = [part.strip() for part in parts]
        parts = [part for part in parts if part]
    return parts


def join(parts: list[str], separator: str) -> str:
    return separator.join(parts)


def repeat(value: str, count: int) -> str:
    if count < 0:
        raise ValueError("repeat count must be >= 0")
    return value * count


def normalize_whitespace(value: str) -> str:
    return " ".join(value.split())


_TEMPLATE_KEY = re.compile(r"\{([a-zA-Z_][a-zA-Z0-9_]*)\}")


def template(pattern: str, values: dict[str, str]) -> str:
    return _TEMPLATE_KEY.sub(lambda match: values.get(match.group(1), match.group(0)), pattern)
