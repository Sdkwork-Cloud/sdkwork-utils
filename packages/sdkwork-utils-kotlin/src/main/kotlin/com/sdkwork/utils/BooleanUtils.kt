package com.sdkwork.utils

import java.util.Locale

object BooleanUtils {
    fun parseBool(value: String?): Boolean? {
        if (value == null) {
            return null
        }
        return when (value.trim().lowercase(Locale.ROOT)) {
            "true", "1", "yes", "on" -> true
            "false", "0", "no", "off" -> false
            else -> null
        }
    }

    fun isTruthy(value: String?): Boolean {
        if (value == null || value.trim().isEmpty()) {
            return false
        }
        return when (value.trim().lowercase(Locale.ROOT)) {
            "false", "0", "no", "off" -> false
            else -> true
        }
    }
}
