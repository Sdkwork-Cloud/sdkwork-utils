package com.sdkwork.utils

object PathUtils {
    fun joinPath(vararg segments: String): String {
        return segments
            .map { segment -> segment.replace(Regex("^/+|/+$"), "") }
            .filter { it.isNotEmpty() }
            .joinToString("/")
    }

    fun normalizePath(value: String): String {
        val joined = value.split("/")
            .filter { it.isNotEmpty() }
            .joinToString("/")
        return if (value.startsWith("/")) "/$joined" else joined
    }
}
