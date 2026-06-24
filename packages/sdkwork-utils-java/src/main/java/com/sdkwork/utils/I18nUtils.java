package com.sdkwork.utils;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public final class I18nUtils {
    private I18nUtils() {
    }

    public static String formatNumberLocale(double value, String locale, int decimals) {
        boolean negative = value < 0;
        double absolute = Math.abs(NumberUtils.round(value, decimals));
        String decimalSeparator = "de-DE".equalsIgnoreCase(locale) ? "," : ".";
        String groupingSeparator = "de-DE".equalsIgnoreCase(locale) ? "." : ",";
        String formatted = String.format("%." + decimals + "f", absolute);
        String[] parts = formatted.split("\\.");
        String grouped = insertGrouping(parts[0], groupingSeparator);
        if (decimals > 0) {
            grouped = grouped + decimalSeparator + parts[1];
        }
        return negative ? "-" + grouped : grouped;
    }

    public static String formatDatetimeLocale(Instant value, String locale) {
        String normalized = locale == null ? "en-us" : locale.toLowerCase();
        DateTimeFormatter formatter;
        if (normalized.startsWith("de")) {
            formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneOffset.UTC);
        } else if (normalized.startsWith("zh")) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneOffset.UTC);
        } else {
            formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm").withZone(ZoneOffset.UTC);
        }
        return formatter.format(value);
    }

    private static String insertGrouping(String integer, String groupingSeparator) {
        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (int index = integer.length() - 1; index >= 0; index--) {
            if (count > 0 && count % 3 == 0) {
                builder.insert(0, groupingSeparator);
            }
            builder.insert(0, integer.charAt(index));
            count++;
        }
        return builder.toString();
    }

    public static Double parseNumberLocale(String input, String locale) {
        if (input == null) {
            return null;
        }
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        String decimalSeparator = "de-DE".equalsIgnoreCase(locale) ? "," : ".";
        String groupingSeparator = "de-DE".equalsIgnoreCase(locale) ? "." : ",";
        String normalized = trimmed.replace(groupingSeparator, "").replace(decimalSeparator, ".");
        return NumberUtils.parseNumber(normalized);
    }
}
