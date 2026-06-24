package com.sdkwork.utils

import java.util.regex.Pattern

object ValidationUtils {
    private val EMAIL =
        Pattern.compile("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$")
    private val UUID = Pattern.compile(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$"
    )
    private val URL = Pattern.compile("^https?://[^\\s/$.?#].[^\\s]*$")
    private val IPV4 = Pattern.compile(
        "^(25[0-5]|2[0-4]\\d|1?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|1?\\d?\\d)){3}$"
    )
    private val E164 = Pattern.compile("^\\+[1-9]\\d{1,14}$")

    fun isEmail(value: String): Boolean {
        return EMAIL.matcher(value.trim()).matches()
    }

    fun isUuid(value: String): Boolean {
        return UUID.matcher(value.trim()).matches()
    }

    fun isUrl(value: String): Boolean {
        return URL.matcher(value.trim()).matches()
    }

    fun isNumeric(value: String): Boolean {
        return NumberUtils.parseNumber(value) != null
    }

    fun isIpv4(value: String): Boolean {
        return IPV4.matcher(value.trim()).matches()
    }

    fun isIpv6(value: String): Boolean {
        return isIpv6Shape(value.trim())
    }

    fun isPhoneE164(value: String): Boolean {
        return E164.matcher(value.trim()).matches()
    }

    private fun isIpv6Shape(value: String): Boolean {
        if (value.isEmpty() || !value.matches(Regex("[0-9a-fA-F:]+"))) {
            return false
        }
        if (value.split("::").size > 2) {
            return false
        }
        if (value.contains("::")) {
            val parts = value.split("::", limit = 2)
            val leftParts = if (parts[0].isEmpty()) emptyList() else parts[0].split(":").filter { it.isNotEmpty() }
            val rightParts = if (parts[1].isEmpty()) emptyList() else parts[1].split(":").filter { it.isNotEmpty() }
            if (!leftParts.all { isIpv6Part(it) } || !rightParts.all { isIpv6Part(it) }) {
                return false
            }
            return leftParts.size + rightParts.size < 8
        }
        val segments = value.split(":")
        return segments.size == 8 && segments.all { isIpv6Part(it) }
    }

    private fun isIpv6Part(part: String): Boolean {
        return part.isNotEmpty() && part.length <= 4
    }
}
