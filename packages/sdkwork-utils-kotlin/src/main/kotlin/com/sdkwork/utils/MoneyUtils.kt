package com.sdkwork.utils

import java.math.BigDecimal
import java.util.Locale

/**
 * Money display formatting aligned with industry Intl.NumberFormat conventions.
 *
 * Modes: symbol, narrow_symbol, code, name, decimal, accounting, compact.
 * Symbol placement: en-US/zh-CN/ja-JP/ko-KR prefix without space; de-DE/fr-FR/it-IT/es-ES/ru-RU
 * suffix with a single space. Rounding is half-up on the shortest decimal representation.
 */
object MoneyUtils {
    private data class CompactUnit(val exponent: Int, val unit: String)

    private data class LocaleRules(
        val prefix: Boolean,
        val decimal: String,
        val grouping: String,
        val nameSpace: Boolean,
        val compact: List<CompactUnit>,
    )

    private val modes = setOf("symbol", "narrow_symbol", "code", "name", "decimal", "accounting", "compact")
    private val signs = setOf("auto", "always", "never", "except_zero")

    private val currencySymbols = mapOf(
        "USD" to "$",
        "EUR" to "€",
        "GBP" to "£",
        "CNY" to "¥",
        "JPY" to "¥",
        "KRW" to "₩",
        "HKD" to "HK$",
        "TWD" to "NT$",
        "CHF" to "CHF",
        "CAD" to "CA$",
        "AUD" to "A$",
        "INR" to "₹",
        "BHD" to "BHD",
        "KWD" to "KWD",
    )

    private val enCompact = listOf(CompactUnit(12, "T"), CompactUnit(9, "B"), CompactUnit(6, "M"), CompactUnit(3, "K"))
    private val zhCompact = listOf(CompactUnit(12, "兆"), CompactUnit(8, "亿"), CompactUnit(4, "万"))
    private val jaCompact = listOf(CompactUnit(12, "兆"), CompactUnit(8, "億"), CompactUnit(4, "万"))
    private val koCompact = listOf(CompactUnit(12, "조"), CompactUnit(8, "억"), CompactUnit(4, "만"))
    private val deCompact = listOf(CompactUnit(12, "Bio."), CompactUnit(9, "Mrd."), CompactUnit(6, "Mio."), CompactUnit(3, "Tsd."))
    private val frCompact = listOf(CompactUnit(12, "B"), CompactUnit(9, "Md"), CompactUnit(6, "M"), CompactUnit(3, "k"))
    private val itCompact = listOf(CompactUnit(12, "Bio."), CompactUnit(9, "Mrd."), CompactUnit(6, "M"), CompactUnit(3, "k"))
    private val esCompact = listOf(CompactUnit(12, "T"), CompactUnit(9, "B"), CompactUnit(6, "M"), CompactUnit(3, "k"))
    private val ruCompact = listOf(CompactUnit(12, "трлн"), CompactUnit(9, "млрд"), CompactUnit(6, "млн"), CompactUnit(3, "тыс."))

    private val localeRules = mapOf(
        "en-us" to LocaleRules(true, ".", ",", true, enCompact),
        "zh-cn" to LocaleRules(true, ".", ",", false, zhCompact),
        "ja-jp" to LocaleRules(true, ".", ",", false, jaCompact),
        "ko-kr" to LocaleRules(true, ".", ",", false, koCompact),
        "de-de" to LocaleRules(false, ",", ".", true, deCompact),
        "fr-fr" to LocaleRules(false, ",", " ", true, frCompact),
        "it-it" to LocaleRules(false, ",", ".", true, itCompact),
        "es-es" to LocaleRules(false, ",", ".", true, esCompact),
        "ru-ru" to LocaleRules(false, ",", " ", true, ruCompact),
    )

    private val currencyNames = mapOf(
        "en-us" to mapOf(
            "USD" to "US dollars", "EUR" to "euros", "GBP" to "British pounds", "CNY" to "Chinese yuan",
            "JPY" to "Japanese yen", "KRW" to "South Korean won", "HKD" to "Hong Kong dollars",
            "TWD" to "New Taiwan dollars", "CHF" to "Swiss francs", "CAD" to "Canadian dollars",
            "AUD" to "Australian dollars", "INR" to "Indian rupees", "BHD" to "Bahraini dinars",
            "KWD" to "Kuwaiti dinars",
        ),
        "zh-cn" to mapOf(
            "USD" to "美元", "EUR" to "欧元", "GBP" to "英镑", "CNY" to "人民币", "JPY" to "日元", "KRW" to "韩元",
            "HKD" to "港币", "TWD" to "新台币", "CHF" to "瑞士法郎", "CAD" to "加拿大元", "AUD" to "澳大利亚元",
            "INR" to "印度卢比", "BHD" to "巴林第纳尔", "KWD" to "科威特第纳尔",
        ),
        "de-de" to mapOf(
            "USD" to "US-Dollar", "EUR" to "Euro", "GBP" to "Britisches Pfund", "CNY" to "Chinesischer Yuan",
            "JPY" to "Japanischer Yen", "KRW" to "Südkoreanischer Won", "HKD" to "Hongkong-Dollar",
            "TWD" to "Neuer Taiwan-Dollar", "CHF" to "Schweizer Franken", "CAD" to "Kanadischer Dollar",
            "AUD" to "Australischer Dollar", "INR" to "Indische Rupie", "BHD" to "Bahrainischer Dinar",
            "KWD" to "Kuwaitischer Dinar",
        ),
        "fr-fr" to mapOf(
            "USD" to "dollar américain", "EUR" to "euro", "GBP" to "livre sterling", "CNY" to "yuan chinois",
            "JPY" to "yen japonais", "KRW" to "won sud-coréen", "HKD" to "dollar de Hong Kong",
            "TWD" to "nouveau dollar de Taïwan", "CHF" to "franc suisse", "CAD" to "dollar canadien",
            "AUD" to "dollar australien", "INR" to "roupie indienne", "BHD" to "dinar bahreïni",
            "KWD" to "dinar koweïtien",
        ),
        "it-it" to mapOf(
            "USD" to "dollaro statunitense", "EUR" to "euro", "GBP" to "sterlina britannica",
            "CNY" to "yuan cinese", "JPY" to "yen giapponese", "KRW" to "won sudcoreano",
            "HKD" to "dollaro di Hong Kong", "TWD" to "nuovo dollaro taiwanese", "CHF" to "franco svizzero",
            "CAD" to "dollaro canadese", "AUD" to "dollaro australiano", "INR" to "rupia indiana",
            "BHD" to "dinaro bahreinita", "KWD" to "dinaro kuwaitiano",
        ),
        "es-es" to mapOf(
            "USD" to "dólar estadounidense", "EUR" to "euro", "GBP" to "libra esterlina",
            "CNY" to "yuan chino", "JPY" to "yen japonés", "KRW" to "won surcoreano",
            "HKD" to "dólar de Hong Kong", "TWD" to "nuevo dólar taiwanés", "CHF" to "franco suizo",
            "CAD" to "dólar canadiense", "AUD" to "dólar australiano", "INR" to "rupia india",
            "BHD" to "dinar bahreiní", "KWD" to "dinar kuwaití",
        ),
        "ja-jp" to mapOf(
            "USD" to "米ドル", "EUR" to "ユーロ", "GBP" to "英ポンド", "CNY" to "中国人民元", "JPY" to "日本円",
            "KRW" to "韓国ウォン", "HKD" to "香港ドル", "TWD" to "台湾ドル", "CHF" to "スイスフラン",
            "CAD" to "カナダドル", "AUD" to "オーストラリアドル", "INR" to "インドルピー",
            "BHD" to "バーレーンディナール", "KWD" to "クウェートディナール",
        ),
        "ko-kr" to mapOf(
            "USD" to "미국 달러", "EUR" to "유로", "GBP" to "영국 파운드", "CNY" to "중국 위안", "JPY" to "일본 엔",
            "KRW" to "대한민국 원", "HKD" to "홍콩 달러", "TWD" to "신 대만 달러", "CHF" to "스위스 프랑",
            "CAD" to "캐나다 달러", "AUD" to "호주 달러", "INR" to "인도 루피", "BHD" to "바레인 디나르",
            "KWD" to "쿠웨이트 디나르",
        ),
        "ru-ru" to mapOf(
            "USD" to "доллар США", "EUR" to "евро", "GBP" to "британский фунт", "CNY" to "китайский юань",
            "JPY" to "японская иена", "KRW" to "южнокорейская вона", "HKD" to "гонконгский доллар",
            "TWD" to "новый тайваньский доллар", "CHF" to "швейцарский франк", "CAD" to "канадский доллар",
            "AUD" to "австралийский доллар", "INR" to "индийская рупия", "BHD" to "бахрейнский динар",
            "KWD" to "кувейтский динар",
        ),
    )

    private fun lookupCurrency(currency: String?): String? {
        if (currency == null) {
            return null
        }
        val normalized = currency.trim()
        if (normalized.length != 3 || normalized != normalized.uppercase(Locale.ROOT) || !normalized.all { it.isLetter() }) {
            return null
        }
        return normalized.takeIf { currencySymbols.containsKey(it) }
    }

    private fun localeKey(locale: String?): String {
        if (locale == null) {
            return "en-us"
        }
        val normalized = locale.trim().lowercase(Locale.ROOT)
        if (localeRules.containsKey(normalized)) {
            return normalized
        }
        val language = normalized.split("-")[0]
        for (key in localeRules.keys) {
            if (key.split("-")[0] == language) {
                return key
            }
        }
        return "en-us"
    }

    private fun rules(locale: String?): LocaleRules = localeRules[localeKey(locale)] ?: localeRules.getValue("en-us")

    private fun names(locale: String?): Map<String, String> = currencyNames[localeKey(locale)] ?: currencyNames.getValue("en-us")

    private data class ParsedValue(val negative: Boolean, val isZero: Boolean, val absDecimal: String)

    private fun parseValue(value: Double?): ParsedValue? {
        if (value == null || value.isNaN() || value.isInfinite()) {
            return null
        }
        val negative = value < 0
        val abs = Math.abs(value)
        if (abs == 0.0) {
            return ParsedValue(false, true, "0")
        }
        return ParsedValue(negative, false, BigDecimal.valueOf(abs).toPlainString())
    }

    private fun splitDecimal(absDecimal: String): Pair<String, String> {
        val index = absDecimal.indexOf('.')
        return if (index < 0) {
            absDecimal to ""
        } else {
            absDecimal.substring(0, index) to absDecimal.substring(index + 1)
        }
    }

    private fun incrementDecimal(intPart: String, fracPart: String): Pair<String, String> {
        val digits = (intPart + fracPart).toCharArray()
        var index = digits.size - 1
        while (index >= 0 && digits[index] == '9') {
            digits[index] = '0'
            index--
        }
        val incremented: String = if (index >= 0) {
            digits[index] = (digits[index] + 1)
            String(digits)
        } else {
            "1" + String(digits)
        }
        val cut = incremented.length - fracPart.length
        return incremented.substring(0, cut) to incremented.substring(cut)
    }

    private fun roundDecimal(absDecimal: String, maxFraction: Int): Pair<String, String> {
        val (intPart, fracPart) = splitDecimal(absDecimal)
        if (fracPart.length <= maxFraction) {
            return intPart to fracPart.padEnd(maxFraction, '0')
        }
        val keep = fracPart.substring(0, maxFraction)
        return if (fracPart[maxFraction] >= '5') {
            incrementDecimal(intPart, keep)
        } else {
            intPart to keep
        }
    }

    private fun trimFraction(fracPart: String, minFraction: Int): String {
        var end = fracPart.length
        while (end > minFraction && fracPart[end - 1] == '0') {
            end--
        }
        return fracPart.substring(0, end)
    }

    private fun groupInteger(intPart: String, grouping: String, useGrouping: Boolean): String {
        if (!useGrouping) {
            return intPart
        }
        val grouped = StringBuilder()
        for (index in intPart.indices) {
            if (index > 0 && (intPart.length - index) % 3 == 0) {
                grouped.append(grouping)
            }
            grouped.append(intPart[index])
        }
        return grouped.toString()
    }

    private fun shiftDecimalPoint(absDecimal: String, exponent: Int): String {
        val (intPart, fracPart) = splitDecimal(absDecimal)
        val digits = intPart + fracPart
        val pointIndex = intPart.length - exponent
        return when {
            pointIndex <= 0 -> "0." + "0".repeat(-pointIndex) + digits
            pointIndex >= digits.length -> digits + "0".repeat(pointIndex - digits.length)
            else -> digits.substring(0, pointIndex) + "." + digits.substring(pointIndex)
        }
    }

    private fun formatCompactBody(parsed: ParsedValue, rules: LocaleRules): String {
        if (parsed.isZero) {
            return "0"
        }
        val intLength = splitDecimal(parsed.absDecimal).first.length
        var unitIndex = -1
        for (index in rules.compact.indices) {
            if (intLength > rules.compact[index].exponent) {
                unitIndex = index
                break
            }
        }
        if (unitIndex < 0) {
            val (roundedInt, roundedFrac) = roundDecimal(parsed.absDecimal, 1)
            val trimmed = trimFraction(roundedFrac, 0)
            return if (trimmed.isEmpty()) roundedInt else "$roundedInt.$trimmed"
        }
        var unit = rules.compact[unitIndex]
        var (scaledInt, scaledFrac) = roundDecimal(shiftDecimalPoint(parsed.absDecimal, unit.exponent), 1)
        if (scaledInt.length > 1 && unitIndex + 1 < rules.compact.size) {
            val nextUnit = rules.compact[unitIndex + 1]
            val escalated = roundDecimal(shiftDecimalPoint(parsed.absDecimal, nextUnit.exponent), 1)
            scaledInt = escalated.first
            scaledFrac = escalated.second
            unit = nextUnit
        }
        val trimmed = trimFraction(scaledFrac, 0)
        return (if (trimmed.isEmpty()) scaledInt else "$scaledInt.$trimmed") + unit.unit
    }

    private fun signPrefix(negative: Boolean, isZero: Boolean, sign: String): String = when (sign) {
        "always" -> if (negative) "-" else "+"
        "never" -> ""
        "except_zero" -> if (negative) "-" else if (isZero) "" else "+"
        else -> if (negative) "-" else ""
    }

    private fun defaultFraction(mode: String): Pair<Int, Int> = when (mode) {
        "compact" -> 0 to 1
        "decimal" -> 0 to 2
        else -> 2 to 2
    }

    private fun formatInternal(
        value: Double?,
        currency: String?,
        locale: String?,
        mode: String?,
        minFraction: Int?,
        maxFraction: Int?,
        sign: String?,
        useGrouping: Boolean?,
    ): String? {
        val code = lookupCurrency(currency) ?: return null
        if (mode == null || mode !in modes) {
            return null
        }
        if (sign != null && sign !in signs) {
            return null
        }
        val parsed = parseValue(value) ?: return null

        val (resolvedMin, resolvedMax) = if (minFraction == null && maxFraction == null) {
            defaultFraction(mode)
        } else if (minFraction == null || maxFraction == null) {
            return null
        } else {
            if (minFraction < 0 || maxFraction > 18 || minFraction > maxFraction) {
                return null
            }
            if (mode == "compact") 0 to 1 else minFraction to maxFraction
        }

        val resolvedGrouping = useGrouping ?: true
        val resolvedSign = sign ?: "auto"
        val rules = rules(locale)
        val symbol = currencySymbols.getValue(code)

        if (mode == "compact") {
            val body = formatCompactBody(parsed, rules)
            val signText = signPrefix(parsed.negative, parsed.isZero, resolvedSign)
            return if (rules.prefix) "$signText$symbol$body" else "$signText$body $symbol"
        }

        val (roundedInt, roundedFrac) = roundDecimal(parsed.absDecimal, resolvedMax)
        val trimmed = trimFraction(roundedFrac, resolvedMin)
        val grouped = groupInteger(roundedInt, rules.grouping, resolvedGrouping)
        val body = if (trimmed.isEmpty()) grouped else grouped + rules.decimal + trimmed
        val signText = signPrefix(parsed.negative, parsed.isZero, resolvedSign)

        if (mode == "decimal") {
            return signText + body
        }

        if (mode == "accounting") {
            if (parsed.negative && rules.prefix) {
                return "($symbol$body)"
            }
            if (parsed.negative && !rules.prefix) {
                return "-$body $symbol"
            }
            return if (rules.prefix) "$symbol$body" else "$body $symbol"
        }

        if (mode == "code") {
            return if (rules.prefix) "$signText$code $body" else "$signText$body $code"
        }

        if (mode == "name") {
            val name = names(locale)[code] ?: "US dollars"
            val separator = if (rules.nameSpace) " " else ""
            return signText + body + separator + name
        }

        return if (rules.prefix) "$signText$symbol$body" else "$signText$body $symbol"
    }

    fun moneySymbol(currency: String): String? {
        val code = lookupCurrency(currency) ?: return null
        return currencySymbols[code]
    }

    fun formatMoney(value: Double, currency: String, locale: String, mode: String): String? =
        formatInternal(value, currency, locale, mode, null, null, null, null)

    fun formatMoneyDigits(
        value: Double,
        currency: String,
        locale: String,
        mode: String,
        minFraction: Int,
        maxFraction: Int,
    ): String? = formatInternal(value, currency, locale, mode, minFraction, maxFraction, null, null)

    fun formatMoneyMinorUnits(minor: Long, currency: String, locale: String, mode: String): String? {
        if (mode == "compact" || mode !in modes) {
            return null
        }
        val exponent = CurrencyUtils.minorUnitExponent(currency) ?: return null
        val major = minor / Math.pow(10.0, exponent.toDouble())
        return formatInternal(major, currency, locale, mode, exponent, exponent, null, null)
    }

    fun formatMoneyOptions(
        value: Double,
        currency: String,
        locale: String,
        mode: String,
        minFraction: Int,
        maxFraction: Int,
        sign: String,
        useGrouping: Boolean,
    ): String? = formatInternal(value, currency, locale, mode, minFraction, maxFraction, sign, useGrouping)
}
