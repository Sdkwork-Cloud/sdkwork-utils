package com.sdkwork.utils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Money display formatting aligned with industry Intl.NumberFormat conventions.
 *
 * <p>Modes: symbol, narrow_symbol, code, name, decimal, accounting, compact.
 * Symbol placement: en-US/zh-CN/ja-JP/ko-KR prefix without space; de-DE/fr-FR/it-IT/es-ES/ru-RU
 * suffix with a single space. Rounding is half-up on the shortest decimal representation.
 */
public final class MoneyUtils {
    private static final List<String> MODES = List.of(
            "symbol", "narrow_symbol", "code", "name", "decimal", "accounting", "compact");
    private static final List<String> SIGNS = List.of("auto", "always", "never", "except_zero");

    private static final Map<String, String> CURRENCY_SYMBOLS = Map.ofEntries(
            Map.entry("USD", "$"),
            Map.entry("EUR", "€"),
            Map.entry("GBP", "£"),
            Map.entry("CNY", "¥"),
            Map.entry("JPY", "¥"),
            Map.entry("KRW", "₩"),
            Map.entry("HKD", "HK$"),
            Map.entry("TWD", "NT$"),
            Map.entry("CHF", "CHF"),
            Map.entry("CAD", "CA$"),
            Map.entry("AUD", "A$"),
            Map.entry("INR", "₹"),
            Map.entry("BHD", "BHD"),
            Map.entry("KWD", "KWD"));

    private record CompactUnit(int exponent, String unit) {
    }

    private record LocaleRules(
            boolean prefix, String decimal, String grouping, boolean nameSpace,
            List<CompactUnit> compact) {
    }

    private static final List<CompactUnit> EN_COMPACT = List.of(
            new CompactUnit(12, "T"), new CompactUnit(9, "B"), new CompactUnit(6, "M"),
            new CompactUnit(3, "K"));
    private static final List<CompactUnit> ZH_COMPACT = List.of(
            new CompactUnit(12, "兆"), new CompactUnit(8, "亿"), new CompactUnit(4, "万"));
    private static final List<CompactUnit> JA_COMPACT = List.of(
            new CompactUnit(12, "兆"), new CompactUnit(8, "億"), new CompactUnit(4, "万"));
    private static final List<CompactUnit> KO_COMPACT = List.of(
            new CompactUnit(12, "조"), new CompactUnit(8, "억"), new CompactUnit(4, "만"));
    private static final List<CompactUnit> DE_COMPACT = List.of(
            new CompactUnit(12, "Bio."), new CompactUnit(9, "Mrd."), new CompactUnit(6, "Mio."),
            new CompactUnit(3, "Tsd."));
    private static final List<CompactUnit> FR_COMPACT = List.of(
            new CompactUnit(12, "B"), new CompactUnit(9, "Md"), new CompactUnit(6, "M"),
            new CompactUnit(3, "k"));
    private static final List<CompactUnit> IT_COMPACT = List.of(
            new CompactUnit(12, "Bio."), new CompactUnit(9, "Mrd."), new CompactUnit(6, "M"),
            new CompactUnit(3, "k"));
    private static final List<CompactUnit> ES_COMPACT = List.of(
            new CompactUnit(12, "T"), new CompactUnit(9, "B"), new CompactUnit(6, "M"),
            new CompactUnit(3, "k"));
    private static final List<CompactUnit> RU_COMPACT = List.of(
            new CompactUnit(12, "трлн"), new CompactUnit(9, "млрд"), new CompactUnit(6, "млн"),
            new CompactUnit(3, "тыс."));

    private static final Map<String, LocaleRules> LOCALE_RULES = Map.ofEntries(
            Map.entry("en-us", new LocaleRules(true, ".", ",", true, EN_COMPACT)),
            Map.entry("zh-cn", new LocaleRules(true, ".", ",", false, ZH_COMPACT)),
            Map.entry("ja-jp", new LocaleRules(true, ".", ",", false, JA_COMPACT)),
            Map.entry("ko-kr", new LocaleRules(true, ".", ",", false, KO_COMPACT)),
            Map.entry("de-de", new LocaleRules(false, ",", ".", true, DE_COMPACT)),
            Map.entry("fr-fr", new LocaleRules(false, ",", " ", true, FR_COMPACT)),
            Map.entry("it-it", new LocaleRules(false, ",", ".", true, IT_COMPACT)),
            Map.entry("es-es", new LocaleRules(false, ",", ".", true, ES_COMPACT)),
            Map.entry("ru-ru", new LocaleRules(false, ",", " ", true, RU_COMPACT)));

    private static final Map<String, Map<String, String>> CURRENCY_NAMES = Map.ofEntries(
            Map.entry("en-us", Map.ofEntries(
                    Map.entry("USD", "US dollars"), Map.entry("EUR", "euros"),
                    Map.entry("GBP", "British pounds"), Map.entry("CNY", "Chinese yuan"),
                    Map.entry("JPY", "Japanese yen"), Map.entry("KRW", "South Korean won"),
                    Map.entry("HKD", "Hong Kong dollars"), Map.entry("TWD", "New Taiwan dollars"),
                    Map.entry("CHF", "Swiss francs"), Map.entry("CAD", "Canadian dollars"),
                    Map.entry("AUD", "Australian dollars"), Map.entry("INR", "Indian rupees"),
                    Map.entry("BHD", "Bahraini dinars"), Map.entry("KWD", "Kuwaiti dinars"))),
            Map.entry("zh-cn", Map.ofEntries(
                    Map.entry("USD", "美元"), Map.entry("EUR", "欧元"), Map.entry("GBP", "英镑"),
                    Map.entry("CNY", "人民币"), Map.entry("JPY", "日元"), Map.entry("KRW", "韩元"),
                    Map.entry("HKD", "港币"), Map.entry("TWD", "新台币"), Map.entry("CHF", "瑞士法郎"),
                    Map.entry("CAD", "加拿大元"), Map.entry("AUD", "澳大利亚元"), Map.entry("INR", "印度卢比"),
                    Map.entry("BHD", "巴林第纳尔"), Map.entry("KWD", "科威特第纳尔"))),
            Map.entry("de-de", Map.ofEntries(
                    Map.entry("USD", "US-Dollar"), Map.entry("EUR", "Euro"),
                    Map.entry("GBP", "Britisches Pfund"), Map.entry("CNY", "Chinesischer Yuan"),
                    Map.entry("JPY", "Japanischer Yen"), Map.entry("KRW", "Südkoreanischer Won"),
                    Map.entry("HKD", "Hongkong-Dollar"), Map.entry("TWD", "Neuer Taiwan-Dollar"),
                    Map.entry("CHF", "Schweizer Franken"), Map.entry("CAD", "Kanadischer Dollar"),
                    Map.entry("AUD", "Australischer Dollar"), Map.entry("INR", "Indische Rupie"),
                    Map.entry("BHD", "Bahrainischer Dinar"), Map.entry("KWD", "Kuwaitischer Dinar"))),
            Map.entry("fr-fr", Map.ofEntries(
                    Map.entry("USD", "dollar américain"), Map.entry("EUR", "euro"),
                    Map.entry("GBP", "livre sterling"), Map.entry("CNY", "yuan chinois"),
                    Map.entry("JPY", "yen japonais"), Map.entry("KRW", "won sud-coréen"),
                    Map.entry("HKD", "dollar de Hong Kong"), Map.entry("TWD", "nouveau dollar de Taïwan"),
                    Map.entry("CHF", "franc suisse"), Map.entry("CAD", "dollar canadien"),
                    Map.entry("AUD", "dollar australien"), Map.entry("INR", "roupie indienne"),
                    Map.entry("BHD", "dinar bahreïni"), Map.entry("KWD", "dinar koweïtien"))),
            Map.entry("it-it", Map.ofEntries(
                    Map.entry("USD", "dollaro statunitense"), Map.entry("EUR", "euro"),
                    Map.entry("GBP", "sterlina britannica"), Map.entry("CNY", "yuan cinese"),
                    Map.entry("JPY", "yen giapponese"), Map.entry("KRW", "won sudcoreano"),
                    Map.entry("HKD", "dollaro di Hong Kong"), Map.entry("TWD", "nuovo dollaro taiwanese"),
                    Map.entry("CHF", "franco svizzero"), Map.entry("CAD", "dollaro canadese"),
                    Map.entry("AUD", "dollaro australiano"), Map.entry("INR", "rupia indiana"),
                    Map.entry("BHD", "dinaro bahreinita"), Map.entry("KWD", "dinaro kuwaitiano"))),
            Map.entry("es-es", Map.ofEntries(
                    Map.entry("USD", "dólar estadounidense"), Map.entry("EUR", "euro"),
                    Map.entry("GBP", "libra esterlina"), Map.entry("CNY", "yuan chino"),
                    Map.entry("JPY", "yen japonés"), Map.entry("KRW", "won surcoreano"),
                    Map.entry("HKD", "dólar de Hong Kong"), Map.entry("TWD", "nuevo dólar taiwanés"),
                    Map.entry("CHF", "franco suizo"), Map.entry("CAD", "dólar canadiense"),
                    Map.entry("AUD", "dólar australiano"), Map.entry("INR", "rupia india"),
                    Map.entry("BHD", "dinar bahreiní"), Map.entry("KWD", "dinar kuwaití"))),
            Map.entry("ja-jp", Map.ofEntries(
                    Map.entry("USD", "米ドル"), Map.entry("EUR", "ユーロ"), Map.entry("GBP", "英ポンド"),
                    Map.entry("CNY", "中国人民元"), Map.entry("JPY", "日本円"), Map.entry("KRW", "韓国ウォン"),
                    Map.entry("HKD", "香港ドル"), Map.entry("TWD", "台湾ドル"), Map.entry("CHF", "スイスフラン"),
                    Map.entry("CAD", "カナダドル"), Map.entry("AUD", "オーストラリアドル"),
                    Map.entry("INR", "インドルピー"), Map.entry("BHD", "バーレーンディナール"),
                    Map.entry("KWD", "クウェートディナール"))),
            Map.entry("ko-kr", Map.ofEntries(
                    Map.entry("USD", "미국 달러"), Map.entry("EUR", "유로"), Map.entry("GBP", "영국 파운드"),
                    Map.entry("CNY", "중국 위안"), Map.entry("JPY", "일본 엔"), Map.entry("KRW", "대한민국 원"),
                    Map.entry("HKD", "홍콩 달러"), Map.entry("TWD", "신 대만 달러"), Map.entry("CHF", "스위스 프랑"),
                    Map.entry("CAD", "캐나다 달러"), Map.entry("AUD", "호주 달러"), Map.entry("INR", "인도 루피"),
                    Map.entry("BHD", "바레인 디나르"), Map.entry("KWD", "쿠웨이트 디나르"))),
            Map.entry("ru-ru", Map.ofEntries(
                    Map.entry("USD", "доллар США"), Map.entry("EUR", "евро"),
                    Map.entry("GBP", "британский фунт"), Map.entry("CNY", "китайский юань"),
                    Map.entry("JPY", "японская иена"), Map.entry("KRW", "южнокорейская вона"),
                    Map.entry("HKD", "гонконгский доллар"), Map.entry("TWD", "новый тайваньский доллар"),
                    Map.entry("CHF", "швейцарский франк"), Map.entry("CAD", "канадский доллар"),
                    Map.entry("AUD", "австралийский доллар"), Map.entry("INR", "индийская рупия"),
                    Map.entry("BHD", "бахрейнский динар"), Map.entry("KWD", "кувейтский динар"))));

    private MoneyUtils() {
    }

    private static String lookupCurrency(String currency) {
        if (currency == null) {
            return null;
        }
        String normalized = currency.trim();
        if (normalized.length() != 3
                || !normalized.equals(normalized.toUpperCase(Locale.ROOT))
                || !normalized.chars().allMatch(Character::isLetter)) {
            return null;
        }
        return CURRENCY_SYMBOLS.containsKey(normalized) ? normalized : null;
    }

    private static String localeKey(String locale) {
        if (locale == null) {
            return "en-us";
        }
        String normalized = locale.trim().toLowerCase(Locale.ROOT);
        if (LOCALE_RULES.containsKey(normalized)) {
            return normalized;
        }
        String language = normalized.split("-")[0];
        for (String key : LOCALE_RULES.keySet()) {
            if (key.split("-")[0].equals(language)) {
                return key;
            }
        }
        return "en-us";
    }

    private static LocaleRules rules(String locale) {
        return LOCALE_RULES.get(localeKey(locale));
    }

    private static Map<String, String> names(String locale) {
        return CURRENCY_NAMES.get(localeKey(locale));
    }

    private record ParsedValue(boolean negative, boolean isZero, String absDecimal) {
    }

    private static ParsedValue parseValue(Double value) {
        if (value == null || value.isNaN() || value.isInfinite()) {
            return null;
        }
        boolean negative = value < 0;
        double abs = Math.abs(value);
        if (abs == 0) {
            return new ParsedValue(false, true, "0");
        }
        return new ParsedValue(negative, false, BigDecimal.valueOf(abs).toPlainString());
    }

    private static String[] splitDecimal(String absDecimal) {
        int index = absDecimal.indexOf('.');
        if (index < 0) {
            return new String[] { absDecimal, "" };
        }
        return new String[] { absDecimal.substring(0, index), absDecimal.substring(index + 1) };
    }

    private static String[] incrementDecimal(String intPart, String fracPart) {
        StringBuilder digits = new StringBuilder(intPart).append(fracPart);
        int index = digits.length() - 1;
        while (index >= 0 && digits.charAt(index) == '9') {
            digits.setCharAt(index, '0');
            index--;
        }
        if (index >= 0) {
            digits.setCharAt(index, (char) (digits.charAt(index) + 1));
        } else {
            digits.insert(0, '1');
        }
        int cut = digits.length() - fracPart.length();
        return new String[] { digits.substring(0, cut), digits.substring(cut) };
    }

    private static String[] roundDecimal(String absDecimal, int maxFraction) {
        String[] parts = splitDecimal(absDecimal);
        String intPart = parts[0];
        String fracPart = parts[1];
        if (fracPart.length() <= maxFraction) {
            StringBuilder padded = new StringBuilder(fracPart);
            while (padded.length() < maxFraction) {
                padded.append('0');
            }
            return new String[] { intPart, padded.toString() };
        }
        String keep = fracPart.substring(0, maxFraction);
        if (fracPart.charAt(maxFraction) >= '5') {
            return incrementDecimal(intPart, keep);
        }
        return new String[] { intPart, keep };
    }

    private static String trimFraction(String fracPart, int minFraction) {
        int end = fracPart.length();
        while (end > minFraction && fracPart.charAt(end - 1) == '0') {
            end--;
        }
        return fracPart.substring(0, end);
    }

    private static String groupInteger(String intPart, String grouping, boolean useGrouping) {
        if (!useGrouping) {
            return intPart;
        }
        StringBuilder grouped = new StringBuilder();
        for (int index = 0; index < intPart.length(); index++) {
            if (index > 0 && (intPart.length() - index) % 3 == 0) {
                grouped.append(grouping);
            }
            grouped.append(intPart.charAt(index));
        }
        return grouped.toString();
    }

    private static String shiftDecimalPoint(String absDecimal, int exponent) {
        String[] parts = splitDecimal(absDecimal);
        String digits = parts[0] + parts[1];
        int pointIndex = parts[0].length() - exponent;
        if (pointIndex <= 0) {
            return "0." + "0".repeat(-pointIndex) + digits;
        }
        if (pointIndex >= digits.length()) {
            return digits + "0".repeat(pointIndex - digits.length());
        }
        return digits.substring(0, pointIndex) + "." + digits.substring(pointIndex);
    }

    private static String formatCompactBody(ParsedValue parsed, LocaleRules rules) {
        if (parsed.isZero()) {
            return "0";
        }
        int intLength = splitDecimal(parsed.absDecimal())[0].length();
        int unitIndex = -1;
        for (int index = 0; index < rules.compact().size(); index++) {
            if (intLength > rules.compact().get(index).exponent()) {
                unitIndex = index;
                break;
            }
        }
        if (unitIndex < 0) {
            String[] rounded = roundDecimal(parsed.absDecimal(), 1);
            String trimmed = trimFraction(rounded[1], 0);
            return trimmed.isEmpty() ? rounded[0] : rounded[0] + "." + trimmed;
        }
        CompactUnit unit = rules.compact().get(unitIndex);
        String[] scaled = roundDecimal(shiftDecimalPoint(parsed.absDecimal(), unit.exponent()), 1);
        if (scaled[0].length() > 1 && unitIndex + 1 < rules.compact().size()) {
            CompactUnit nextUnit = rules.compact().get(unitIndex + 1);
            scaled = roundDecimal(shiftDecimalPoint(parsed.absDecimal(), nextUnit.exponent()), 1);
            unit = nextUnit;
        }
        String trimmed = trimFraction(scaled[1], 0);
        return (trimmed.isEmpty() ? scaled[0] : scaled[0] + "." + trimmed) + unit.unit();
    }

    private static String signPrefix(boolean negative, boolean isZero, String sign) {
        switch (sign) {
            case "always":
                return negative ? "-" : "+";
            case "never":
                return "";
            case "except_zero":
                return negative ? "-" : (isZero ? "" : "+");
            default:
                return negative ? "-" : "";
        }
    }

    private static int[] defaultFraction(String mode) {
        if ("compact".equals(mode)) {
            return new int[] { 0, 1 };
        }
        if ("decimal".equals(mode)) {
            return new int[] { 0, 2 };
        }
        return new int[] { 2, 2 };
    }

    private static String formatInternal(
            Double value, String currency, String locale, String mode,
            Integer minFraction, Integer maxFraction, String sign, Boolean useGrouping) {
        String code = lookupCurrency(currency);
        if (code == null || !MODES.contains(mode)) {
            return null;
        }
        if (sign != null && !SIGNS.contains(sign)) {
            return null;
        }
        ParsedValue parsed = parseValue(value);
        if (parsed == null) {
            return null;
        }

        int resolvedMin;
        int resolvedMax;
        if (minFraction == null && maxFraction == null) {
            int[] defaults = defaultFraction(mode);
            resolvedMin = defaults[0];
            resolvedMax = defaults[1];
        } else if (minFraction == null || maxFraction == null) {
            return null;
        } else {
            if (minFraction < 0 || maxFraction > 18 || minFraction > maxFraction) {
                return null;
            }
            resolvedMin = minFraction;
            resolvedMax = maxFraction;
            if ("compact".equals(mode)) {
                resolvedMin = 0;
                resolvedMax = 1;
            }
        }

        boolean resolvedGrouping = useGrouping == null || useGrouping;
        String resolvedSign = sign == null ? "auto" : sign;
        LocaleRules rules = rules(locale);
        String symbol = CURRENCY_SYMBOLS.get(code);

        if ("compact".equals(mode)) {
            String body = formatCompactBody(parsed, rules);
            String signText = signPrefix(parsed.negative(), parsed.isZero(), resolvedSign);
            return rules.prefix()
                    ? signText + symbol + body
                    : signText + body + " " + symbol;
        }

        String[] rounded = roundDecimal(parsed.absDecimal(), resolvedMax);
        String trimmed = trimFraction(rounded[1], resolvedMin);
        String grouped = groupInteger(rounded[0], rules.grouping(), resolvedGrouping);
        String body = trimmed.isEmpty() ? grouped : grouped + rules.decimal() + trimmed;
        String signText = signPrefix(parsed.negative(), parsed.isZero(), resolvedSign);

        if ("decimal".equals(mode)) {
            return signText + body;
        }

        if ("accounting".equals(mode)) {
            if (parsed.negative() && rules.prefix()) {
                return "(" + symbol + body + ")";
            }
            if (parsed.negative() && !rules.prefix()) {
                return "-" + body + " " + symbol;
            }
            return rules.prefix() ? symbol + body : body + " " + symbol;
        }

        if ("code".equals(mode)) {
            return rules.prefix()
                    ? signText + code + " " + body
                    : signText + body + " " + code;
        }

        if ("name".equals(mode)) {
            String name = names(locale).getOrDefault(code, "US dollars");
            String separator = rules.nameSpace() ? " " : "";
            return signText + body + separator + name;
        }

        return rules.prefix() ? signText + symbol + body : signText + body + " " + symbol;
    }

    public static String moneySymbol(String currency) {
        String code = lookupCurrency(currency);
        return code == null ? null : CURRENCY_SYMBOLS.get(code);
    }

    public static String formatMoney(double value, String currency, String locale, String mode) {
        return formatInternal(value, currency, locale, mode, null, null, null, null);
    }

    public static String formatMoneyDigits(
            double value, String currency, String locale, String mode,
            int minFraction, int maxFraction) {
        return formatInternal(value, currency, locale, mode, minFraction, maxFraction, null, null);
    }

    public static String formatMoneyMinorUnits(
            long minor, String currency, String locale, String mode) {
        if ("compact".equals(mode) || !MODES.contains(mode)) {
            return null;
        }
        Integer exponent = CurrencyUtils.minorUnitExponent(currency);
        if (exponent == null) {
            return null;
        }
        double major = minor / Math.pow(10, exponent);
        return formatInternal(major, currency, locale, mode, exponent, exponent, null, null);
    }

    public static String formatMoneyOptions(
            double value, String currency, String locale, String mode,
            int minFraction, int maxFraction, String sign, boolean useGrouping) {
        return formatInternal(value, currency, locale, mode, minFraction, maxFraction, sign, useGrouping);
    }
}
