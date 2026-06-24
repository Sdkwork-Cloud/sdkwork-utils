from dataclasses import dataclass
from typing import Callable, Generic, TypeVar

T = TypeVar("T")
U = TypeVar("U")


@dataclass
class ResultValue(Generic[T]):
    value: T | None = None
    error: str | None = None

    @staticmethod
    def ok(value: T) -> "ResultValue[T]":
        return ResultValue(value=value)

    @staticmethod
    def err(message: str) -> "ResultValue[T]":
        return ResultValue(error=message)

    def is_ok(self) -> bool:
        return self.error is None

    def is_err(self) -> bool:
        return self.error is not None

    def unwrap_or(self, default: T) -> T:
        if self.is_err() or self.value is None:
            return default
        return self.value

    def map(self, mapper: Callable[[T], U]) -> "ResultValue[U]":
        if self.is_err():
            return ResultValue.err(self.error or "error")
        return ResultValue.ok(mapper(self.value))  # type: ignore[arg-type]
