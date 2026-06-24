from collections import defaultdict
from collections.abc import Callable, Hashable, Iterable, Sequence


def unique(items: Iterable[Hashable]) -> list[Hashable]:
    seen: set[Hashable] = set()
    result: list[Hashable] = []
    for item in items:
        if item not in seen:
            seen.add(item)
            result.append(item)
    return result


def chunk(items: Sequence, size: int) -> list[list]:
    if size <= 0:
        return []
    return [list(items[index : index + size]) for index in range(0, len(items), size)]


def group_by(items: Iterable, key_fn: Callable) -> dict:
    groups: dict = defaultdict(list)
    for item in items:
        groups[key_fn(item)].append(item)
    return dict(groups)


def flatten(items: Sequence[Sequence]) -> list:
    result: list = []
    for group in items:
        result.extend(group)
    return result


def compact(items: list) -> list:
    return [item for item in items if item is not None]


def first(items: list):
    return items[0] if items else None


def last(items: list):
    return items[-1] if items else None


def sort_by(items: list, key_fn: Callable) -> list:
    return sorted(items, key=key_fn)


def key_by(items: list, key_fn: Callable) -> dict:
    result: dict = {}
    for item in items:
        result[key_fn(item)] = item
    return result


def filter(items: list, predicate: Callable) -> list:
    return [item for item in items if predicate(item)]


def find(items: list, predicate: Callable):
    for item in items:
        if predicate(item):
            return item
    return None
