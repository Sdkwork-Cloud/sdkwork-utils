package com.sdkwork.utils;

import java.util.Locale;
import java.util.Map;

public final class CurrencyUtils {
    private static final Map<String, CurrencyMeta> KNOWN = Map.ofEntries(
            Map.entry("USD", new CurrencyMeta(2, "$")),
            Map.entry("EUR", new CurrencyMeta(2, "€")),
            Map.entry("GBP", new CurrencyMeta(2, "£")),
            Map.entry("CNY", new CurrencyMeta(2, "¥")),
            Map.entry("JPY", new CurrencyMeta(0, "¥")),
            Map.entry("KRW", new CurrencyMeta(0, "₩")),
            Map.entry("HKD", new CurrencyMeta(2, "HK$")),
            Map.entry("TWD", new CurrencyMeta(2, "NT$")),
            Map.entry("CHF", new CurrencyMeta(2, "CHF")),
            Map.entry("CAD", new CurrencyMeta(2, "CA$")),
            Map.entry("AUD", new CurrencyMeta(2, "A$")),
            Map.entry("INR", new CurrencyMeta(2, "₹")),
            Map.entry("BHD", new CurrencyMeta(3, "BHD")),
            Map.entry("KWD", new CurrencyMeta(3, "KWD"))
    );

    private CurrencyUtils() {
    }

    private record CurrencyMeta(int exponent, String symbol) {
    }

    private static CurrencyMeta lookup(String code) {
        if (code == null) {
            return null;
        }
        String normalized = code.trim();
        if (normalized.length() != 3 || !normalized.equals(normalized.toUpperCase(Locale.ROOT))
                || !normalized.chars().allMatch(Character::isLetter)) {
            return null;
        }
        return KNOWN.get(normalized);
    }

    public static boolean isCurrencyCode(String value) {
        return lookup(value) != null;
    }

    public static Integer minorUnitExponent(String code) {
        CurrencyMeta meta = lookup(code);
        return meta == null ? null : meta.exponent();
    }

    public static Long toMinorUnits(double amount, String code) {
        CurrencyMeta meta = lookup(code);
        if (meta == null) {
            return null;
        }
        double factor = Math.pow(10, meta.exponent());
        return (long) NumberUtils.round(amount * factor, 0);
    }

    public static Double fromMinorUnits(long minor, String code) {
        CurrencyMeta meta = lookup(code);
        if (meta == null) {
            return null;
        }
        double factor = Math.pow(10, meta.exponent());
        return minor / factor;
    }

    private static boolean suffixLocale(String locale) {
        String normalized = locale.toLowerCase(Locale.ROOT);
        return normalized.startsWith("de")
                || normalized.startsWith("fr")
                || normalized.startsWith("it")
                || normalized.startsWith("es");
    }

    public static String formatCurrency(double amount, String code, String locale) {
        CurrencyMeta meta = lookup(code);
        if (meta == null) {
            return null;
        }
        String formatted = I18nUtils.formatNumberLocale(amount, locale, meta.exponent());
        return suffixLocale(locale) ? formatted + " " + meta.symbol() : meta.symbol() + formatted;
    }
}
