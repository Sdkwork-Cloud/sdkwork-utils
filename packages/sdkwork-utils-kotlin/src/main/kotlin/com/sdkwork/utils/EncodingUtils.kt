package com.sdkwork.utils

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Base64

object EncodingUtils {
    fun base64Encode(value: ByteArray): String {
        return Base64.getEncoder().encodeToString(value)
    }

    fun base64Decode(value: String): ByteArray? {
        return try {
            Base64.getDecoder().decode(value.trim())
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    fun hexEncode(value: ByteArray): String {
        val builder = StringBuilder(value.size * 2)
        for (current in value) {
            builder.append(String.format("%02x", current))
        }
        return builder.toString()
    }

    fun hexDecode(value: String): ByteArray? {
        val trimmed = value.trim()
        if (trimmed.length % 2 != 0) {
            return null
        }
        val result = ByteArray(trimmed.length / 2)
        var index = 0
        while (index < trimmed.length) {
            try {
                result[index / 2] = trimmed.substring(index, index + 2).toInt(16).toByte()
            } catch (_: NumberFormatException) {
                return null
            }
            index += 2
        }
        return result
    }

    fun urlEncode(value: String): String {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20")
    }

    fun urlDecode(value: String): String? {
        return try {
            URLDecoder.decode(value, StandardCharsets.UTF_8)
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    fun base64urlEncode(value: ByteArray): String {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value)
    }

    fun base64urlDecode(value: String): ByteArray? {
        return try {
            Base64.getUrlDecoder().decode(value.trim())
        } catch (_: IllegalArgumentException) {
            null
        }
    }
}
