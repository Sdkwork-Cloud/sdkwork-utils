package com.sdkwork.utils

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DateTimeUtils {
    const val DEFAULT_PATTERN = "iso8601"
    private val ISO8601_MILLIS: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC)

    fun now(): Instant {
        return Instant.now()
    }

    fun formatDatetime(value: Instant, pattern: String?): String {
        var effectivePattern = pattern
        if (effectivePattern == null) {
            effectivePattern = DEFAULT_PATTERN
        }
        if (effectivePattern != DEFAULT_PATTERN) {
            throw IllegalArgumentException("Unsupported datetime pattern: $effectivePattern")
        }
        return ISO8601_MILLIS.format(value)
    }

    fun parseDatetime(value: String, pattern: String?): Instant? {
        var effectivePattern = pattern
        if (effectivePattern == null) {
            effectivePattern = DEFAULT_PATTERN
        }
        if (effectivePattern != DEFAULT_PATTERN) {
            return null
        }
        return try {
            Instant.parse(value.trim())
        } catch (_: Exception) {
            null
        }
    }

    fun addDays(value: Instant, days: Long): Instant {
        return value.plus(days, ChronoUnit.DAYS)
    }

    fun addHours(value: Instant, hours: Long): Instant {
        return value.plus(hours, ChronoUnit.HOURS)
    }

    fun addMinutes(value: Instant, minutes: Long): Instant {
        return value.plus(minutes, ChronoUnit.MINUTES)
    }

    fun diffMillis(earlier: Instant, later: Instant): Long {
        return later.toEpochMilli() - earlier.toEpochMilli()
    }

    fun isBefore(first: Instant, second: Instant): Boolean {
        return first.isBefore(second)
    }

    fun isAfter(first: Instant, second: Instant): Boolean {
        return first.isAfter(second)
    }

    fun startOfDayUtc(value: Instant): Instant {
        return value.atZone(ZoneOffset.UTC).truncatedTo(ChronoUnit.DAYS).toInstant()
    }

    fun endOfDayUtc(value: Instant): Instant {
        return startOfDayUtc(value).plus(1, ChronoUnit.DAYS).minusMillis(1)
    }

    fun toUnixMillis(value: Instant): Long {
        return value.toEpochMilli()
    }

    fun fromUnixMillis(value: Long): Instant {
        return Instant.ofEpochMilli(value)
    }

    fun isSameInstant(first: Instant, second: Instant): Boolean {
        return first.toEpochMilli() == second.toEpochMilli()
    }
}
