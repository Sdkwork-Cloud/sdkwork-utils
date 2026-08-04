package utils

import (
	"math"
	"strconv"
	"strings"
)

type compactUnit struct {
	exponent int
	unit     string
}

type localeRules struct {
	prefix    bool
	decimal   string
	grouping  string
	nameSpace bool
	compact   []compactUnit
}

var moneyModes = map[string]bool{
	"symbol":        true,
	"narrow_symbol": true,
	"code":          true,
	"name":          true,
	"decimal":       true,
	"accounting":    true,
	"compact":       true,
}

var moneySigns = map[string]bool{
	"auto":        true,
	"always":      true,
	"never":       true,
	"except_zero": true,
}

var enCompact = []compactUnit{{12, "T"}, {9, "B"}, {6, "M"}, {3, "K"}}
var zhCompact = []compactUnit{{12, "兆"}, {8, "亿"}, {4, "万"}}
var jaCompact = []compactUnit{{12, "兆"}, {8, "億"}, {4, "万"}}
var koCompact = []compactUnit{{12, "조"}, {8, "억"}, {4, "만"}}
var deCompact = []compactUnit{{12, "Bio."}, {9, "Mrd."}, {6, "Mio."}, {3, "Tsd."}}
var frCompact = []compactUnit{{12, "B"}, {9, "Md"}, {6, "M"}, {3, "k"}}
var itCompact = []compactUnit{{12, "Bio."}, {9, "Mrd."}, {6, "M"}, {3, "k"}}
var esCompact = []compactUnit{{12, "T"}, {9, "B"}, {6, "M"}, {3, "k"}}
var ruCompact = []compactUnit{{12, "трлн"}, {9, "млрд"}, {6, "млн"}, {3, "тыс."}}

var localeRulesByKey = map[string]localeRules{
	"en-us": {true, ".", ",", true, enCompact},
	"zh-cn": {true, ".", ",", false, zhCompact},
	"ja-jp": {true, ".", ",", false, jaCompact},
	"ko-kr": {true, ".", ",", false, koCompact},
	"de-de": {false, ",", ".", true, deCompact},
	"fr-fr": {false, ",", " ", true, frCompact},
	"it-it": {false, ",", ".", true, itCompact},
	"es-es": {false, ",", ".", true, esCompact},
	"ru-ru": {false, ",", " ", true, ruCompact},
}

var currencyNamesByKey = map[string]map[string]string{
	"en-us": {
		"USD": "US dollars", "EUR": "euros", "GBP": "British pounds", "CNY": "Chinese yuan",
		"JPY": "Japanese yen", "KRW": "South Korean won", "HKD": "Hong Kong dollars",
		"TWD": "New Taiwan dollars", "CHF": "Swiss francs", "CAD": "Canadian dollars",
		"AUD": "Australian dollars", "INR": "Indian rupees", "BHD": "Bahraini dinars",
		"KWD": "Kuwaiti dinars",
	},
	"zh-cn": {
		"USD": "美元", "EUR": "欧元", "GBP": "英镑", "CNY": "人民币", "JPY": "日元", "KRW": "韩元",
		"HKD": "港币", "TWD": "新台币", "CHF": "瑞士法郎", "CAD": "加拿大元", "AUD": "澳大利亚元",
		"INR": "印度卢比", "BHD": "巴林第纳尔", "KWD": "科威特第纳尔",
	},
	"de-de": {
		"USD": "US-Dollar", "EUR": "Euro", "GBP": "Britisches Pfund", "CNY": "Chinesischer Yuan",
		"JPY": "Japanischer Yen", "KRW": "Südkoreanischer Won", "HKD": "Hongkong-Dollar",
		"TWD": "Neuer Taiwan-Dollar", "CHF": "Schweizer Franken", "CAD": "Kanadischer Dollar",
		"AUD": "Australischer Dollar", "INR": "Indische Rupie", "BHD": "Bahrainischer Dinar",
		"KWD": "Kuwaitischer Dinar",
	},
	"fr-fr": {
		"USD": "dollar américain", "EUR": "euro", "GBP": "livre sterling", "CNY": "yuan chinois",
		"JPY": "yen japonais", "KRW": "won sud-coréen", "HKD": "dollar de Hong Kong",
		"TWD": "nouveau dollar de Taïwan", "CHF": "franc suisse", "CAD": "dollar canadien",
		"AUD": "dollar australien", "INR": "roupie indienne", "BHD": "dinar bahreïni",
		"KWD": "dinar koweïtien",
	},
	"it-it": {
		"USD": "dollaro statunitense", "EUR": "euro", "GBP": "sterlina britannica",
		"CNY": "yuan cinese", "JPY": "yen giapponese", "KRW": "won sudcoreano",
		"HKD": "dollaro di Hong Kong", "TWD": "nuovo dollaro taiwanese", "CHF": "franco svizzero",
		"CAD": "dollaro canadese", "AUD": "dollaro australiano", "INR": "rupia indiana",
		"BHD": "dinaro bahreinita", "KWD": "dinaro kuwaitiano",
	},
	"es-es": {
		"USD": "dólar estadounidense", "EUR": "euro", "GBP": "libra esterlina",
		"CNY": "yuan chino", "JPY": "yen japonés", "KRW": "won surcoreano",
		"HKD": "dólar de Hong Kong", "TWD": "nuevo dólar taiwanés", "CHF": "franco suizo",
		"CAD": "dólar canadiense", "AUD": "dólar australiano", "INR": "rupia india",
		"BHD": "dinar bahreiní", "KWD": "dinar kuwaití",
	},
	"ja-jp": {
		"USD": "米ドル", "EUR": "ユーロ", "GBP": "英ポンド", "CNY": "中国人民元", "JPY": "日本円",
		"KRW": "韓国ウォン", "HKD": "香港ドル", "TWD": "台湾ドル", "CHF": "スイスフラン",
		"CAD": "カナダドル", "AUD": "オーストラリアドル", "INR": "インドルピー",
		"BHD": "バーレーンディナール", "KWD": "クウェートディナール",
	},
	"ko-kr": {
		"USD": "미국 달러", "EUR": "유로", "GBP": "영국 파운드", "CNY": "중국 위안", "JPY": "일본 엔",
		"KRW": "대한민국 원", "HKD": "홍콩 달러", "TWD": "신 대만 달러", "CHF": "스위스 프랑",
		"CAD": "캐나다 달러", "AUD": "호주 달러", "INR": "인도 루피", "BHD": "바레인 디나르",
		"KWD": "쿠웨이트 디나르",
	},
	"ru-ru": {
		"USD": "доллар США", "EUR": "евро", "GBP": "британский фунт", "CNY": "китайский юань",
		"JPY": "японская иена", "KRW": "южнокорейская вона", "HKD": "гонконгский доллар",
		"TWD": "новый тайваньский доллар", "CHF": "швейцарский франк", "CAD": "канадский доллар",
		"AUD": "австралийский доллар", "INR": "индийская рупия", "BHD": "бахрейнский динар",
		"KWD": "кувейтский динар",
	},
}

func moneyLocaleKey(locale string) string {
	normalized := strings.ToLower(strings.TrimSpace(locale))
	if _, ok := localeRulesByKey[normalized]; ok {
		return normalized
	}
	language := normalized
	if index := strings.Index(normalized, "-"); index >= 0 {
		language = normalized[:index]
	}
	for key := range localeRulesByKey {
		keyLanguage := key
		if index := strings.Index(key, "-"); index >= 0 {
			keyLanguage = key[:index]
		}
		if keyLanguage == language {
			return key
		}
	}
	return "en-us"
}

func moneyRules(locale string) localeRules {
	return localeRulesByKey[moneyLocaleKey(locale)]
}

func moneyNames(locale string) map[string]string {
	return currencyNamesByKey[moneyLocaleKey(locale)]
}

func parsedMoney(value float64) (negative bool, isZero bool, absDecimal string, ok bool) {
	if math.IsNaN(value) || math.IsInf(value, 0) {
		return false, false, "", false
	}
	negative = value < 0
	absValue := math.Abs(value)
	if absValue == 0 {
		return false, true, "0", true
	}
	return negative, false, strconv.FormatFloat(absValue, 'f', -1, 64), true
}

func splitMoneyDecimal(absDecimal string) (string, string) {
	if index := strings.Index(absDecimal, "."); index >= 0 {
		return absDecimal[:index], absDecimal[index+1:]
	}
	return absDecimal, ""
}

func incrementMoneyDecimal(intPart string, fracPart string) (string, string) {
	digits := []byte(intPart + fracPart)
	index := len(digits) - 1
	for index >= 0 && digits[index] == '9' {
		digits[index] = '0'
		index--
	}
	if index >= 0 {
		digits[index]++
	} else {
		digits = append([]byte{'1'}, digits...)
	}
	cut := len(digits) - len(fracPart)
	return string(digits[:cut]), string(digits[cut:])
}

func roundMoneyDecimal(absDecimal string, maxFraction int) (string, string) {
	intPart, fracPart := splitMoneyDecimal(absDecimal)
	if len(fracPart) <= maxFraction {
		return intPart, fracPart + strings.Repeat("0", maxFraction-len(fracPart))
	}
	keep := fracPart[:maxFraction]
	if fracPart[maxFraction] >= '5' {
		return incrementMoneyDecimal(intPart, keep)
	}
	return intPart, keep
}

func trimMoneyFraction(fracPart string, minFraction int) string {
	end := len(fracPart)
	for end > minFraction && fracPart[end-1] == '0' {
		end--
	}
	return fracPart[:end]
}

func groupMoneyInteger(intPart string, grouping string, useGrouping bool) string {
	if !useGrouping {
		return intPart
	}
	var builder strings.Builder
	for index, ch := range []byte(intPart) {
		if index > 0 && (len(intPart)-index)%3 == 0 {
			builder.WriteString(grouping)
		}
		builder.WriteByte(ch)
	}
	return builder.String()
}

func shiftMoneyDecimalPoint(absDecimal string, exponent int) string {
	intPart, fracPart := splitMoneyDecimal(absDecimal)
	digits := intPart + fracPart
	pointIndex := len(intPart) - exponent
	if pointIndex <= 0 {
		return "0." + strings.Repeat("0", -pointIndex) + digits
	}
	if pointIndex >= len(digits) {
		return digits + strings.Repeat("0", pointIndex-len(digits))
	}
	return digits[:pointIndex] + "." + digits[pointIndex:]
}

func formatMoneyCompactBody(negative bool, isZero bool, absDecimal string, rules localeRules) string {
	if isZero {
		return "0"
	}
	intPart, _ := splitMoneyDecimal(absDecimal)
	intLength := len(intPart)
	unitIndex := -1
	for index, unit := range rules.compact {
		if intLength > unit.exponent {
			unitIndex = index
			break
		}
	}
	if unitIndex < 0 {
		roundedInt, roundedFrac := roundMoneyDecimal(absDecimal, 1)
		trimmed := trimMoneyFraction(roundedFrac, 0)
		if trimmed == "" {
			return roundedInt
		}
		return roundedInt + "." + trimmed
	}
	unit := rules.compact[unitIndex]
	scaledInt, scaledFrac := roundMoneyDecimal(shiftMoneyDecimalPoint(absDecimal, unit.exponent), 1)
	if len(scaledInt) > 1 && unitIndex+1 < len(rules.compact) {
		nextUnit := rules.compact[unitIndex+1]
		scaledInt, scaledFrac = roundMoneyDecimal(shiftMoneyDecimalPoint(absDecimal, nextUnit.exponent), 1)
		unit = nextUnit
	}
	trimmed := trimMoneyFraction(scaledFrac, 0)
	if trimmed == "" {
		return scaledInt + unit.unit
	}
	return scaledInt + "." + trimmed + unit.unit
}

func moneySignPrefix(negative bool, isZero bool, sign string) string {
	switch sign {
	case "always":
		if negative {
			return "-"
		}
		return "+"
	case "never":
		return ""
	case "except_zero":
		if negative {
			return "-"
		}
		if isZero {
			return ""
		}
		return "+"
	default:
		if negative {
			return "-"
		}
		return ""
	}
}

func defaultMoneyFraction(mode string) (int, int) {
	switch mode {
	case "compact":
		return 0, 1
	case "decimal":
		return 0, 2
	default:
		return 2, 2
	}
}

func formatMoneyInternal(
	value float64,
	currency string,
	locale string,
	mode string,
	minFraction *int,
	maxFraction *int,
	sign *string,
	useGrouping *bool,
) (string, bool) {
	meta, ok := lookupCurrency(currency)
	if !ok || !moneyModes[mode] {
		return "", false
	}
	if sign != nil && !moneySigns[*sign] {
		return "", false
	}
	negative, isZero, absDecimal, ok := parsedMoney(value)
	if !ok {
		return "", false
	}

	var resolvedMin int
	var resolvedMax int
	if minFraction == nil && maxFraction == nil {
		resolvedMin, resolvedMax = defaultMoneyFraction(mode)
	} else if minFraction == nil || maxFraction == nil {
		return "", false
	} else {
		if *minFraction < 0 || *maxFraction > 18 || *minFraction > *maxFraction {
			return "", false
		}
		resolvedMin, resolvedMax = *minFraction, *maxFraction
		if mode == "compact" {
			resolvedMin, resolvedMax = 0, 1
		}
	}

	resolvedGrouping := true
	if useGrouping != nil {
		resolvedGrouping = *useGrouping
	}
	resolvedSign := "auto"
	if sign != nil {
		resolvedSign = *sign
	}
	rules := moneyRules(locale)
	symbol := meta.symbol

	if mode == "compact" {
		body := formatMoneyCompactBody(negative, isZero, absDecimal, rules)
		signText := moneySignPrefix(negative, isZero, resolvedSign)
		if rules.prefix {
			return signText + symbol + body, true
		}
		return signText + body + " " + symbol, true
	}

	roundedInt, roundedFrac := roundMoneyDecimal(absDecimal, resolvedMax)
	trimmed := trimMoneyFraction(roundedFrac, resolvedMin)
	grouped := groupMoneyInteger(roundedInt, rules.grouping, resolvedGrouping)
	body := grouped
	if trimmed != "" {
		body = grouped + rules.decimal + trimmed
	}
	signText := moneySignPrefix(negative, isZero, resolvedSign)

	if mode == "decimal" {
		return signText + body, true
	}

	if mode == "accounting" {
		if negative && rules.prefix {
			return "(" + symbol + body + ")", true
		}
		if negative && !rules.prefix {
			return "-" + body + " " + symbol, true
		}
		if rules.prefix {
			return symbol + body, true
		}
		return body + " " + symbol, true
	}

	if mode == "code" {
		if rules.prefix {
			return signText + currency + " " + body, true
		}
		return signText + body + " " + currency, true
	}

	if mode == "name" {
		name := currencyNamesByKey[moneyLocaleKey(locale)][currency]
		if name == "" {
			name = "US dollars"
		}
		separator := " "
		if !rules.nameSpace {
			separator = ""
		}
		return signText + body + separator + name, true
	}

	if rules.prefix {
		return signText + symbol + body, true
	}
	return signText + body + " " + symbol, true
}

// MoneySymbol returns the narrow display symbol for a supported ISO 4217 code.
func MoneySymbol(currency string) (string, bool) {
	meta, ok := lookupCurrency(currency)
	if !ok {
		return "", false
	}
	return meta.symbol, true
}

// FormatMoney formats a major-unit amount with locale separators and the given display mode.
func FormatMoney(value float64, currency string, locale string, mode string) (string, bool) {
	return formatMoneyInternal(value, currency, locale, mode, nil, nil, nil, nil)
}

// FormatMoneyDigits formats with explicit minimum and maximum fraction digits.
func FormatMoneyDigits(value float64, currency string, locale string, mode string, minFraction int, maxFraction int) (string, bool) {
	return formatMoneyInternal(value, currency, locale, mode, &minFraction, &maxFraction, nil, nil)
}

// FormatMoneyMinorUnits formats an integer minor-unit amount using the currency exponent.
func FormatMoneyMinorUnits(minor int64, currency string, locale string, mode string) (string, bool) {
	if mode == "compact" || !moneyModes[mode] {
		return "", false
	}
	exponent, ok := MinorUnitExponent(currency)
	if !ok {
		return "", false
	}
	major := float64(minor) / math.Pow(10, float64(exponent))
	return formatMoneyInternal(major, currency, locale, mode, &exponent, &exponent, nil, nil)
}

// FormatMoneyOptions formats with explicit fraction digits, sign display, and grouping control.
func FormatMoneyOptions(value float64, currency string, locale string, mode string, minFraction int, maxFraction int, sign string, useGrouping bool) (string, bool) {
	return formatMoneyInternal(value, currency, locale, mode, &minFraction, &maxFraction, &sign, &useGrouping)
}
