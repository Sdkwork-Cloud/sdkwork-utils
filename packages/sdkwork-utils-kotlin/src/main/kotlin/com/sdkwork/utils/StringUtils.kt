package com.sdkwork.utils

import java.util.Locale
import java.util.regex.Pattern

object StringUtils {
    private val WORD_SPLIT = Pattern.compile("[^a-zA-Z0-9]+")
    private val CAMEL_BOUNDARY =
        Pattern.compile("(?<=[a-z0-9])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])")

    fun isBlank(value: String?): Boolean {
        return value == null || value.trim().isEmpty()
    }

    fun trim(value: String?): String {
        return value?.trim() ?: ""
    }

    fun truncate(value: String, maxLen: Int, suffix: String?): String {
        if (maxLen <= 0) {
            return ""
        }
        var effectiveSuffix = suffix
        if (effectiveSuffix == null) {
            effectiveSuffix = "..."
        }
        if (value.length <= maxLen) {
            return value
        }
        if (effectiveSuffix.length >= maxLen) {
            return effectiveSuffix.substring(0, maxLen)
        }
        return value.substring(0, maxLen - effectiveSuffix.length) + effectiveSuffix
    }

    fun capitalize(value: String?): String {
        if (value == null || value.isEmpty()) {
            return ""
        }
        return value.substring(0, 1).uppercase(Locale.ROOT) +
            value.substring(1).lowercase(Locale.ROOT)
    }

    fun camelCase(value: String): String {
        val parts = camelParts(value)
        if (parts.isEmpty()) {
            return ""
        }
        val builder = StringBuilder(parts[0])
        for (index in 1 until parts.size) {
            builder.append(capitalize(parts[index]))
        }
        return builder.toString()
    }

    fun snakeCase(value: String): String {
        return camelParts(value).joinToString("_")
    }

    fun kebabCase(value: String): String {
        return camelParts(value).joinToString("-")
    }

    fun slugify(value: String): String {
        return kebabCase(value).replace(Regex("[^a-z0-9-]"), "").replace(Regex("^-+|-+$"), "")
    }

    fun mask(value: String, visibleStart: Int, visibleEnd: Int, maskChar: Char = '*'): String {
        val effectiveMaskChar = if (maskChar.code == 0) '*' else maskChar
        if (visibleStart + visibleEnd >= value.length) {
            return value
        }
        val chars = value.toCharArray()
        for (index in visibleStart until value.length - visibleEnd) {
            chars[index] = effectiveMaskChar
        }
        return String(chars)
    }

    fun padStart(value: String, targetLen: Int, padChar: Char = ' '): String {
        val effectivePadChar = if (padChar.code == 0) ' ' else padChar
        if (value.length >= targetLen) {
            return value
        }
        return effectivePadChar.toString().repeat(targetLen - value.length) + value
    }

    fun padEnd(value: String, targetLen: Int, padChar: Char = ' '): String {
        val effectivePadChar = if (padChar.code == 0) ' ' else padChar
        if (value.length >= targetLen) {
            return value
        }
        return value + effectivePadChar.toString().repeat(targetLen - value.length)
    }

    fun startsWith(value: String, prefix: String): Boolean {
        return value.startsWith(prefix)
    }

    fun endsWith(value: String, suffix: String): Boolean {
        return value.endsWith(suffix)
    }

    fun contains(value: String, substring: String): Boolean {
        return value.contains(substring)
    }

    fun replaceAll(value: String, search: String, replacement: String): String {
        return value.replace(search, replacement)
    }

    fun split(value: String, delimiter: String, trimParts: Boolean? = true): List<String> {
        val shouldTrim = trimParts ?: true
        val parts = value.split(delimiter)
        val result = ArrayList<String>()
        for (part in parts) {
            val current = if (shouldTrim) part.trim() else part
            if (!shouldTrim || current.isNotEmpty()) {
                result.add(current)
            }
        }
        return result
    }

    fun join(parts: List<String>, separator: String): String {
        return parts.joinToString(separator)
    }

    fun repeat(value: String, count: Int): String {
        require(count >= 0) { "repeat count must be >= 0" }
        return value.repeat(count)
    }

    fun normalizeWhitespace(value: String): String =
        trim(value).split(Regex("\\s+")).filter { it.isNotEmpty() }.joinToString(" ")

    private val TEMPLATE_KEY = Regex("\\{([a-zA-Z_][a-zA-Z0-9_]*)\\}")

    fun template(pattern: String, values: Map<String, String>): String {
        return TEMPLATE_KEY.replace(pattern) { match ->
            val key = match.groupValues[1]
            values[key] ?: match.value
        }
    }

    private fun camelParts(value: String): List<String> {
        val normalized = CAMEL_BOUNDARY.matcher(trim(value)).replaceAll(" ")
        return WORD_SPLIT.split(normalized)
            .filter { it.isNotEmpty() }
            .map { it.lowercase(Locale.ROOT) }
    }
}
