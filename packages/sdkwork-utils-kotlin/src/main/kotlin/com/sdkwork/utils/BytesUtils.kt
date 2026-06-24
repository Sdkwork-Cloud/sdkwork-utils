package com.sdkwork.utils

object BytesUtils {
    private val UNITS = arrayOf("B", "KB", "MB", "GB", "TB", "PB")

    fun formatBytes(bytes: Long, decimals: Int): String {
        val normalized = maxOf(0L, bytes)
        if (normalized < 1024L) {
            return "$normalized B"
        }

        var size = normalized.toDouble()
        var unitIndex = 0
        while (size >= 1024.0 && unitIndex < UNITS.lastIndex) {
            size /= 1024.0
            unitIndex++
        }

        return "%.${decimals}f ${UNITS[unitIndex]}".format(size)
    }
}
