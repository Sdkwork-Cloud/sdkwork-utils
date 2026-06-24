package utils

import (
	"fmt"
	"strings"
	"time"
)

type localeSeparators struct {
	decimal  string
	grouping string
}

func separators(locale string) localeSeparators {
	if strings.EqualFold(locale, "de-DE") {
		return localeSeparators{decimal: ",", grouping: "."}
	}
	return localeSeparators{decimal: ".", grouping: ","}
}

func FormatNumberLocale(value float64, locale string, decimals int) string {
	rounded := Round(value, decimals)
	negative := rounded < 0
	absolute := Round(absFloat(rounded), decimals)
	text := fmt.Sprintf("%.*f", decimals, absolute)
	parts := strings.Split(text, ".")
	integer := parts[0]
	fraction := ""
	if len(parts) > 1 {
		fraction = parts[1]
	}
	sep := separators(locale)
	grouped := insertGrouping(integer, sep.grouping)
	if decimals > 0 {
		grouped = grouped + sep.decimal + fraction
	}
	if negative {
		return "-" + grouped
	}
	return grouped
}

func FormatDatetimeLocale(value time.Time, locale string) string {
	utc := value.UTC()
	normalized := strings.ToLower(locale)
	if strings.HasPrefix(normalized, "de") {
		return utc.Format("02.01.2006 15:04")
	}
	if strings.HasPrefix(normalized, "zh") {
		return utc.Format("2006-01-02 15:04")
	}
	return utc.Format("01/02/2006 15:04")
}

func FormatDatetimeLocaleStr(value string, locale string) (string, bool) {
	parsed, ok := ParseDatetime(value, DefaultPattern)
	if !ok {
		return "", false
	}
	return FormatDatetimeLocale(parsed, locale), true
}

func ParseNumberLocale(input string, locale string) (float64, bool) {
	trimmed := Trim(input)
	if trimmed == "" {
		return 0, false
	}
	sep := separators(locale)
	normalized := strings.ReplaceAll(strings.ReplaceAll(trimmed, sep.grouping, ""), sep.decimal, ".")
	parsed, ok := ParseNumber(normalized)
	return parsed, ok
}

func insertGrouping(integer string, grouping string) string {
	if len(integer) <= 3 {
		return integer
	}
	var parts []string
	for len(integer) > 3 {
		parts = append([]string{integer[len(integer)-3:]}, parts...)
		integer = integer[:len(integer)-3]
	}
	parts = append([]string{integer}, parts...)
	return strings.Join(parts, grouping)
}

func absFloat(value float64) float64 {
	if value < 0 {
		return -value
	}
	return value
}
