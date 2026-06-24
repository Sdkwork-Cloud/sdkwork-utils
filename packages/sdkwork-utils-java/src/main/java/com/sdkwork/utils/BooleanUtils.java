package com.sdkwork.utils;

import java.util.Locale;

public final class BooleanUtils {
    private BooleanUtils() {
    }

    public static Boolean parseBool(String value) {
        if (value == null) {
            return null;
        }
        switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "true":
            case "1":
            case "yes":
            case "on":
                return true;
            case "false":
            case "0":
            case "no":
            case "off":
                return false;
            default:
                return null;
        }
    }

    public static boolean isTruthy(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "false":
            case "0":
            case "no":
            case "off":
                return false;
            default:
                return true;
        }
    }
}
