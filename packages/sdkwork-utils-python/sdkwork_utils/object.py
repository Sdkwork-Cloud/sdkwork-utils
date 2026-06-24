from collections.abc import Mapping, MutableMapping
from typing import Any


def pick(source: Mapping[str, Any], keys: list[str]) -> dict[str, Any]:
    return {key: source[key] for key in keys if key in source}


def omit(source: Mapping[str, Any], keys: list[str]) -> dict[str, Any]:
    blocked = set(keys)
    return {key: value for key, value in source.items() if key not in blocked}


def get_path(source: Any, path: str) -> Any | None:
    current = source
    for segment in [part for part in path.split(".") if part]:
        if not isinstance(current, dict) or segment not in current:
            return None
        current = current[segment]
    return current


def has_path(source: Any, path: str) -> bool:
    value = get_path(source, path)
    return value is not None


def shallow_merge(base: Any, overlay: Any) -> Any:
    if isinstance(base, dict) and isinstance(overlay, dict):
        return {**base, **overlay}
    return overlay


def set_path(source: MutableMapping[str, Any], path: str, value: Any) -> dict[str, Any]:
    segments = [part for part in path.split(".") if part]
    if not segments:
        return dict(source)

    current: MutableMapping[str, Any] = source
    for segment in segments[:-1]:
        next_value = current.get(segment)
        if not isinstance(next_value, dict):
            next_value = {}
            current[segment] = next_value
        current = next_value
    current[segments[-1]] = value
    return dict(source)


def deep_merge(base: Any, overlay: Any) -> Any:
    if isinstance(base, dict) and isinstance(overlay, dict):
        merged = dict(base)
        for key, value in overlay.items():
            merged[key] = deep_merge(merged[key], value) if key in merged else value
        return merged
    return overlay


def compact_map(source: dict) -> dict:
    return {key: value for key, value in source.items() if value is not None}


def keys(source: dict) -> list[str]:
    return list(source.keys())


def values(source: dict) -> list:
    return list(source.values())
