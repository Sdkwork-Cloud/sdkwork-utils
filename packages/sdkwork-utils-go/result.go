package utils

type ResultValue[T any] struct {
	Value T
	Error string
	Ok    bool
}

func Ok[T any](value T) ResultValue[T] {
	return ResultValue[T]{Value: value, Ok: true}
}

func Err[T any](message string) ResultValue[T] {
	return ResultValue[T]{Error: message, Ok: false}
}

func IsOk[T any](result ResultValue[T]) bool {
	return result.Ok
}

func IsErr[T any](result ResultValue[T]) bool {
	return !result.Ok
}

func UnwrapOr[T any](result ResultValue[T], defaultValue T) T {
	if result.Ok {
		return result.Value
	}
	return defaultValue
}

func Map[T any, U any](result ResultValue[T], mapper func(T) U) ResultValue[U] {
	if !result.Ok {
		return Err[U](result.Error)
	}
	return Ok(mapper(result.Value))
}
