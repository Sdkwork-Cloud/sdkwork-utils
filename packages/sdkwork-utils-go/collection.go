package utils

import (
	"cmp"
	"slices"
)

func Unique[T comparable](items []T) []T {
	seen := make(map[T]struct{}, len(items))
	result := make([]T, 0, len(items))
	for _, item := range items {
		if _, ok := seen[item]; ok {
			continue
		}
		seen[item] = struct{}{}
		result = append(result, item)
	}
	return result
}

func Chunk[T any](items []T, size int) [][]T {
	if size <= 0 {
		return nil
	}
	result := make([][]T, 0, (len(items)+size-1)/size)
	for index := 0; index < len(items); index += size {
		end := index + size
		if end > len(items) {
			end = len(items)
		}
		result = append(result, items[index:end])
	}
	return result
}

func GroupBy[T any, K comparable](items []T, keyFn func(T) K) map[K][]T {
	groups := make(map[K][]T)
	for _, item := range items {
		key := keyFn(item)
		groups[key] = append(groups[key], item)
	}
	return groups
}

func Flatten[T any](items [][]T) []T {
	var result []T
	for _, group := range items {
		result = append(result, group...)
	}
	return result
}

func CompactAny(items []any) []any {
	result := make([]any, 0, len(items))
	for _, item := range items {
		if item != nil {
			result = append(result, item)
		}
	}
	return result
}

func First[T any](items []T) (T, bool) {
	if len(items) == 0 {
		var zero T
		return zero, false
	}
	return items[0], true
}

func Last[T any](items []T) (T, bool) {
	if len(items) == 0 {
		var zero T
		return zero, false
	}
	return items[len(items)-1], true
}

func SortBy[T any, K cmp.Ordered](items []T, keyFn func(T) K) []T {
	sorted := append([]T(nil), items...)
	slices.SortStableFunc(sorted, func(left, right T) int {
		leftKey := keyFn(left)
		rightKey := keyFn(right)
		switch {
		case leftKey < rightKey:
			return -1
		case leftKey > rightKey:
			return 1
		default:
			return 0
		}
	})
	return sorted
}

func KeyBy[T any, K comparable](items []T, keyFn func(T) K) map[K]T {
	result := make(map[K]T, len(items))
	for _, item := range items {
		result[keyFn(item)] = item
	}
	return result
}

func Filter[T any](items []T, predicate func(T) bool) []T {
	result := make([]T, 0, len(items))
	for _, item := range items {
		if predicate(item) {
			result = append(result, item)
		}
	}
	return result
}

func Find[T any](items []T, predicate func(T) bool) (T, bool) {
	for _, item := range items {
		if predicate(item) {
			return item, true
		}
	}
	var zero T
	return zero, false
}
