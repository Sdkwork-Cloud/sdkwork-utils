package utils

import (
	"encoding/base64"
	"encoding/hex"
	"net/url"
)

func Base64Encode(value []byte) string {
	return base64.StdEncoding.EncodeToString(value)
}

func Base64Decode(value string) ([]byte, bool) {
	decoded, err := base64.StdEncoding.DecodeString(Trim(value))
	if err != nil {
		return nil, false
	}
	return decoded, true
}

func HexEncode(value []byte) string {
	return hex.EncodeToString(value)
}

func HexDecode(value string) ([]byte, bool) {
	decoded, err := hex.DecodeString(Trim(value))
	if err != nil {
		return nil, false
	}
	return decoded, true
}

func URLEncode(value string) string {
	return url.QueryEscape(value)
}

func URLDecode(value string) (string, bool) {
	decoded, err := url.QueryUnescape(value)
	if err != nil {
		return "", false
	}
	return decoded, true
}

func Base64URLEncode(value []byte) string {
	return base64.RawURLEncoding.EncodeToString(value)
}

func Base64URLDecode(value string) ([]byte, bool) {
	decoded, err := base64.RawURLEncoding.DecodeString(Trim(value))
	if err != nil {
		return nil, false
	}
	return decoded, true
}
