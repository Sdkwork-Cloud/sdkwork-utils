package utils

import (
	"crypto/rand"
	"fmt"
)

const alphanumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

func UUID() string {
	bytes := make([]byte, 16)
	_, _ = rand.Read(bytes)
	bytes[6] = (bytes[6] & 0x0f) | 0x40
	bytes[8] = (bytes[8] & 0x3f) | 0x80
	return fmt.Sprintf(
		"%x-%x-%x-%x-%x",
		bytes[0:4],
		bytes[4:6],
		bytes[6:8],
		bytes[8:10],
		bytes[10:16],
	)
}

func RandomString(length int) string {
	bytes := make([]byte, length)
	_, _ = rand.Read(bytes)
	result := make([]byte, length)
	for index := range result {
		result[index] = alphanumeric[int(bytes[index])%len(alphanumeric)]
	}
	return string(result)
}
