package com.sdkwork.utils;

import java.util.Locale;

public final class BytesUtils {
    private static final String[] UNITS = {"B", "KB", "MB", "GB", "TB", "PB"};

    private BytesUtils() {
    }

    public static String formatBytes(long bytes, int decimals) {
        long normalized = Math.max(0L, bytes);
        if (normalized < 1024L) {
            return normalized + " B";
        }

        double size = normalized;
        int unitIndex = 0;
        while (size >= 1024.0 && unitIndex < UNITS.length - 1) {
            size /= 1024.0;
            unitIndex++;
        }

        return String.format(Locale.ROOT, "%." + decimals + "f %s", size, UNITS[unitIndex]);
    }
}
