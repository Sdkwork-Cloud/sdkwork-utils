package com.sdkwork.utils

import java.security.SecureRandom
import java.util.UUID

object IdUtils {
    private val RANDOM = SecureRandom()
    private const val ALPHANUMERIC =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

    fun uuid(): String {
        return UUID.randomUUID().toString()
    }

    fun randomString(length: Int): String {
        val builder = StringBuilder(length)
        for (index in 0 until length) {
            builder.append(ALPHANUMERIC[RANDOM.nextInt(ALPHANUMERIC.length)])
        }
        return builder.toString()
    }
}
