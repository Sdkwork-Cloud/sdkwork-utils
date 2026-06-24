package com.sdkwork.utils;

public final class NumberUtils {
    private NumberUtils() {
    }

    public static double clamp(double value, double min, double max) {
        return Math.min(max, Math.max(min, value));
    }

    public static double round(double value, int decimals) {
        double factor = Math.pow(10, decimals);
        return Math.round(value * factor) / factor;
    }

    public static String formatNumber(double value, int decimals) {
        return String.format("%." + decimals + "f", value);
    }

    public static Double parseNumber(String value) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static boolean isInteger(double value) {
        return Double.isFinite(value) && value == Math.rint(value);
    }

    public static Long parseInt(String value) {
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static String percentFormat(double value, int decimals) {
        return formatNumber(value * 100.0, decimals) + "%";
    }

    public static boolean inRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    public static double abs(double value) {
        return Math.abs(value);
    }
}
