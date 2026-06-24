package com.sdkwork.utils

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object CryptoUtils {
    fun sha256Hash(value: ByteArray): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            toHex(digest.digest(value))
        } catch (ex: Exception) {
            throw IllegalStateException("SHA-256 not available", ex)
        }
    }

    fun sha256Hash(value: String): String {
        return sha256Hash(value.toByteArray(StandardCharsets.UTF_8))
    }

    fun hmacSha256(value: ByteArray, secret: ByteArray): String {
        return try {
            val mac = Mac.getInstance("HmacSHA256")
            mac.init(SecretKeySpec(secret, "HmacSHA256"))
            toHex(mac.doFinal(value))
        } catch (ex: Exception) {
            throw IllegalStateException("HmacSHA256 not available", ex)
        }
    }

    fun hmacSha256(value: String, secret: String): String {
        return hmacSha256(
            value.toByteArray(StandardCharsets.UTF_8),
            secret.toByteArray(StandardCharsets.UTF_8)
        )
    }

    fun secureCompare(left: String, right: String): Boolean {
        if (left.length != right.length) {
            return false
        }
        var result = 0
        for (index in left.indices) {
            result = result or (left[index].code xor right[index].code)
        }
        return result == 0
    }

    private fun toHex(bytes: ByteArray): String {
        val builder = StringBuilder(bytes.size * 2)
        for (current in bytes) {
            builder.append(String.format("%02x", current))
        }
        return builder.toString()
    }
}
