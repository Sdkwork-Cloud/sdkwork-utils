package utils

import (
	"fmt"
	"math"
)

var byteUnits = []string{"B", "KB", "MB", "GB", "TB", "PB"}

func FormatBytes(value int64, decimals int) string {
	bytes := value
	if bytes < 0 {
		bytes = 0
	}
	if bytes < 1024 {
		return fmt.Sprintf("%d B", bytes)
	}

	size := float64(bytes)
	unitIndex := 0
	for size >= 1024 && unitIndex < len(byteUnits)-1 {
		size /= 1024
		unitIndex++
	}

	format := fmt.Sprintf("%%.%df %s", decimals, byteUnits[unitIndex])
	return fmt.Sprintf(format, size)
}
