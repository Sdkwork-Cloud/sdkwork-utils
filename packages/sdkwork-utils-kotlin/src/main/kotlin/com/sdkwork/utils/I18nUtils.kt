package com.sdkwork.utils

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object I18nUtils {
    fun formatNumberLocale(value: Double, locale: String, decimals: Int = 0): String {
        val negative = value < 0
        val absolute = kotlin.math.abs(NumberUtils.round(value, decimals))
        val decimalSeparator = if (locale.equals("de-DE", ignoreCase = true)) "," else "."
        val groupingSeparator = if (locale.equals("de-DE", ignoreCase = true)) "." else ","
        val formatted = "%.${decimals}f".format(absolute)
        val parts = formatted.split(".")
        val grouped = insertGrouping(parts[0], groupingSeparator)
        val result = if (decimals > 0) grouped + decimalSeparator + parts[1] else grouped
        return if (negative) "-$result" else result
    }

    fun formatDatetimeLocale(value: Instant, locale: String): String {
        val normalized = locale.lowercase()
        val formatter = when {
            normalized.startsWith("de") -> DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneOffset.UTC)
            normalized.startsWith("zh") -> DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneOffset.UTC)
            else -> DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm").withZone(ZoneOffset.UTC)
        }
        return formatter.format(value)
    }

    private fun insertGrouping(integer: String, groupingSeparator: String): String {
        val builder = StringBuilder()
        var count = 0
        for (index in integer.indices.reversed()) {
            if (count > 0 && count % 3 == 0) builder.insert(0, groupingSeparator)
            builder.insert(0, integer[index])
            count++
        }
        return builder.toString()
    }

    fun parseNumberLocale(input: String, locale: String): Double? {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return null
        }
        val decimalSeparator = if (locale.equals("de-DE", ignoreCase = true)) "," else "."
        val groupingSeparator = if (locale.equals("de-DE", ignoreCase = true)) "." else ","
        val normalized = trimmed.replace(groupingSeparator, "").replace(decimalSeparator, ".")
        return NumberUtils.parseNumber(normalized)
    }
}
