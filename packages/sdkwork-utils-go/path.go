package utils

import "strings"

func JoinPath(segments ...string) string {
	parts := make([]string, 0, len(segments))
	for _, segment := range segments {
		clean := strings.Trim(segment, "/")
		if clean != "" {
			parts = append(parts, clean)
		}
	}
	return strings.Join(parts, "/")
}

func NormalizePath(value string) string {
	parts := make([]string, 0)
	for _, part := range strings.Split(value, "/") {
		if part != "" {
			parts = append(parts, part)
		}
	}
	joined := strings.Join(parts, "/")
	if strings.HasPrefix(value, "/") {
		return "/" + joined
	}
	return joined
}
