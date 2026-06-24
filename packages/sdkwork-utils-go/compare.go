package utils

import "reflect"

func DeepEqual(left, right any) bool {
	return deepEqualJSON(left, right)
}

func deepEqualJSON(left, right any) bool {
	if left == nil || right == nil {
		return left == right
	}

	switch leftValue := left.(type) {
	case map[string]any:
		rightValue, ok := right.(map[string]any)
		if !ok || len(leftValue) != len(rightValue) {
			return false
		}
		for key, value := range leftValue {
			other, exists := rightValue[key]
			if !exists || !deepEqualJSON(value, other) {
				return false
			}
		}
		return true
	case []any:
		rightValue, ok := right.([]any)
		if !ok || len(leftValue) != len(rightValue) {
			return false
		}
		for index := range leftValue {
			if !deepEqualJSON(leftValue[index], rightValue[index]) {
				return false
			}
		}
		return true
	default:
		return reflect.DeepEqual(left, right)
	}
}

func DeepEqualJSON(left, right any) bool {
	return deepEqualJSON(left, right)
}

func DeepClone(value any) any {
	switch typed := value.(type) {
	case map[string]any:
		cloned := make(map[string]any, len(typed))
		for key, nested := range typed {
			cloned[key] = DeepClone(nested)
		}
		return cloned
	case []any:
		cloned := make([]any, len(typed))
		for index, nested := range typed {
			cloned[index] = DeepClone(nested)
		}
		return cloned
	default:
		return value
	}
}
