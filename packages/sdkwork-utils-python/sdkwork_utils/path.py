def join_path(*segments: str) -> str:
    cleaned = [segment.strip("/") for segment in segments if segment.strip("/")]
    return "/".join(cleaned)


def normalize_path(value: str) -> str:
    parts = [part for part in value.split("/") if part]
    joined = "/".join(parts)
    return f"/{joined}" if value.startswith("/") else joined
