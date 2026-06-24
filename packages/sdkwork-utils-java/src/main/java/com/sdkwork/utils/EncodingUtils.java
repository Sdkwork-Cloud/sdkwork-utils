package com.sdkwork.utils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class EncodingUtils {
    private EncodingUtils() {
    }

    public static String base64Encode(byte[] value) {
        return Base64.getEncoder().encodeToString(value);
    }

    public static byte[] base64Decode(String value) {
        try {
            return Base64.getDecoder().decode(value.trim());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public static String hexEncode(byte[] value) {
        StringBuilder builder = new StringBuilder(value.length * 2);
        for (byte current : value) {
            builder.append(String.format("%02x", current));
        }
        return builder.toString();
    }

    public static byte[] hexDecode(String value) {
        String trimmed = value.trim();
        if (trimmed.length() % 2 != 0) {
            return null;
        }
        byte[] result = new byte[trimmed.length() / 2];
        for (int index = 0; index < trimmed.length(); index += 2) {
            try {
                result[index / 2] = (byte) Integer.parseInt(trimmed.substring(index, index + 2), 16);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return result;
    }

    public static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    public static String urlDecode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public static String base64urlEncode(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }

    public static byte[] base64urlDecode(String value) {
        try {
            return Base64.getUrlDecoder().decode(value.trim());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
