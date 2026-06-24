package utils

import (
	"fmt"
	"strings"
	"time"
)

func Coalesce(values ...*string) *string {
	for _, value := range values {
		if value == nil {
			continue
		}
		if !IsBlank(value) {
			trimmed := Trim(*value)
			return &trimmed
		}
	}
	return nil
}

func DefaultIfBlank(value *string, defaultValue string) string {
	if IsBlank(value) {
		return defaultValue
	}
	return Trim(*value)
}
