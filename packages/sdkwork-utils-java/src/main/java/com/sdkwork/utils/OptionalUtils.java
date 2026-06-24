package com.sdkwork.utils;

public final class OptionalUtils {
    private OptionalUtils() {
    }

    public static String coalesce(String... values) {
        for (String value : values) {
            if (!StringUtils.isBlank(value)) {
                return value.trim();
            }
        }
        return null;
    }

    public static String defaultIfBlank(String value, String defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : value.trim();
    }
}
