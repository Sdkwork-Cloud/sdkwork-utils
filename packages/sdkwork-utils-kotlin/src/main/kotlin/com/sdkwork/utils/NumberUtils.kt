package com.sdkwork.utils

object NumberUtils {
    fun clamp(value: Double, min: Double, max: Double): Double {
        return minOf(max, maxOf(min, value))
    }

    fun round(value: Double, decimals: Int): Double {
        val factor = Math.pow(10.0, decimals.toDouble())
        return Math.round(value * factor) / factor
    }

    fun formatNumber(value: Double, decimals: Int): String {
        return String.format("%.${decimals}f", value)
    }

    fun parseNumber(value: String): Double? {
        return try {
            value.trim().toDouble()
        } catch (_: NumberFormatException) {
            null
        }
    }

    fun isInteger(value: Double): Boolean {
        return value.isFinite() && value == Math.rint(value)
    }

    fun parseInt(value: String): Long? {
        return try {
            value.trim().toLong()
        } catch (_: NumberFormatException) {
            null
        }
    }

    fun percentFormat(value: Double, decimals: Int): String {
        return "${formatNumber(value * 100.0, decimals)}%"
    }

    fun inRange(value: Double, min: Double, max: Double): Boolean {
        return value in min..max
    }

    fun abs(value: Double): Double = kotlin.math.abs(value)
}
