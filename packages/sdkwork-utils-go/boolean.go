package utils

import "strings"

func ParseBool(value string) (*bool, bool) {
	switch strings.ToLower(strings.TrimSpace(value)) {
	case "true", "1", "yes", "on":
		result := true
		return &result, true
	case "false", "0", "no", "off":
		result := false
		return &result, true
	default:
		return nil, false
	}
}

func IsTruthy(value *string) bool {
	if value == nil {
		return false
	}
	trimmed := strings.TrimSpace(*value)
	if trimmed == "" {
		return false
	}
	switch strings.ToLower(trimmed) {
	case "false", "0", "no", "off":
		return false
	default:
		return true
	}
}
