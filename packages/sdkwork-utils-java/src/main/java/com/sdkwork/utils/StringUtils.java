package com.sdkwork.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class StringUtils {
    private static final Pattern WORD_SPLIT = Pattern.compile("[^a-zA-Z0-9]+");
    private static final Pattern CAMEL_BOUNDARY =
            Pattern.compile("(?<=[a-z0-9])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])");

    private StringUtils() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static String trim(String value) {
        return value == null ? "" : value.trim();
    }

    public static String truncate(String value, int maxLen, String suffix) {
        if (maxLen <= 0) {
            return "";
        }
        if (suffix == null) {
            suffix = "...";
        }
        if (value.length() <= maxLen) {
            return value;
        }
        if (suffix.length() >= maxLen) {
            return suffix.substring(0, maxLen);
        }
        return value.substring(0, maxLen - suffix.length()) + suffix;
    }

    public static String capitalize(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return value.substring(0, 1).toUpperCase(Locale.ROOT)
                + value.substring(1).toLowerCase(Locale.ROOT);
    }

    public static String camelCase(String value) {
        List<String> parts = camelParts(value);
        if (parts.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder(parts.get(0));
        for (int index = 1; index < parts.size(); index++) {
            builder.append(capitalize(parts.get(index)));
        }
        return builder.toString();
    }

    public static String snakeCase(String value) {
        return String.join("_", camelParts(value));
    }

    public static String kebabCase(String value) {
        return String.join("-", camelParts(value));
    }

    public static String slugify(String value) {
        return kebabCase(value).replaceAll("[^a-z0-9-]", "").replaceAll("^-+|-+$", "");
    }

    public static String mask(String value, int visibleStart, int visibleEnd) {
        return mask(value, visibleStart, visibleEnd, '*');
    }

    public static String mask(String value, int visibleStart, int visibleEnd, char maskChar) {
        if (maskChar == 0) {
            maskChar = '*';
        }
        if (visibleStart + visibleEnd >= value.length()) {
            return value;
        }
        char[] chars = value.toCharArray();
        for (int index = visibleStart; index < value.length() - visibleEnd; index++) {
            chars[index] = maskChar;
        }
        return new String(chars);
    }

    public static String padStart(String value, int targetLen) {
        return padStart(value, targetLen, ' ');
    }

    public static String padStart(String value, int targetLen, char padChar) {
        if (padChar == 0) {
            padChar = ' ';
        }
        if (value.length() >= targetLen) {
            return value;
        }
        return String.valueOf(padChar).repeat(targetLen - value.length()) + value;
    }

    public static String padEnd(String value, int targetLen) {
        return padEnd(value, targetLen, ' ');
    }

    public static String padEnd(String value, int targetLen, char padChar) {
        if (padChar == 0) {
            padChar = ' ';
        }
        if (value.length() >= targetLen) {
            return value;
        }
        return value + String.valueOf(padChar).repeat(targetLen - value.length());
    }

    public static boolean startsWith(String value, String prefix) {
        return value.startsWith(prefix);
    }

    public static boolean endsWith(String value, String suffix) {
        return value.endsWith(suffix);
    }

    public static boolean contains(String value, String substring) {
        return value.contains(substring);
    }

    public static String replaceAll(String value, String search, String replacement) {
        return value.replace(search, replacement);
    }

    public static List<String> split(String value, String delimiter, Boolean trimParts) {
        boolean shouldTrim = trimParts == null || trimParts;
        List<String> parts = new ArrayList<>();
        for (String part : value.split(java.util.regex.Pattern.quote(delimiter), -1)) {
            String current = shouldTrim ? part.trim() : part;
            if (!shouldTrim || !current.isEmpty()) {
                parts.add(current);
            }
        }
        return parts;
    }

    public static String join(List<String> parts, String separator) {
        return String.join(separator, parts);
    }

    public static String repeat(String value, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("repeat count must be >= 0");
        }
        return value.repeat(count);
    }

    public static String normalizeWhitespace(String value) {
        return trim(value).replaceAll("\\s+", " ");
    }

    private static final Pattern TEMPLATE_KEY = Pattern.compile("\\{([a-zA-Z_][a-zA-Z0-9_]*)\\}");

    public static String template(String pattern, Map<String, String> values) {
        Matcher matcher = TEMPLATE_KEY.matcher(pattern);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            String replacement = values.get(key);
            matcher.appendReplacement(
                    buffer,
                    Matcher.quoteReplacement(replacement != null ? replacement : matcher.group(0)));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static List<String> camelParts(String value) {
        String normalized = CAMEL_BOUNDARY.matcher(trim(value)).replaceAll(" ");
        return Arrays.stream(WORD_SPLIT.split(normalized))
                .filter(part -> !part.isEmpty())
                .map(part -> part.toLowerCase(Locale.ROOT))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
