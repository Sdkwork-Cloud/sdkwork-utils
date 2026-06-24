package com.sdkwork.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class CryptoUtils {
    private CryptoUtils() {
    }

    public static String sha256Hash(byte[] value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return toHex(digest.digest(value));
        } catch (Exception ex) {
            throw new IllegalStateException("SHA-256 not available", ex);
        }
    }

    public static String sha256Hash(String value) {
        return sha256Hash(value.getBytes(StandardCharsets.UTF_8));
    }

    public static String hmacSha256(byte[] value, byte[] secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return toHex(mac.doFinal(value));
        } catch (Exception ex) {
            throw new IllegalStateException("HmacSHA256 not available", ex);
        }
    }

    public static String hmacSha256(String value, String secret) {
        return hmacSha256(
                value.getBytes(StandardCharsets.UTF_8),
                secret.getBytes(StandardCharsets.UTF_8));
    }

    public static boolean secureCompare(String left, String right) {
        if (left.length() != right.length()) {
            return false;
        }
        int result = 0;
        for (int index = 0; index < left.length(); index++) {
            result |= left.charAt(index) ^ right.charAt(index);
        }
        return result == 0;
    }

    private static String toHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte current : bytes) {
            builder.append(String.format("%02x", current));
        }
        return builder.toString();
    }
}
