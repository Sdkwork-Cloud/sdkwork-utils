package com.sdkwork.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UtilsTest {
    @Test
    void stringHelpers() {
        assertTrue(StringUtils.isBlank("  "));
        assertEquals("helloWorld", StringUtils.camelCase("hello_world"));
        assertEquals("hello-sdk-work", StringUtils.slugify("Hello, SDKWork!"));
        assertEquals("ab...", StringUtils.truncate("abcdef", 5, "..."));
    }

    @Test
    void datetimeHelpers() {
        var first = DateTimeUtils.parseDatetime("2024-01-01T00:00:00Z", DateTimeUtils.DEFAULT_PATTERN);
        var second = DateTimeUtils.addHours(first, 2);
        assertEquals(7_200_000L, DateTimeUtils.diffMillis(first, second));
    }

    @Test
    void encodingHelpers() {
        byte[] data = "hello".getBytes();
        assertEquals("aGVsbG8=", EncodingUtils.base64Encode(data));
        assertEquals("68656c6c6f", EncodingUtils.hexEncode(data));
    }

    @Test
    void objectAndCryptoHelpers() {
        var merged = ObjectUtils.deepMerge(
                java.util.Map.of("a", 1, "nested", java.util.Map.of("x", 1)),
                java.util.Map.of("b", 2, "nested", java.util.Map.of("y", 2)));
        assertEquals(
                java.util.Map.of("a", 1, "b", 2, "nested", java.util.Map.of("x", 1, "y", 2)),
                merged);
        var updated = ObjectUtils.setPath(new java.util.LinkedHashMap<>(), "user.city", "Paris");
        assertEquals("Paris", ObjectUtils.getPath(updated, "user.city"));
        assertEquals(
                "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824",
                CryptoUtils.sha256Hash("hello"));
        assertEquals(
                "b82fcb791acec57859b989b430a826488ce2e479fdf92326bd0a2e8375a42ba4",
                CryptoUtils.hmacSha256("payload", "secret"));
    }

    @Test
    void optionalResultAndI18nHelpers() {
        assertEquals("ok", OptionalUtils.coalesce(null, "", "  ", "ok"));
        assertEquals("fallback", OptionalUtils.defaultIfBlank("  ", "fallback"));
        assertEquals(42, ResultUtils.unwrapOr(ResultUtils.ResultValue.ok(42), 0));
        assertEquals(0, ResultUtils.unwrapOr(ResultUtils.ResultValue.err("fail"), 0));
        assertEquals("1,234.50", I18nUtils.formatNumberLocale(1234.5, "en-US", 2));
        assertEquals("1.234,50", I18nUtils.formatNumberLocale(1234.5, "de-DE", 2));
        var formatted = I18nUtils.formatDatetimeLocale(
                DateTimeUtils.parseDatetime("2024-06-15T14:30:00.000Z", DateTimeUtils.DEFAULT_PATTERN),
                "en-US");
        assertTrue(formatted.contains("2024"));
    }
}
