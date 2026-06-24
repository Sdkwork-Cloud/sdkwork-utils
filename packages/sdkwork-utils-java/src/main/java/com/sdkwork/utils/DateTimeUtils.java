package com.sdkwork.utils;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public final class DateTimeUtils {
    public static final String DEFAULT_PATTERN = "iso8601";
    private static final DateTimeFormatter ISO8601_MILLIS =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);

    private DateTimeUtils() {
    }

    public static Instant now() {
        return Instant.now();
    }

    public static String formatDatetime(Instant value, String pattern) {
        if (pattern == null) {
            pattern = DEFAULT_PATTERN;
        }
        if (!DEFAULT_PATTERN.equals(pattern)) {
            throw new IllegalArgumentException("Unsupported datetime pattern: " + pattern);
        }
        return ISO8601_MILLIS.format(value);
    }

    public static Instant parseDatetime(String value, String pattern) {
        if (pattern == null) {
            pattern = DEFAULT_PATTERN;
        }
        if (!DEFAULT_PATTERN.equals(pattern)) {
            return null;
        }
        try {
            return Instant.parse(value.trim());
        } catch (Exception ex) {
            return null;
        }
    }

    public static Instant addDays(Instant value, long days) {
        return value.plus(days, ChronoUnit.DAYS);
    }

    public static Instant addHours(Instant value, long hours) {
        return value.plus(hours, ChronoUnit.HOURS);
    }

    public static Instant addMinutes(Instant value, long minutes) {
        return value.plus(minutes, ChronoUnit.MINUTES);
    }

    public static long diffMillis(Instant earlier, Instant later) {
        return later.toEpochMilli() - earlier.toEpochMilli();
    }

    public static boolean isBefore(Instant first, Instant second) {
        return first.isBefore(second);
    }

    public static boolean isAfter(Instant first, Instant second) {
        return first.isAfter(second);
    }

    public static Instant startOfDayUtc(Instant value) {
        return value.atZone(ZoneOffset.UTC).truncatedTo(ChronoUnit.DAYS).toInstant();
    }

    public static Instant endOfDayUtc(Instant value) {
        return startOfDayUtc(value).plus(1, ChronoUnit.DAYS).minusMillis(1);
    }

    public static long toUnixMillis(Instant value) {
        return value.toEpochMilli();
    }

    public static Instant fromUnixMillis(long value) {
        return Instant.ofEpochMilli(value);
    }

    public static boolean isSameInstant(Instant first, Instant second) {
        return first.toEpochMilli() == second.toEpochMilli();
    }
}
