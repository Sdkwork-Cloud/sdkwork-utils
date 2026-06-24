package utils

import (
	"math"
	"strings"
)

type currencyMeta struct {
	exponent int
	symbol   string
}

var knownCurrencies = map[string]currencyMeta{
	"USD": {2, "$"},
	"EUR": {2, "€"},
	"GBP": {2, "£"},
	"CNY": {2, "¥"},
	"JPY": {0, "¥"},
	"KRW": {0, "₩"},
	"HKD": {2, "HK$"},
	"TWD": {2, "NT$"},
	"CHF": {2, "CHF"},
	"CAD": {2, "CA$"},
	"AUD": {2, "A$"},
	"INR": {2, "₹"},
	"BHD": {3, "BHD"},
	"KWD": {3, "KWD"},
}

func lookupCurrency(code string) (currencyMeta, bool) {
	normalized := strings.TrimSpace(code)
	if len(normalized) != 3 {
		return currencyMeta{}, false
	}
	for _, ch := range normalized {
		if ch < 'A' || ch > 'Z' {
			return currencyMeta{}, false
		}
	}
	meta, ok := knownCurrencies[normalized]
	return meta, ok
}

func IsCurrencyCode(value string) bool {
	_, ok := lookupCurrency(value)
	return ok
}

func MinorUnitExponent(code string) (int, bool) {
	meta, ok := lookupCurrency(code)
	if !ok {
		return 0, false
	}
	return meta.exponent, true
}

func ToMinorUnits(amount float64, code string) (int64, bool) {
	meta, ok := lookupCurrency(code)
	if !ok {
		return 0, false
	}
	factor := math.Pow(10, float64(meta.exponent))
	return int64(Round(amount*factor, 0)), true
}

func FromMinorUnits(minor int64, code string) (float64, bool) {
	meta, ok := lookupCurrency(code)
	if !ok {
		return 0, false
	}
	factor := math.Pow(10, float64(meta.exponent))
	return float64(minor) / factor, true
}

func suffixCurrencyLocale(locale string) bool {
	normalized := strings.ToLower(locale)
	return strings.HasPrefix(normalized, "de") || strings.HasPrefix(normalized, "fr") ||
		strings.HasPrefix(normalized, "it") || strings.HasPrefix(normalized, "es")
}

func FormatCurrency(amount float64, code string, locale string) (string, bool) {
	meta, ok := lookupCurrency(code)
	if !ok {
		return "", false
	}
	formatted := FormatNumberLocale(amount, locale, meta.exponent)
	if suffixCurrencyLocale(locale) {
		return formatted + " " + meta.symbol, true
	}
	return meta.symbol + formatted, true
}
