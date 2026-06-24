package utils

import (
	"regexp"
	"strings"
	"unicode"
)

var wordSplit = regexp.MustCompile(`[^a-zA-Z0-9]+`)
var camelBoundary = regexp.MustCompile(`([a-z0-9])([A-Z])|([A-Z]+)([A-Z][a-z])`)

func camelParts(input string) []string {
	normalized := camelBoundary.ReplaceAllString(strings.TrimSpace(input), "${1}${3} ${2}${4}")
	parts := wordSplit.Split(normalized, -1)
	result := make([]string, 0, len(parts))
	for _, part := range parts {
		if part != "" {
			result = append(result, strings.ToLower(part))
		}
	}
	return result
}

func IsBlank(value *string) bool {
	if value == nil {
		return true
	}
	return strings.TrimSpace(*value) == ""
}

func Trim(value string) string {
	return strings.TrimSpace(value)
}

func Truncate(value string, maxLen int, suffix string) string {
	if maxLen <= 0 {
		return ""
	}
	if suffix == "" {
		suffix = "..."
	}
	runes := []rune(value)
	if len(runes) <= maxLen {
		return value
	}
	suffixRunes := []rune(suffix)
	if len(suffixRunes) >= maxLen {
		return string(suffixRunes[:maxLen])
	}
	return string(runes[:maxLen-len(suffixRunes)]) + suffix
}

func Capitalize(value string) string {
	if value == "" {
		return ""
	}
	runes := []rune(value)
	runes[0] = unicode.ToUpper(runes[0])
	for index := 1; index < len(runes); index++ {
		runes[index] = unicode.ToLower(runes[index])
	}
	return string(runes)
}

func CamelCase(value string) string {
	parts := camelParts(value)
	if len(parts) == 0 {
		return ""
	}
	result := parts[0]
	for _, part := range parts[1:] {
		result += Capitalize(part)
	}
	return result
}

func SnakeCase(value string) string {
	return strings.Join(camelParts(value), "_")
}

func KebabCase(value string) string {
	return strings.Join(camelParts(value), "-")
}

func Slugify(value string) string {
	slug := KebabCase(value)
	var builder strings.Builder
	for _, ch := range slug {
		if (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch == '-' {
			builder.WriteRune(ch)
		}
	}
	return strings.Trim(builder.String(), "-")
}

func Mask(value string, visibleStart, visibleEnd int, maskChar rune) string {
	if maskChar == 0 {
		maskChar = '*'
	}
	runes := []rune(value)
	if visibleStart+visibleEnd >= len(runes) {
		return value
	}
	var builder strings.Builder
	for index, ch := range runes {
		if index < visibleStart || index >= len(runes)-visibleEnd {
			builder.WriteRune(ch)
		} else {
			builder.WriteRune(maskChar)
		}
	}
	return builder.String()
}

func PadStart(value string, targetLen int, padChar rune) string {
	if padChar == 0 {
		padChar = ' '
	}
	runes := []rune(value)
	if len(runes) >= targetLen {
		return value
	}
	return strings.Repeat(string(padChar), targetLen-len(runes)) + value
}

func PadEnd(value string, targetLen int, padChar rune) string {
	if padChar == 0 {
		padChar = ' '
	}
	runes := []rune(value)
	if len(runes) >= targetLen {
		return value
	}
	return value + strings.Repeat(string(padChar), targetLen-len(runes))
}

func StartsWith(value, prefix string) bool {
	return strings.HasPrefix(value, prefix)
}

func EndsWith(value, suffix string) bool {
	return strings.HasSuffix(value, suffix)
}

func Contains(value, substring string) bool {
	return strings.Contains(value, substring)
}

func ReplaceAll(value, search, replacement string) string {
	return strings.ReplaceAll(value, search, replacement)
}

func Split(value, delimiter string, trimParts bool) []string {
	parts := strings.Split(value, delimiter)
	if !trimParts {
		return parts
	}
	result := make([]string, 0, len(parts))
	for _, part := range parts {
		trimmed := strings.TrimSpace(part)
		if trimmed != "" {
			result = append(result, trimmed)
		}
	}
	return result
}

func Join(parts []string, separator string) string {
	return strings.Join(parts, separator)
}

func Repeat(value string, count int) string {
	if count < 0 {
		panic("repeat count must be >= 0")
	}
	return strings.Repeat(value, count)
}

func NormalizeWhitespace(value string) string {
	return strings.Join(strings.Fields(strings.TrimSpace(value)), " ")
}

var templateKeyPattern = regexp.MustCompile(`\{([a-zA-Z_][a-zA-Z0-9_]*)\}`)

func Template(pattern string, values map[string]string) string {
	return templateKeyPattern.ReplaceAllStringFunc(pattern, func(match string) string {
		submatches := templateKeyPattern.FindStringSubmatch(match)
		if len(submatches) < 2 {
			return match
		}
		if value, ok := values[submatches[1]]; ok {
			return value
		}
		return match
	})
}
