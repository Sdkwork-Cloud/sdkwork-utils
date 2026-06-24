package utils

import (
	"encoding/json"
	"os"
	"path/filepath"
	"regexp"
	"runtime"
	"strings"
	"testing"
)

type extendedFixtures struct {
	String struct {
		Trim                []simpleStringCase `json:"trim"`
		Truncate            []truncateCase     `json:"truncate"`
		Capitalize          []simpleStringCase `json:"capitalize"`
		SnakeCase           []simpleStringCase `json:"snake_case"`
		KebabCase           []simpleStringCase `json:"kebab_case"`
		NormalizeWhitespace []simpleStringCase `json:"normalize_whitespace"`
		Template            []templateCase     `json:"template"`
	} `json:"string"`
	Datetime struct {
		Now            []struct{ Valid bool `json:"valid"` } `json:"now"`
		Parse          []struct {
			Input string `json:"input"`
			Valid bool   `json:"valid"`
		} `json:"parse"`
		AddDays       []datetimeDeltaCase     `json:"add_days"`
		AddHours      []datetimeDeltaCase     `json:"add_hours"`
		AddMinutes    []datetimeDeltaCase     `json:"add_minutes"`
		IsBefore      []datetimeCompareCase   `json:"is_before"`
		IsAfter       []datetimeCompareCase   `json:"is_after"`
		StartOfDayUTC []datetimeTransformCase `json:"start_of_day_utc"`
		EndOfDayUTC   []datetimeTransformCase `json:"end_of_day_utc"`
	} `json:"datetime"`
	Number struct {
		Round        []roundCase        `json:"round"`
		FormatNumber []formatNumberCase `json:"format_number"`
		ParseNumber  []parseNumberCase  `json:"parse_number"`
		IsInteger    []isIntegerCase    `json:"is_integer"`
		Abs          []absCase          `json:"abs"`
	} `json:"number"`
	Collection struct {
		Chunk   []chunkCase   `json:"chunk"`
		Flatten []flattenCase `json:"flatten"`
		Compact []compactCase `json:"compact"`
		GroupBy []groupByCase `json:"group_by"`
		SortBy  []sortByCase  `json:"sort_by"`
		KeyBy   []keyByCase   `json:"key_by"`
		Filter  []filterCase  `json:"filter"`
		Find    []findCase    `json:"find"`
	} `json:"collection"`
	Validation struct {
		IsUUID    []boolCase `json:"is_uuid"`
		IsURL     []boolCase `json:"is_url"`
		IsNumeric []boolCase `json:"is_numeric"`
		IsIPv4    []boolCase `json:"is_ipv4"`
		IsIPv6    []boolCase `json:"is_ipv6"`
	} `json:"validation"`
	ID struct {
		UUID         []struct{ Pattern string `json:"pattern"` } `json:"uuid"`
		RandomString []struct{ Length int `json:"length"` }      `json:"random_string"`
	} `json:"id"`
	Encoding struct {
		Base64Decode    []bytesStringCase  `json:"base64_decode"`
		HexDecode       []bytesStringCase  `json:"hex_decode"`
		URLEncode       []simpleStringCase `json:"url_encode"`
		URLDecode       []simpleStringCase `json:"url_decode"`
		Base64URLEncode []simpleStringCase `json:"base64url_encode"`
		Base64URLDecode []bytesStringCase  `json:"base64url_decode"`
	} `json:"encoding"`
	Path struct {
		NormalizePath []simpleStringCase `json:"normalize_path"`
	} `json:"path"`
	Object struct {
		Pick    []pickCase          `json:"pick"`
		Omit    []pickCase          `json:"omit"`
		Compact []compactObjectCase `json:"compact"`
		Keys    []keysCase          `json:"keys"`
		Values  []valuesCase        `json:"values"`
	} `json:"object"`
	Optional struct {
		DefaultIfBlank []defaultIfBlankCase `json:"default_if_blank"`
	} `json:"optional"`
	Result struct {
		Err   []resultErrCase  `json:"err"`
		IsOk  []resultKindCase `json:"is_ok"`
		IsErr []resultKindCase `json:"is_err"`
		Map   []resultMapCase  `json:"map"`
	} `json:"result"`
	I18n struct {
		FormatDatetimeLocale []formatDatetimeLocaleCase `json:"format_datetime_locale"`
	} `json:"i18n"`
	Bytes struct {
		FormatBytes []formatBytesCase `json:"format_bytes"`
	} `json:"bytes"`
}

type simpleStringCase struct {
	Input  string `json:"input"`
	Output string `json:"output"`
}

type truncateCase struct {
	Input  string `json:"input"`
	MaxLen int    `json:"max_len"`
	Suffix string `json:"suffix"`
	Output string `json:"output"`
}

type templateCase struct {
	Template string            `json:"template"`
	Values   map[string]string `json:"values"`
	Output   string            `json:"output"`
}

type datetimeDeltaCase struct {
	Input   string `json:"input"`
	Days    int    `json:"days,omitempty"`
	Hours   int    `json:"hours,omitempty"`
	Minutes int    `json:"minutes,omitempty"`
	Output  string `json:"output"`
}

type datetimeCompareCase struct {
	Left   string `json:"left"`
	Right  string `json:"right"`
	Output bool   `json:"output"`
}

type datetimeTransformCase struct {
	Input  string `json:"input"`
	Output string `json:"output"`
}

type roundCase struct {
	Value    float64 `json:"value"`
	Decimals int     `json:"decimals"`
	Output   float64 `json:"output"`
}

type formatNumberCase struct {
	Value    float64 `json:"value"`
	Decimals int     `json:"decimals"`
	Output   string  `json:"output"`
}

type parseNumberCase struct {
	Input  string   `json:"input"`
	Output *float64 `json:"output"`
}

type isIntegerCase struct {
	Value  float64 `json:"value"`
	Output bool    `json:"output"`
}

type absCase struct {
	Input  float64 `json:"input"`
	Output float64 `json:"output"`
}

type chunkCase struct {
	Input  []int   `json:"input"`
	Size   int     `json:"size"`
	Output [][]int `json:"output"`
}

type flattenCase struct {
	Input  [][]int `json:"input"`
	Output []int   `json:"output"`
}

type compactCase struct {
	Input  []any `json:"input"`
	Output []any `json:"output"`
}

type groupByCase struct {
	Input  []map[string]any          `json:"input"`
	Output map[string][]map[string]any `json:"output"`
}

type sortByCase struct {
	Input  []map[string]any `json:"input"`
	Output []map[string]any `json:"output"`
}

type keyByCase struct {
	Input  []map[string]any          `json:"input"`
	Output map[string]map[string]any `json:"output"`
}

type filterCase struct {
	Input     []int `json:"input"`
	Threshold int   `json:"threshold"`
	Output    []int `json:"output"`
}

type findCase struct {
	Input     []int `json:"input"`
	Threshold int   `json:"threshold"`
	Output    int   `json:"output"`
}

type boolCase struct {
	Input  string `json:"input"`
	Output bool   `json:"output"`
}

type bytesStringCase struct {
	Input  string `json:"input"`
	Output string `json:"output"`
}

type pickCase struct {
	Source map[string]any `json:"source"`
	Keys   []string       `json:"keys"`
	Output map[string]any `json:"output"`
}

type compactObjectCase struct {
	Input  map[string]any `json:"input"`
	Output map[string]any `json:"output"`
}

type keysCase struct {
	Input  map[string]any `json:"input"`
	Output []string       `json:"output"`
}

type valuesCase struct {
	Input  map[string]any `json:"input"`
	Output []any          `json:"output"`
}

type defaultIfBlankCase struct {
	Input   string `json:"input"`
	Default string `json:"default"`
	Output  string `json:"output"`
}

type resultErrCase struct {
	Message string `json:"message"`
	IsOk    bool   `json:"is_ok"`
	IsErr   bool   `json:"is_err"`
}

type resultKindCase struct {
	Kind    string `json:"kind"`
	Value   int    `json:"value,omitempty"`
	Message string `json:"message,omitempty"`
	Output  bool   `json:"output"`
}

type resultMapCase struct {
	Value  int `json:"value"`
	Output int `json:"output"`
}

type formatDatetimeLocaleCase struct {
	Input    string `json:"input"`
	Locale   string `json:"locale"`
	Contains string `json:"contains"`
}

type formatBytesCase struct {
	Value    int64  `json:"value"`
	Decimals int    `json:"decimals"`
	Output   string `json:"output"`
}

func loadExtendedFixtures(t *testing.T) extendedFixtures {
	t.Helper()
	_, filename, _, ok := runtime.Caller(0)
	if !ok {
		t.Fatal("runtime caller failed")
	}
	path := filepath.Join(filepath.Dir(filename), "..", "..", "specs", "conformance", "fixtures.json")
	bytes, err := os.ReadFile(path)
	if err != nil {
		t.Fatal(err)
	}
	var data extendedFixtures
	if err := json.Unmarshal(bytes, &data); err != nil {
		t.Fatal(err)
	}
	return data
}

func assertDeepEqualJSON(t *testing.T, expected, actual any) {
	t.Helper()
	if !jsonEqual(expected, actual) {
		expectedJSON, _ := json.Marshal(expected)
		actualJSON, _ := json.Marshal(actual)
		t.Fatalf("json mismatch expected %s got %s", expectedJSON, actualJSON)
	}
}

func runExtendedConformance(t *testing.T, fixtures extendedFixtures) {
	for _, item := range fixtures.String.Trim {
		if Trim(item.Input) != item.Output {
			t.Fatal("trim mismatch")
		}
	}
	for _, item := range fixtures.String.Truncate {
		if Truncate(item.Input, item.MaxLen, item.Suffix) != item.Output {
			t.Fatal("truncate mismatch")
		}
	}
	for _, item := range fixtures.String.Capitalize {
		if Capitalize(item.Input) != item.Output {
			t.Fatal("capitalize mismatch")
		}
	}
	for _, item := range fixtures.String.SnakeCase {
		if SnakeCase(item.Input) != item.Output {
			t.Fatal("snake_case mismatch")
		}
	}
	for _, item := range fixtures.String.KebabCase {
		if KebabCase(item.Input) != item.Output {
			t.Fatal("kebab_case mismatch")
		}
	}
	for _, item := range fixtures.String.NormalizeWhitespace {
		if NormalizeWhitespace(item.Input) != item.Output {
			t.Fatal("normalize_whitespace mismatch")
		}
	}
	for _, item := range fixtures.String.Template {
		if Template(item.Template, item.Values) != item.Output {
			t.Fatal("template mismatch")
		}
	}

	for range fixtures.Datetime.Now {
		if Now().IsZero() {
			t.Fatal("now mismatch")
		}
	}
	for _, item := range fixtures.Datetime.Parse {
		_, ok := ParseDatetime(item.Input, DefaultPattern)
		if ok != item.Valid {
			t.Fatal("parse mismatch")
		}
	}
	for _, item := range fixtures.Datetime.AddDays {
		parsed, ok := ParseDatetime(item.Input, DefaultPattern)
		if !ok {
			t.Fatal("add_days parse failed")
		}
		if FormatDatetime(AddDays(parsed, item.Days), DefaultPattern) != item.Output {
			t.Fatal("add_days mismatch")
		}
	}
	for _, item := range fixtures.Datetime.AddHours {
		parsed, ok := ParseDatetime(item.Input, DefaultPattern)
		if !ok {
			t.Fatal("add_hours parse failed")
		}
		if FormatDatetime(AddHours(parsed, item.Hours), DefaultPattern) != item.Output {
			t.Fatal("add_hours mismatch")
		}
	}
	for _, item := range fixtures.Datetime.AddMinutes {
		parsed, ok := ParseDatetime(item.Input, DefaultPattern)
		if !ok {
			t.Fatal("add_minutes parse failed")
		}
		if FormatDatetime(AddMinutes(parsed, item.Minutes), DefaultPattern) != item.Output {
			t.Fatal("add_minutes mismatch")
		}
	}
	for _, item := range fixtures.Datetime.IsBefore {
		left, _ := ParseDatetime(item.Left, DefaultPattern)
		right, _ := ParseDatetime(item.Right, DefaultPattern)
		if IsBefore(left, right) != item.Output {
			t.Fatal("is_before mismatch")
		}
	}
	for _, item := range fixtures.Datetime.IsAfter {
		left, _ := ParseDatetime(item.Left, DefaultPattern)
		right, _ := ParseDatetime(item.Right, DefaultPattern)
		if IsAfter(left, right) != item.Output {
			t.Fatal("is_after mismatch")
		}
	}
	for _, item := range fixtures.Datetime.StartOfDayUTC {
		parsed, _ := ParseDatetime(item.Input, DefaultPattern)
		if FormatDatetime(StartOfDayUTC(parsed), DefaultPattern) != item.Output {
			t.Fatal("start_of_day_utc mismatch")
		}
	}
	for _, item := range fixtures.Datetime.EndOfDayUTC {
		parsed, _ := ParseDatetime(item.Input, DefaultPattern)
		if FormatDatetime(EndOfDayUTC(parsed), DefaultPattern) != item.Output {
			t.Fatal("end_of_day_utc mismatch")
		}
	}

	for _, item := range fixtures.Number.Round {
		if Round(item.Value, item.Decimals) != item.Output {
			t.Fatal("round mismatch")
		}
	}
	for _, item := range fixtures.Number.FormatNumber {
		if FormatNumber(item.Value, item.Decimals) != item.Output {
			t.Fatal("format_number mismatch")
		}
	}
	for _, item := range fixtures.Number.ParseNumber {
		parsed, ok := ParseNumber(item.Input)
		if item.Output == nil {
			if ok {
				t.Fatal("parse_number expected null")
			}
			continue
		}
		if !ok || parsed != *item.Output {
			t.Fatal("parse_number mismatch")
		}
	}
	for _, item := range fixtures.Number.IsInteger {
		if IsInteger(item.Value) != item.Output {
			t.Fatal("is_integer mismatch")
		}
	}
	for _, item := range fixtures.Number.Abs {
		if Abs(item.Input) != item.Output {
			t.Fatal("abs mismatch")
		}
	}

	for _, item := range fixtures.Collection.Chunk {
		if !jsonEqual(Chunk(item.Input, item.Size), item.Output) {
			t.Fatal("chunk mismatch")
		}
	}
	for _, item := range fixtures.Collection.Flatten {
		if !jsonEqual(Flatten(item.Input), item.Output) {
			t.Fatal("flatten mismatch")
		}
	}
	for _, item := range fixtures.Collection.Compact {
		if !jsonEqual(CompactAny(item.Input), item.Output) {
			t.Fatal("compact mismatch")
		}
	}
	for _, item := range fixtures.Collection.GroupBy {
		grouped := GroupBy(item.Input, func(entry map[string]any) string {
			return entry["type"].(string)
		})
		assertDeepEqualJSON(t, item.Output, grouped)
	}
	for _, item := range fixtures.Collection.SortBy {
		sorted := SortBy(item.Input, func(entry map[string]any) string {
			return entry["k"].(string)
		})
		assertDeepEqualJSON(t, item.Output, sorted)
	}
	for _, item := range fixtures.Collection.KeyBy {
		keyed := KeyBy(item.Input, func(entry map[string]any) string {
			return entry["id"].(string)
		})
		assertDeepEqualJSON(t, item.Output, keyed)
	}
	for _, item := range fixtures.Collection.Filter {
		filtered := Filter(item.Input, func(value int) bool { return value > item.Threshold })
		if !jsonEqual(filtered, item.Output) {
			t.Fatal("filter mismatch")
		}
	}
	for _, item := range fixtures.Collection.Find {
		found, ok := Find(item.Input, func(value int) bool { return value > item.Threshold })
		if !ok || found != item.Output {
			t.Fatal("find mismatch")
		}
	}

	for _, item := range fixtures.Validation.IsUUID {
		if IsUUID(item.Input) != item.Output {
			t.Fatal("is_uuid mismatch")
		}
	}
	for _, item := range fixtures.Validation.IsURL {
		if IsURL(item.Input) != item.Output {
			t.Fatal("is_url mismatch")
		}
	}
	for _, item := range fixtures.Validation.IsNumeric {
		if IsNumeric(item.Input) != item.Output {
			t.Fatal("is_numeric mismatch")
		}
	}
	for _, item := range fixtures.Validation.IsIPv4 {
		if IsIPv4(item.Input) != item.Output {
			t.Fatal("is_ipv4 mismatch")
		}
	}
	for _, item := range fixtures.Validation.IsIPv6 {
		if IsIPv6(item.Input) != item.Output {
			t.Fatal("is_ipv6 mismatch")
		}
	}

	uuidPattern := regexp.MustCompile(`(?i)^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$`)
	for _, item := range fixtures.ID.UUID {
		if !uuidPattern.MatchString(UUID()) {
			t.Fatal("uuid mismatch")
		}
		if item.Pattern != "uuid-v4" {
			t.Fatal("uuid pattern mismatch")
		}
	}
	randomPattern := regexp.MustCompile(`^[A-Za-z0-9]+$`)
	for _, item := range fixtures.ID.RandomString {
		value := RandomString(item.Length)
		if len(value) != item.Length || !randomPattern.MatchString(value) {
			t.Fatal("random_string mismatch")
		}
	}

	for _, item := range fixtures.Encoding.Base64Decode {
		decoded, ok := Base64Decode(item.Input)
		if !ok || string(decoded) != item.Output {
			t.Fatal("base64_decode mismatch")
		}
	}
	for _, item := range fixtures.Encoding.HexDecode {
		decoded, ok := HexDecode(item.Input)
		if !ok || string(decoded) != item.Output {
			t.Fatal("hex_decode mismatch")
		}
	}
	for _, item := range fixtures.Encoding.URLEncode {
		if URLEncode(item.Input) != item.Output {
			t.Fatal("url_encode mismatch")
		}
	}
	for _, item := range fixtures.Encoding.URLDecode {
		decoded, ok := URLDecode(item.Input)
		if !ok || decoded != item.Output {
			t.Fatal("url_decode mismatch")
		}
	}
	for _, item := range fixtures.Encoding.Base64URLEncode {
		if Base64URLEncode([]byte(item.Input)) != item.Output {
			t.Fatal("base64url_encode mismatch")
		}
	}
	for _, item := range fixtures.Encoding.Base64URLDecode {
		decoded, ok := Base64URLDecode(item.Input)
		if !ok || string(decoded) != item.Output {
			t.Fatal("base64url_decode mismatch")
		}
	}

	for _, item := range fixtures.Path.NormalizePath {
		if NormalizePath(item.Input) != item.Output {
			t.Fatal("normalize_path mismatch")
		}
	}

	for _, item := range fixtures.Object.Pick {
		assertDeepEqualJSON(t, item.Output, Pick(item.Source, item.Keys))
	}
	for _, item := range fixtures.Object.Omit {
		assertDeepEqualJSON(t, item.Output, Omit(item.Source, item.Keys))
	}
	for _, item := range fixtures.Object.Compact {
		assertDeepEqualJSON(t, item.Output, CompactMap(item.Input))
	}
	for _, item := range fixtures.Object.Keys {
		assertDeepEqualJSON(t, item.Output, Keys(item.Input))
	}
	for _, item := range fixtures.Object.Values {
		assertDeepEqualJSON(t, item.Output, Values(item.Input))
	}

	for _, item := range fixtures.Optional.DefaultIfBlank {
		input := item.Input
		if DefaultIfBlank(&input, item.Default) != item.Output {
			t.Fatal("default_if_blank mismatch")
		}
	}

	for _, item := range fixtures.Result.Err {
		result := Err[int](item.Message)
		if IsOk(result) != item.IsOk || IsErr(result) != item.IsErr {
			t.Fatal("err mismatch")
		}
	}
	for _, item := range fixtures.Result.IsOk {
		var result ResultValue[int]
		if item.Kind == "ok" {
			result = Ok(item.Value)
		} else {
			result = Err[int](item.Message)
		}
		if IsOk(result) != item.Output {
			t.Fatal("is_ok mismatch")
		}
	}
	for _, item := range fixtures.Result.IsErr {
		var result ResultValue[int]
		if item.Kind == "ok" {
			result = Ok(item.Value)
		} else {
			result = Err[int](item.Message)
		}
		if IsErr(result) != item.Output {
			t.Fatal("is_err mismatch")
		}
	}
	for _, item := range fixtures.Result.Map {
		mapped := Map(Ok(item.Value), func(value int) int { return value * 2 })
		if mapped.Value != item.Output {
			t.Fatal("map mismatch")
		}
	}

	for _, item := range fixtures.I18n.FormatDatetimeLocale {
		formatted, ok := FormatDatetimeLocaleStr(item.Input, item.Locale)
		if !ok || !strings.Contains(formatted, item.Contains) {
			t.Fatal("format_datetime_locale mismatch")
		}
	}

	for _, item := range fixtures.Bytes.FormatBytes {
		if FormatBytes(item.Value, item.Decimals) != item.Output {
			t.Fatal("format_bytes mismatch")
		}
	}
}

func jsonEqual(left, right any) bool {
	leftJSON, err := json.Marshal(left)
	if err != nil {
		return false
	}
	rightJSON, err := json.Marshal(right)
	if err != nil {
		return false
	}
	return string(leftJSON) == string(rightJSON)
}
