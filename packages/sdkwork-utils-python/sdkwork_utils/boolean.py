from sdkwork_utils.string import is_blank


def parse_bool(value: str) -> bool | None:
    match value.strip().lower():
        case "true" | "1" | "yes" | "on":
            return True
        case "false" | "0" | "no" | "off":
            return False
        case _:
            return None


def is_truthy(value: str | None) -> bool:
    if is_blank(value):
        return False
    return value.strip().lower() not in {"false", "0", "no", "off"}
