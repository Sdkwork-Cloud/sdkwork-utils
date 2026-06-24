package utils

import (
	"encoding/json"
	"os"
	"path/filepath"
	"runtime"
	"testing"
)

type fixtures struct {
	String struct {
		IsBlank    []json.RawMessage `json:"is_blank"`
		CamelCase  []struct {
			Input  string `json:"input"`
			Output string `json:"output"`
		} `json:"camel_case"`
		Slugify []struct {
			Input  string `json:"input"`
			Output string `json:"output"`
		} `json:"slugify"`
		Mask []struct {
			Input        string `json:"input"`
			VisibleStart int    `json:"visible_start"`
			VisibleEnd   int    `json:"visible_end"`
			Output       string `json:"output"`
		} `json:"mask"`
		PadStart []struct {
			Input     string `json:"input"`
			TargetLen int    `json:"target_len"`
			Output    string `json:"output"`
		} `json:"pad_start"`
		PadEnd []struct {
			Input     string `json:"input"`
			TargetLen int    `json:"target_len"`
			Output    string `json:"output"`
		} `json:"pad_end"`
		StartsWith []struct {
			Input  string `json:"input"`
			Prefix string `json:"prefix"`
			Output bool   `json:"output"`
		} `json:"starts_with"`
		EndsWith []struct {
			Input  string `json:"input"`
			Suffix string `json:"suffix"`
			Output bool   `json:"output"`
		} `json:"ends_with"`
		Contains []struct {
			Input      string `json:"input"`
			Substring  string `json:"substring"`
			Output     bool   `json:"output"`
		} `json:"contains"`
		ReplaceAll []struct {
			Input       string `json:"input"`
			Search      string `json:"search"`
			Replacement string `json:"replacement"`
			Output      string `json:"output"`
		} `json:"replace_all"`
		Split []struct {
			Input     string   `json:"input"`
			Delimiter string   `json:"delimiter"`
			TrimParts bool     `json:"trim_parts"`
			Output    []string `json:"output"`
		} `json:"split"`
        Join []struct {
            Parts     []string `json:"parts"`
            Separator string   `json:"separator"`
            Output    string   `json:"output"`
        } `json:"join"`
        Repeat json.RawMessage `json:"repeat"`
    } `json:"string"`
	Datetime struct {
		DiffMillis struct {
			Earlier string `json:"earlier"`
			Later   string `json:"later"`
			Output  int64  `json:"output"`
		} `json:"diff_millis"`
		ToUnixMillis []struct {
			Input  string `json:"input"`
			Output int64  `json:"output"`
		} `json:"to_unix_millis"`
		FromUnixMillis []struct {
			Input  int64  `json:"input"`
			Output string `json:"output"`
		} `json:"from_unix_millis"`
	} `json:"datetime"`
	Encoding struct {
		Base64Encode []struct {
			Input  string `json:"input"`
			Output string `json:"output"`
		} `json:"base64_encode"`
		HexEncode []struct {
			Input  string `json:"input"`
			Output string `json:"output"`
		} `json:"hex_encode"`
		URLEncode []struct {
			Input  string `json:"input"`
			Output string `json:"output"`
		} `json:"url_encode"`
		URLDecode []struct {
			Input  string `json:"input"`
			Output string `json:"output"`
		} `json:"url_decode"`
		Base64URLEncode []struct {
			Input  string `json:"input"`
			Output string `json:"output"`
		} `json:"base64url_encode"`
		Base64URLDecode []struct {
			Input  string `json:"input"`
			Output string `json:"output"`
		} `json:"base64url_decode"`
	} `json:"encoding"`
	Object struct {
		DeepMerge struct {
			Base    map[string]any `json:"base"`
			Overlay map[string]any `json:"overlay"`
			Output  map[string]any `json:"output"`
		} `json:"deep_merge"`
		ShallowMerge struct {
			Base    map[string]any `json:"base"`
			Overlay map[string]any `json:"overlay"`
			Output  map[string]any `json:"output"`
		} `json:"shallow_merge"`
		SetGetPath struct {
			Path   string `json:"path"`
			Value  string `json:"value"`
			Output string `json:"output"`
		} `json:"set_get_path"`
		HasPath []struct {
			Path   string `json:"path"`
			Exists bool   `json:"exists"`
		} `json:"has_path"`
	} `json:"object"`
	Crypto struct {
		Sha256Hash []struct {
			Input  string `json:"input"`
			Output string `json:"output"`
		} `json:"sha256_hash"`
		HmacSha256 []struct {
			Input  string `json:"input"`
			Secret string `json:"secret"`
			Output string `json:"output"`
		} `json:"hmac_sha256"`
		SecureCompare []struct {
			Left   string `json:"left"`
			Right  string `json:"right"`
			Output bool   `json:"output"`
		} `json:"secure_compare"`
	} `json:"crypto"`
	I18n struct {
		FormatNumberLocale []struct {
			Value    float64 `json:"value"`
			Locale   string  `json:"locale"`
			Decimals int     `json:"decimals"`
			Output   string  `json:"output"`
		} `json:"format_number_locale"`
		ParseNumberLocale []struct {
			Input  string   `json:"input"`
			Locale string   `json:"locale"`
			Output *float64 `json:"output"`
		} `json:"parse_number_locale"`
	} `json:"i18n"`
	Optional struct {
		Coalesce []struct {
			Output string `json:"output"`
		} `json:"coalesce"`
	} `json:"optional"`
	Result struct {
		UnwrapOr []struct {
			Kind    string `json:"kind"`
			Value   int    `json:"value"`
			Message string `json:"message"`
			Default int    `json:"default"`
			Output  int    `json:"output"`
		} `json:"unwrap_or"`
	} `json:"result"`
	Number struct {
		Clamp []struct {
			Value  float64 `json:"value"`
			Min    float64 `json:"min"`
			Max    float64 `json:"max"`
			Output float64 `json:"output"`
		} `json:"clamp"`
		ParseInt []struct {
			Input  string `json:"input"`
			Output *int64 `json:"output"`
		} `json:"parse_int"`
		PercentFormat []struct {
			Value    float64 `json:"value"`
			Decimals int     `json:"decimals"`
			Output   string  `json:"output"`
		} `json:"percent_format"`
	} `json:"number"`
	Collection struct {
		Unique []struct {
			Input  []int `json:"input"`
			Output []int `json:"output"`
		} `json:"unique"`
		First []struct {
			Input  []int `json:"input"`
			Output *int  `json:"output"`
		} `json:"first"`
		Last []struct {
			Input  []int `json:"input"`
			Output *int  `json:"output"`
		} `json:"last"`
	} `json:"collection"`
	Validation struct {
		IsEmail []struct {
			Input  string `json:"input"`
			Output bool   `json:"output"`
		} `json:"is_email"`
		IsIPv4 []struct {
			Input  string `json:"input"`
			Output bool   `json:"output"`
		} `json:"is_ipv4"`
		IsIPv6 []struct {
			Input  string `json:"input"`
			Output bool   `json:"output"`
		} `json:"is_ipv6"`
		IsPhoneE164 []struct {
			Input  string `json:"input"`
			Output bool   `json:"output"`
		} `json:"is_phone_e164"`
	} `json:"validation"`
	Path struct {
		JoinPath []struct {
			Segments []string `json:"segments"`
			Output   string   `json:"output"`
		} `json:"join_path"`
	} `json:"path"`
	Boolean struct {
		ParseBool []struct {
			Input  string `json:"input"`
			Output *bool  `json:"output"`
		} `json:"parse_bool"`
		IsTruthy []struct {
			Input  string `json:"input"`
			Output bool   `json:"output"`
		} `json:"is_truthy"`
	} `json:"boolean"`
	Bloom struct {
		Create []struct {
			ExpectedItems      int     `json:"expected_items"`
			FalsePositiveRate  float64 `json:"false_positive_rate"`
			BitCount           int     `json:"bit_count"`
			HashCount          int     `json:"hash_count"`
		} `json:"create"`
		EstimateBitCount []struct {
			ExpectedItems     int     `json:"expected_items"`
			FalsePositiveRate float64 `json:"false_positive_rate"`
			Output            int     `json:"output"`
		} `json:"estimate_bit_count"`
		EstimateHashCount []struct {
			ExpectedItems int `json:"expected_items"`
			BitCount      int `json:"bit_count"`
			Output        int `json:"output"`
		} `json:"estimate_hash_count"`
		MightContain []struct {
			Added         []string `json:"added"`
			Present       string   `json:"present"`
			Absent        string   `json:"absent"`
			PresentOutput bool     `json:"present_output"`
			AbsentOutput  bool     `json:"absent_output"`
		} `json:"might_contain"`
	} `json:"bloom"`
	Currency struct {
		IsCurrencyCode []struct {
			Input  string `json:"input"`
			Output bool   `json:"output"`
		} `json:"is_currency_code"`
		MinorUnitExponent []struct {
			Code   string `json:"code"`
			Output *int   `json:"output"`
		} `json:"minor_unit_exponent"`
		ToMinorUnits []struct {
			Amount float64 `json:"amount"`
			Code   string  `json:"code"`
			Output int64   `json:"output"`
		} `json:"to_minor_units"`
		FromMinorUnits []struct {
			Minor  int64   `json:"minor"`
			Code   string  `json:"code"`
			Output float64 `json:"output"`
		} `json:"from_minor_units"`
		FormatCurrency []struct {
			Amount float64 `json:"amount"`
			Code   string  `json:"code"`
			Locale string  `json:"locale"`
			Output string  `json:"output"`
		} `json:"format_currency"`
	} `json:"currency"`
}

func loadFixtures(t *testing.T) fixtures {
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
	var data fixtures
	if err := json.Unmarshal(bytes, &data); err != nil {
		t.Fatal(err)
	}
	return data
}

func TestConformanceFixtures(t *testing.T) {
	fixtures := loadFixtures(t)

	for _, raw := range fixtures.String.IsBlank {
		var item struct {
			Input  *string `json:"input"`
			Output bool    `json:"output"`
		}
		if err := json.Unmarshal(raw, &item); err != nil {
			t.Fatal(err)
		}
		if IsBlank(item.Input) != item.Output {
			t.Fatalf("is_blank mismatch for %#v", item.Input)
		}
	}
	for _, item := range fixtures.String.CamelCase {
		if CamelCase(item.Input) != item.Output {
			t.Fatal("camel_case mismatch")
		}
	}
	slug := fixtures.String.Slugify[0]
	if Slugify(slug.Input) != slug.Output {
		t.Fatal("slugify mismatch")
	}
	for _, item := range fixtures.String.Mask {
		if Mask(item.Input, item.VisibleStart, item.VisibleEnd, 0) != item.Output {
			t.Fatal("mask mismatch")
		}
	}
	for _, item := range fixtures.String.PadStart {
		if PadStart(item.Input, item.TargetLen, 0) != item.Output {
			t.Fatal("pad_start mismatch")
		}
	}
	for _, item := range fixtures.String.PadEnd {
		if PadEnd(item.Input, item.TargetLen, 0) != item.Output {
			t.Fatal("pad_end mismatch")
		}
	}
	for _, item := range fixtures.String.StartsWith {
		if StartsWith(item.Input, item.Prefix) != item.Output {
			t.Fatal("starts_with mismatch")
		}
	}
	for _, item := range fixtures.String.EndsWith {
		if EndsWith(item.Input, item.Suffix) != item.Output {
			t.Fatal("ends_with mismatch")
		}
	}
	for _, item := range fixtures.String.Contains {
		if Contains(item.Input, item.Substring) != item.Output {
			t.Fatal("contains mismatch")
		}
	}
	for _, item := range fixtures.String.ReplaceAll {
		if ReplaceAll(item.Input, item.Search, item.Replacement) != item.Output {
			t.Fatal("replace_all mismatch")
		}
	}
	for _, item := range fixtures.String.Split {
		if len(Split(item.Input, item.Delimiter, item.TrimParts)) != len(item.Output) {
			t.Fatal("split length mismatch")
		}
		parts := Split(item.Input, item.Delimiter, item.TrimParts)
		for index, part := range parts {
			if part != item.Output[index] {
				t.Fatal("split mismatch")
			}
		}
	}
	for _, item := range fixtures.String.Join {
		if Join(item.Parts, item.Separator) != item.Output {
			t.Fatal("join mismatch")
		}
	}

	diff := fixtures.Datetime.DiffMillis
	earlier, ok := ParseDatetime(diff.Earlier, DefaultPattern)
	if !ok {
		t.Fatal("parse earlier failed")
	}
	later, ok := ParseDatetime(diff.Later, DefaultPattern)
	if !ok {
		t.Fatal("parse later failed")
	}
	if DiffMillis(earlier, later) != diff.Output {
		t.Fatal("diff_millis mismatch")
	}
	for _, item := range fixtures.Datetime.ToUnixMillis {
		parsed, ok := ParseDatetime(item.Input, DefaultPattern)
		if !ok {
			t.Fatal("to_unix_millis parse failed")
		}
		if ToUnixMillis(parsed) != item.Output {
			t.Fatal("to_unix_millis mismatch")
		}
	}
	for _, item := range fixtures.Datetime.FromUnixMillis {
		parsed, ok := FromUnixMillis(item.Input)
		if !ok {
			t.Fatal("from_unix_millis failed")
		}
		if FormatDatetime(parsed, DefaultPattern) != item.Output {
			t.Fatal("from_unix_millis mismatch")
		}
	}

	hello := []byte(fixtures.Encoding.Base64Encode[0].Input)
	if Base64Encode(hello) != fixtures.Encoding.Base64Encode[0].Output {
		t.Fatal("base64 mismatch")
	}
	if HexEncode(hello) != fixtures.Encoding.HexEncode[0].Output {
		t.Fatal("hex mismatch")
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

	merge := fixtures.Object.DeepMerge
	merged := DeepMerge(merge.Base, merge.Overlay).(map[string]any)
	nested := merged["nested"].(map[string]any)
	expectedNested := merge.Output["nested"].(map[string]any)
	if nested["x"] != expectedNested["x"] || nested["y"] != expectedNested["y"] {
		t.Fatal("deep_merge mismatch")
	}
	shallow := fixtures.Object.ShallowMerge
	shallowResult := ShallowMerge(shallow.Base, shallow.Overlay).(map[string]any)
	if shallowResult["a"] != shallow.Output["a"] {
		t.Fatal("shallow_merge a mismatch")
	}
	shallowNested := shallowResult["nested"].(map[string]any)
	expectedShallowNested := shallow.Output["nested"].(map[string]any)
	if shallowNested["y"] != expectedShallowNested["y"] {
		t.Fatal("shallow_merge nested mismatch")
	}

	pathCase := fixtures.Object.SetGetPath
	updated := SetPath(map[string]any{}, pathCase.Path, pathCase.Value)
	value, ok := GetPath(updated, pathCase.Path)
	if !ok || value != pathCase.Output {
		t.Fatal("set/get path mismatch")
	}
	hasPathSource := map[string]any{
		"user": map[string]any{"name": "Ada"},
	}
	for _, item := range fixtures.Object.HasPath {
		if HasPath(hasPathSource, item.Path) != item.Exists {
			t.Fatalf("has_path mismatch for %s", item.Path)
		}
	}

	sha := fixtures.Crypto.Sha256Hash[0]
	if Sha256Hash([]byte(sha.Input)) != sha.Output {
		t.Fatal("sha256 mismatch")
	}
	hmac := fixtures.Crypto.HmacSha256[0]
	if HmacSha256([]byte(hmac.Input), []byte(hmac.Secret)) != hmac.Output {
		t.Fatal("hmac mismatch")
	}
	for _, item := range fixtures.Crypto.SecureCompare {
		if SecureCompare(item.Left, item.Right) != item.Output {
			t.Fatal("secure_compare mismatch")
		}
	}

	for _, item := range fixtures.I18n.FormatNumberLocale {
		if FormatNumberLocale(item.Value, item.Locale, item.Decimals) != item.Output {
			t.Fatal("format_number_locale mismatch")
		}
	}
	for _, item := range fixtures.I18n.ParseNumberLocale {
		parsed, ok := ParseNumberLocale(item.Input, item.Locale)
		if item.Output == nil {
			if ok {
				t.Fatal("parse_number_locale expected null")
			}
			continue
		}
		if !ok || parsed != *item.Output {
			t.Fatal("parse_number_locale mismatch")
		}
	}

	if result := Coalesce(nil, ptr(""), ptr("  "), ptr("ok")); result == nil || *result != fixtures.Optional.Coalesce[0].Output {
		t.Fatal("coalesce mismatch")
	}

	for _, item := range fixtures.Result.UnwrapOr {
		var result ResultValue[int]
		if item.Kind == "ok" {
			result = Ok(item.Value)
		} else {
			result = Err[int](item.Message)
		}
		if UnwrapOr(result, item.Default) != item.Output {
			t.Fatal("unwrap_or mismatch")
		}
	}

	clamp := fixtures.Number.Clamp[0]
	if Clamp(clamp.Value, clamp.Min, clamp.Max) != clamp.Output {
		t.Fatal("clamp mismatch")
	}
	for _, item := range fixtures.Number.ParseInt {
		parsed, ok := ParseInt(item.Input)
		if item.Output == nil {
			if ok {
				t.Fatal("parse_int expected failure")
			}
			continue
		}
		if !ok || parsed != *item.Output {
			t.Fatal("parse_int mismatch")
		}
	}
	for _, item := range fixtures.Number.PercentFormat {
		if PercentFormat(item.Value, item.Decimals) != item.Output {
			t.Fatal("percent_format mismatch")
		}
	}

	uniqueItem := fixtures.Collection.Unique[0]
	if len(Unique(uniqueItem.Input)) != len(uniqueItem.Output) {
		t.Fatal("unique mismatch")
	}
	for _, item := range fixtures.Collection.First {
		value, ok := First(item.Input)
		if item.Output == nil {
			if ok {
				t.Fatal("first expected empty")
			}
			continue
		}
		if !ok || value != *item.Output {
			t.Fatal("first mismatch")
		}
	}
	for _, item := range fixtures.Collection.Last {
		value, ok := Last(item.Input)
		if item.Output == nil {
			if ok {
				t.Fatal("last expected empty")
			}
			continue
		}
		if !ok || value != *item.Output {
			t.Fatal("last mismatch")
		}
	}

	email := fixtures.Validation.IsEmail[0]
	if IsEmail(email.Input) != email.Output {
		t.Fatal("is_email mismatch")
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
	for _, item := range fixtures.Validation.IsPhoneE164 {
		if IsPhoneE164(item.Input) != item.Output {
			t.Fatal("is_phone_e164 mismatch")
		}
	}

	pathJoin := fixtures.Path.JoinPath[0]
	if JoinPath(pathJoin.Segments...) != pathJoin.Output {
		t.Fatal("join_path mismatch")
	}

	for _, item := range fixtures.Boolean.ParseBool {
		parsed, ok := ParseBool(item.Input)
		if item.Output == nil {
			if ok {
				t.Fatal("parse_bool expected null")
			}
			continue
		}
		if !ok || parsed == nil || *parsed != *item.Output {
			t.Fatal("parse_bool mismatch")
		}
	}
    for _, item := range fixtures.Boolean.IsTruthy {
        input := item.Input
        if IsTruthy(&input) != item.Output {
            t.Fatal("is_truthy mismatch")
        }
    }

    var repeatCases []struct {
        Input  string `json:"input"`
        Count  int    `json:"count"`
        Output string `json:"output"`
    }
    if err := json.Unmarshal(fixtures.String.Repeat, &repeatCases); err == nil {
        for _, item := range repeatCases {
            if Repeat(item.Input, item.Count) != item.Output {
                t.Fatal("repeat mismatch")
            }
        }
    }

    keys, err := KeysFromJSON([]byte(`{"b":2,"a":1}`))
    if err != nil || len(keys) != 2 || keys[0] != "b" || keys[1] != "a" {
        t.Fatal("keys mismatch")
    }
    values, err := ValuesFromJSON([]byte(`{"b":2,"a":1}`))
    if err != nil || len(values) != 2 {
        t.Fatal("values mismatch")
    }

    if !DeepEqual(map[string]any{"a": 1, "b": []any{2, 3}}, map[string]any{"b": []any{2, 3}, "a": 1}) {
        t.Fatal("deep_equal mismatch")
    }
    cloned := DeepClone(map[string]any{"a": 1, "b": []any{2, map[string]any{"c": 3}}}).(map[string]any)
    nested := cloned["b"].([]any)
    nested[1].(map[string]any)["c"] = 99
    if DeepEqual(cloned, map[string]any{"a": 1, "b": []any{2, map[string]any{"c": 3}}}) {
        t.Fatal("deep_clone should be independent")
    }
    if InRange(5, 1, 10) != true || InRange(0, 1, 10) != false {
        t.Fatal("in_range mismatch")
    }
	if !IsSameInstant(earlier, earlier) {
		t.Fatal("is_same_instant mismatch")
	}

	for _, item := range fixtures.Bloom.Create {
		filter := Create(item.ExpectedItems, item.FalsePositiveRate)
		if filter.BitCount != item.BitCount || filter.HashCount != item.HashCount {
			t.Fatal("bloom create mismatch")
		}
	}
	for _, item := range fixtures.Bloom.EstimateBitCount {
		if EstimateBitCount(item.ExpectedItems, item.FalsePositiveRate) != item.Output {
			t.Fatal("bloom estimate_bit_count mismatch")
		}
	}
	for _, item := range fixtures.Bloom.EstimateHashCount {
		if EstimateHashCount(item.ExpectedItems, item.BitCount) != item.Output {
			t.Fatal("bloom estimate_hash_count mismatch")
		}
	}
	for _, item := range fixtures.Bloom.MightContain {
		filter := Create(128, 0.01)
		for _, value := range item.Added {
			Add(filter, value)
		}
		if MightContain(filter, item.Present) != item.PresentOutput {
			t.Fatal("bloom might_contain present mismatch")
		}
		if MightContain(filter, item.Absent) != item.AbsentOutput {
			t.Fatal("bloom might_contain absent mismatch")
		}
	}

	for _, item := range fixtures.Currency.IsCurrencyCode {
		if IsCurrencyCode(item.Input) != item.Output {
			t.Fatal("is_currency_code mismatch")
		}
	}
	for _, item := range fixtures.Currency.MinorUnitExponent {
		exponent, ok := MinorUnitExponent(item.Code)
		if item.Output == nil {
			if ok {
				t.Fatal("minor_unit_exponent expected null")
			}
			continue
		}
		if !ok || exponent != *item.Output {
			t.Fatal("minor_unit_exponent mismatch")
		}
	}
	for _, item := range fixtures.Currency.ToMinorUnits {
		minor, ok := ToMinorUnits(item.Amount, item.Code)
		if !ok || minor != item.Output {
			t.Fatal("to_minor_units mismatch")
		}
	}
	for _, item := range fixtures.Currency.FromMinorUnits {
		amount, ok := FromMinorUnits(item.Minor, item.Code)
		if !ok || amount != item.Output {
			t.Fatal("from_minor_units mismatch")
		}
	}
	for _, item := range fixtures.Currency.FormatCurrency {
		formatted, ok := FormatCurrency(item.Amount, item.Code, item.Locale)
		if !ok || formatted != item.Output {
			t.Fatal("format_currency mismatch")
		}
	}

	runExtendedConformance(t, loadExtendedFixtures(t))
}
