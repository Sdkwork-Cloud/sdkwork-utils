package com.sdkwork.utils

object CurrencyUtils {
    private data class CurrencyMeta(val exponent: Int, val symbol: String)

    private val known = mapOf(
        "USD" to CurrencyMeta(2, "$"),
        "EUR" to CurrencyMeta(2, "€"),
        "GBP" to CurrencyMeta(2, "£"),
        "CNY" to CurrencyMeta(2, "¥"),
        "JPY" to CurrencyMeta(0, "¥"),
        "KRW" to CurrencyMeta(0, "₩"),
        "HKD" to CurrencyMeta(2, "HK$"),
        "TWD" to CurrencyMeta(2, "NT$"),
        "CHF" to CurrencyMeta(2, "CHF"),
        "CAD" to CurrencyMeta(2, "CA$"),
        "AUD" to CurrencyMeta(2, "A$"),
        "INR" to CurrencyMeta(2, "₹"),
        "BHD" to CurrencyMeta(3, "BHD"),
        "KWD" to CurrencyMeta(3, "KWD"),
    )

    private fun lookup(code: String): CurrencyMeta? {
        val normalized = code.trim()
        if (normalized.length != 3 || normalized != normalized.uppercase() || !normalized.all { it.isLetter() }) {
            return null
        }
        return known[normalized]
    }

    fun isCurrencyCode(value: String): Boolean = lookup(value) != null

    fun minorUnitExponent(code: String): Int? = lookup(code)?.exponent

    fun toMinorUnits(amount: Double, code: String): Long? {
        val meta = lookup(code) ?: return null
        val factor = Math.pow(10.0, meta.exponent.toDouble())
        return NumberUtils.round(amount * factor, 0).toLong()
    }

    fun fromMinorUnits(minor: Long, code: String): Double? {
        val meta = lookup(code) ?: return null
        val factor = Math.pow(10.0, meta.exponent.toDouble())
        return minor / factor
    }

    private fun suffixLocale(locale: String): Boolean {
        val normalized = locale.lowercase()
        return normalized.startsWith("de") || normalized.startsWith("fr") ||
            normalized.startsWith("it") || normalized.startsWith("es")
    }

    fun formatCurrency(amount: Double, code: String, locale: String): String? {
        val meta = lookup(code) ?: return null
        val formatted = I18nUtils.formatNumberLocale(amount, locale, meta.exponent)
        return if (suffixLocale(locale)) "$formatted ${meta.symbol}" else "${meta.symbol}$formatted"
    }
}
