package com.sdkwork.utils

object OptionalUtils {
    fun coalesce(vararg values: String?): String? {
        for (value in values) {
            if (!StringUtils.isBlank(value)) {
                return value!!.trim()
            }
        }
        return null
    }

    fun defaultIfBlank(value: String?, defaultValue: String): String {
        return if (StringUtils.isBlank(value)) defaultValue else value!!.trim()
    }
}
