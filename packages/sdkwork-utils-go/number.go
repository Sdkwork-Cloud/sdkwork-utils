package utils

import (
	"fmt"
	"math"
	"strconv"
	"strings"
)

func Clamp(value, min, max float64) float64 {
	if value < min {
		return min
	}
	if value > max {
		return max
	}
	return value
}

func Round(value float64, decimals int) float64 {
	factor := math.Pow(10, float64(decimals))
	return math.Round(value*factor) / factor
}

func FormatNumber(value float64, decimals int) string {
	return fmt.Sprintf("%.*f", decimals, value)
}

func ParseNumber(value string) (float64, bool) {
	parsed, err := strconv.ParseFloat(value, 64)
	if err != nil {
		return 0, false
	}
	return parsed, true
}

func IsInteger(value float64) bool {
	return math.IsInf(value, 0) == false && math.IsNaN(value) == false && value == math.Trunc(value)
}

func ParseInt(value string) (int64, bool) {
	parsed, err := strconv.ParseInt(strings.TrimSpace(value), 10, 64)
	if err != nil {
		return 0, false
	}
	return parsed, true
}

func PercentFormat(value float64, decimals int) string {
	return FormatNumber(value*100.0, decimals) + "%"
}

func InRange(value, min, max float64) bool {
	return value >= min && value <= max
}

func Abs(value float64) float64 {
	return math.Abs(value)
}
