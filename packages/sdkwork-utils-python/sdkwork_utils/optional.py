from sdkwork_utils.string import is_blank


def coalesce(*values: str | None) -> str | None:
    for value in values:
        if not is_blank(value):
            return value.strip() if value is not None else None
    return None


def default_if_blank(value: str | None, default: str) -> str:
    if is_blank(value):
        return default
    return value.strip() if value is not None else default
