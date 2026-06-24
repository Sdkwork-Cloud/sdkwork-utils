package utils

import "testing"
import "strings"

func TestStringHelpers(t *testing.T) {
	if !IsBlank(nil) {
		t.Fatal("expected blank nil")
	}
	if CamelCase("hello_world") != "helloWorld" {
		t.Fatal("unexpected camel case")
	}
	if Slugify("Hello, SDKWork!") != "hello-sdk-work" {
		t.Fatal("unexpected slug")
	}
	if Truncate("abcdef", 5, "...") != "ab..." {
		t.Fatal("unexpected truncate")
	}
}

func TestDatetimeHelpers(t *testing.T) {
	first, ok := ParseDatetime("2024-01-01T00:00:00Z", DefaultPattern)
	if !ok {
		t.Fatal("parse failed")
	}
	second := AddHours(first, 2)
	if DiffMillis(first, second) != 7_200_000 {
		t.Fatal("unexpected diff")
	}
}

func TestEncodingHelpers(t *testing.T) {
	data := []byte("hello")
	if Base64Encode(data) != "aGVsbG8=" {
		t.Fatal("unexpected base64")
	}
	if HexEncode(data) != "68656c6c6f" {
		t.Fatal("unexpected hex")
	}
}

func TestOptionalResultAndI18nHelpers(t *testing.T) {
	okValue := Coalesce(nil, ptr(""), ptr("  "), ptr("ok"))
	if okValue == nil || *okValue != "ok" {
		t.Fatal("unexpected coalesce")
	}
	if DefaultIfBlank(ptr("  "), "fallback") != "fallback" {
		t.Fatal("unexpected default_if_blank")
	}
	if UnwrapOr(Ok(42), 0) != 42 || UnwrapOr(Err[int]("fail"), 0) != 0 {
		t.Fatal("unexpected unwrap_or")
	}
	if FormatNumberLocale(1234.5, "en-US", 2) != "1,234.50" {
		t.Fatal("unexpected en-US number locale")
	}
	if FormatNumberLocale(1234.5, "de-DE", 2) != "1.234,50" {
		t.Fatal("unexpected de-DE number locale")
	}
	formatted, ok := FormatDatetimeLocaleStr("2024-06-15T14:30:00.000Z", "en-US")
	if !ok || !strings.Contains(formatted, "2024") {
		t.Fatal("unexpected datetime locale")
	}
}

func ptr(value string) *string {
	return &value
}

func TestObjectAndCryptoHelpers(t *testing.T) {
	merged := DeepMerge(
		map[string]any{"a": 1, "nested": map[string]any{"x": 1}},
		map[string]any{"b": 2, "nested": map[string]any{"y": 2}},
	).(map[string]any)
	nested := merged["nested"].(map[string]any)
	if nested["x"] != 1 || nested["y"] != 2 {
		t.Fatal("unexpected deep merge")
	}
	updated := SetPath(map[string]any{}, "user.city", "Paris")
	city, ok := GetPath(updated, "user.city")
	if !ok || city != "Paris" {
		t.Fatal("unexpected set/get path")
	}
	if Sha256Hash([]byte("hello")) != "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824" {
		t.Fatal("unexpected sha256")
	}
	if HmacSha256([]byte("payload"), []byte("secret")) != "b82fcb791acec57859b989b430a826488ce2e479fdf92326bd0a2e8375a42ba4" {
		t.Fatal("unexpected hmac")
	}
}
