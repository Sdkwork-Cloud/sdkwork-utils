_UNITS = ("B", "KB", "MB", "GB", "TB", "PB")


def format_bytes(value: int | float, decimals: int = 1) -> str:
    bytes_count = max(0, int(value))
    if bytes_count < 1024:
        return f"{bytes_count} B"

    size = float(bytes_count)
    unit_index = 0
    while size >= 1024 and unit_index < len(_UNITS) - 1:
        size /= 1024
        unit_index += 1

    return f"{size:.{decimals}f} {_UNITS[unit_index]}"
