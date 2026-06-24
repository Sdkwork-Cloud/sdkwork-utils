from typing import Any
import copy


def deep_equal(left: Any, right: Any) -> bool:
    if left is right:
        return True
    if left is None or right is None:
        return False
    if isinstance(left, list) and isinstance(right, list):
        if len(left) != len(right):
            return False
        return all(deep_equal(left_item, right_item) for left_item, right_item in zip(left, right))
    if isinstance(left, dict) and isinstance(right, dict):
        if set(left.keys()) != set(right.keys()):
            return False
        return all(deep_equal(left[key], right[key]) for key in left)
    return left == right


def deep_clone(value: Any) -> Any:
    return copy.deepcopy(value)
