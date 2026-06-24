package com.sdkwork.utils;

import java.util.regex.Pattern;

public final class ValidationUtils {
    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$");
    private static final Pattern UUID = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$");
    private static final Pattern URL = Pattern.compile("^https?://[^\\s/$.?#].[^\\s]*$");
    private static final Pattern IPV4 = Pattern.compile(
            "^(25[0-5]|2[0-4]\\d|1?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|1?\\d?\\d)){3}$");
    private static final Pattern E164 = Pattern.compile("^\\+[1-9]\\d{1,14}$");

    private ValidationUtils() {
    }

    public static boolean isEmail(String value) {
        return EMAIL.matcher(value.trim()).matches();
    }

    public static boolean isUuid(String value) {
        return UUID.matcher(value.trim()).matches();
    }

    public static boolean isUrl(String value) {
        return URL.matcher(value.trim()).matches();
    }

    public static boolean isNumeric(String value) {
        return NumberUtils.parseNumber(value) != null;
    }

    public static boolean isIpv4(String value) {
        return IPV4.matcher(value.trim()).matches();
    }

    public static boolean isIpv6(String value) {
        return isIpv6Shape(value.trim());
    }

    public static boolean isPhoneE164(String value) {
        return E164.matcher(value.trim()).matches();
    }

    private static boolean isIpv6Shape(String value) {
        if (value.isEmpty() || !value.matches("[0-9a-fA-F:]+")) {
            return false;
        }
        if (value.split("::", -1).length > 2) {
            return false;
        }
        if (value.contains("::")) {
            String[] parts = value.split("::", 2);
            String[] leftParts = parts[0].isEmpty() ? new String[0] : parts[0].split(":");
            String[] rightParts = parts[1].isEmpty() ? new String[0] : parts[1].split(":");
            int segmentCount = 0;
            for (String part : leftParts) {
                if (!part.isEmpty()) {
                    if (!isIpv6Part(part)) {
                        return false;
                    }
                    segmentCount++;
                }
            }
            for (String part : rightParts) {
                if (!part.isEmpty()) {
                    if (!isIpv6Part(part)) {
                        return false;
                    }
                    segmentCount++;
                }
            }
            return segmentCount < 8;
        }
        String[] segments = value.split(":");
        if (segments.length != 8) {
            return false;
        }
        for (String segment : segments) {
            if (!isIpv6Part(segment)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isIpv6Part(String part) {
        return !part.isEmpty() && part.length() <= 4;
    }
}
