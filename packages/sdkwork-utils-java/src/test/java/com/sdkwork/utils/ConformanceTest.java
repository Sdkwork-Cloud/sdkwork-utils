package com.sdkwork.utils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConformanceTest {
    private static Map<String, Object> fixtures;

    @BeforeAll
    static void loadFixtures() throws IOException {
        try (InputStream stream = ConformanceTest.class.getResourceAsStream("/conformance/fixtures.json")) {
            assertNotNull(stream);
            fixtures = new com.fasterxml.jackson.databind.ObjectMapper()
                    .readValue(new String(stream.readAllBytes(), StandardCharsets.UTF_8), Map.class);
        }
    }

    @Test
    void conformanceFixtures() {
        Map<String, Object> stringCases = (Map<String, Object>) fixtures.get("string");
        List<Map<String, Object>> isBlankCases = (List<Map<String, Object>>) stringCases.get("is_blank");
        for (Map<String, Object> item : isBlankCases) {
            assertEquals(item.get("output"), StringUtils.isBlank((String) item.get("input")));
        }

        Map<String, Object> slug = ((List<Map<String, Object>>) stringCases.get("slugify")).get(0);
        assertEquals(slug.get("output"), StringUtils.slugify((String) slug.get("input")));

        Map<String, Object> mask = ((List<Map<String, Object>>) stringCases.get("mask")).get(0);
        assertEquals(
                mask.get("output"),
                StringUtils.mask(
                        (String) mask.get("input"),
                        ((Number) mask.get("visible_start")).intValue(),
                        ((Number) mask.get("visible_end")).intValue()));

        Map<String, Object> padStart = ((List<Map<String, Object>>) stringCases.get("pad_start")).get(0);
        assertEquals(
                padStart.get("output"),
                StringUtils.padStart(
                        (String) padStart.get("input"),
                        ((Number) padStart.get("target_len")).intValue()));

        Map<String, Object> padEnd = ((List<Map<String, Object>>) stringCases.get("pad_end")).get(0);
        assertEquals(
                padEnd.get("output"),
                StringUtils.padEnd(
                        (String) padEnd.get("input"),
                        ((Number) padEnd.get("target_len")).intValue()));

        Map<String, Object> startsWith = ((List<Map<String, Object>>) stringCases.get("starts_with")).get(0);
        assertEquals(
                startsWith.get("output"),
                StringUtils.startsWith((String) startsWith.get("input"), (String) startsWith.get("prefix")));

        Map<String, Object> endsWith = ((List<Map<String, Object>>) stringCases.get("ends_with")).get(0);
        assertEquals(
                endsWith.get("output"),
                StringUtils.endsWith((String) endsWith.get("input"), (String) endsWith.get("suffix")));

        Map<String, Object> contains = ((List<Map<String, Object>>) stringCases.get("contains")).get(0);
        assertEquals(
                contains.get("output"),
                StringUtils.contains((String) contains.get("input"), (String) contains.get("substring")));

        Map<String, Object> replaceAll = ((List<Map<String, Object>>) stringCases.get("replace_all")).get(0);
        assertEquals(
                replaceAll.get("output"),
                StringUtils.replaceAll(
                        (String) replaceAll.get("input"),
                        (String) replaceAll.get("search"),
                        (String) replaceAll.get("replacement")));

        Map<String, Object> split = ((List<Map<String, Object>>) stringCases.get("split")).get(0);
        assertEquals(
                split.get("output"),
                StringUtils.split(
                        (String) split.get("input"),
                        (String) split.get("delimiter"),
                        (Boolean) split.get("trim_parts")));

        Map<String, Object> join = ((List<Map<String, Object>>) stringCases.get("join")).get(0);
        assertEquals(
                join.get("output"),
                StringUtils.join((List<String>) join.get("parts"), (String) join.get("separator")));

        Map<String, Object> diff = (Map<String, Object>) ((Map<?, ?>) fixtures.get("datetime")).get("diff_millis");
        var earlier = DateTimeUtils.parseDatetime((String) diff.get("earlier"), DateTimeUtils.DEFAULT_PATTERN);
        var later = DateTimeUtils.parseDatetime((String) diff.get("later"), DateTimeUtils.DEFAULT_PATTERN);
        assertEquals(((Number) diff.get("output")).longValue(), DateTimeUtils.diffMillis(earlier, later));

        Map<String, Object> toUnix = ((List<Map<String, Object>>) ((Map<?, ?>) fixtures.get("datetime")).get("to_unix_millis")).get(0);
        var parsedInstant = DateTimeUtils.parseDatetime((String) toUnix.get("input"), DateTimeUtils.DEFAULT_PATTERN);
        assertEquals(((Number) toUnix.get("output")).longValue(), DateTimeUtils.toUnixMillis(parsedInstant));

        Map<String, Object> fromUnix = ((List<Map<String, Object>>) ((Map<?, ?>) fixtures.get("datetime")).get("from_unix_millis")).get(0);
        assertEquals(
                fromUnix.get("output"),
                DateTimeUtils.formatDatetime(
                        DateTimeUtils.fromUnixMillis(((Number) fromUnix.get("input")).longValue()),
                        DateTimeUtils.DEFAULT_PATTERN));

        Map<String, Object> sha = ((List<Map<String, Object>>) ((Map<?, ?>) fixtures.get("crypto")).get("sha256_hash")).get(0);
        assertEquals(sha.get("output"), CryptoUtils.sha256Hash((String) sha.get("input")));

        Map<String, Object> hmac = ((List<Map<String, Object>>) ((Map<?, ?>) fixtures.get("crypto")).get("hmac_sha256")).get(0);
        assertEquals(hmac.get("output"), CryptoUtils.hmacSha256((String) hmac.get("input"), (String) hmac.get("secret")));

        for (Map<String, Object> item : (List<Map<String, Object>>) ((Map<?, ?>) fixtures.get("crypto")).get("secure_compare")) {
            assertEquals(
                    item.get("output"),
                    CryptoUtils.secureCompare((String) item.get("left"), (String) item.get("right")));
        }

        List<Map<String, Object>> numberLocales =
                (List<Map<String, Object>>) ((Map<?, ?>) fixtures.get("i18n")).get("format_number_locale");
        for (Map<String, Object> item : numberLocales) {
            assertEquals(
                    item.get("output"),
                    I18nUtils.formatNumberLocale(
                            ((Number) item.get("value")).doubleValue(),
                            (String) item.get("locale"),
                            ((Number) item.get("decimals")).intValue()));
        }

        Map<String, Object> coalesce = ((List<Map<String, Object>>) ((Map<?, ?>) fixtures.get("optional")).get("coalesce")).get(0);
        assertEquals(coalesce.get("output"), OptionalUtils.coalesce(null, "", "  ", "ok"));

        Map<String, Object> clamp = ((List<Map<String, Object>>) ((Map<?, ?>) fixtures.get("number")).get("clamp")).get(0);
        assertEquals(
                ((Number) clamp.get("output")).doubleValue(),
                NumberUtils.clamp(
                        ((Number) clamp.get("value")).doubleValue(),
                        ((Number) clamp.get("min")).doubleValue(),
                        ((Number) clamp.get("max")).doubleValue()));

        for (Map<String, Object> item : (List<Map<String, Object>>) ((Map<?, ?>) fixtures.get("number")).get("parse_int")) {
            if (item.get("output") == null) {
                assertNull(NumberUtils.parseInt((String) item.get("input")));
            } else {
                assertEquals(((Number) item.get("output")).longValue(), NumberUtils.parseInt((String) item.get("input")));
            }
        }

        Map<String, Object> percent = ((List<Map<String, Object>>) ((Map<?, ?>) fixtures.get("number")).get("percent_format")).get(0);
        assertEquals(
                percent.get("output"),
                NumberUtils.percentFormat(
                        ((Number) percent.get("value")).doubleValue(),
                        ((Number) percent.get("decimals")).intValue()));

        Map<String, Object> email = ((List<Map<String, Object>>) ((Map<?, ?>) fixtures.get("validation")).get("is_email")).get(0);
        assertEquals(email.get("output"), ValidationUtils.isEmail((String) email.get("input")));

        for (Map<String, Object> item : (List<Map<String, Object>>) ((Map<?, ?>) fixtures.get("validation")).get("is_ipv4")) {
            assertEquals(item.get("output"), ValidationUtils.isIpv4((String) item.get("input")));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) ((Map<?, ?>) fixtures.get("validation")).get("is_ipv6")) {
            assertEquals(item.get("output"), ValidationUtils.isIpv6((String) item.get("input")));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) ((Map<?, ?>) fixtures.get("validation")).get("is_phone_e164")) {
            assertEquals(item.get("output"), ValidationUtils.isPhoneE164((String) item.get("input")));
        }

        List<Map<String, Object>> unwrapCases = (List<Map<String, Object>>) ((Map<?, ?>) fixtures.get("result")).get("unwrap_or");
        for (Map<String, Object> item : unwrapCases) {
            ResultUtils.ResultValue<Integer> result = "ok".equals(item.get("kind"))
                    ? ResultUtils.ResultValue.ok(((Number) item.get("value")).intValue())
                    : ResultUtils.ResultValue.err((String) item.get("message"));
            assertEquals(
                    ((Number) item.get("output")).intValue(),
                    ResultUtils.unwrapOr(result, ((Number) item.get("default")).intValue()));
        }

        Map<String, Object> merge = (Map<String, Object>) ((Map<?, ?>) fixtures.get("object")).get("deep_merge");
        assertEquals(merge.get("output"), ObjectUtils.deepMerge(merge.get("base"), merge.get("overlay")));

        Map<String, Object> shallow = (Map<String, Object>) ((Map<?, ?>) fixtures.get("object")).get("shallow_merge");
        assertEquals(shallow.get("output"), ObjectUtils.shallowMerge(shallow.get("base"), shallow.get("overlay")));

        Map<String, Object> urlEncode = ((List<Map<String, Object>>) ((Map<?, ?>) fixtures.get("encoding")).get("url_encode")).get(0);
        assertEquals(urlEncode.get("output"), EncodingUtils.urlEncode((String) urlEncode.get("input")));

        Map<String, Object> urlDecode = ((List<Map<String, Object>>) ((Map<?, ?>) fixtures.get("encoding")).get("url_decode")).get(0);
        assertEquals(urlDecode.get("output"), EncodingUtils.urlDecode((String) urlDecode.get("input")));

        for (Map<String, Object> item : (List<Map<String, Object>>) ((Map<?, ?>) fixtures.get("encoding")).get("base64url_encode")) {
            assertEquals(item.get("output"), EncodingUtils.base64urlEncode(((String) item.get("input")).getBytes(StandardCharsets.UTF_8)));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) ((Map<?, ?>) fixtures.get("encoding")).get("base64url_decode")) {
            byte[] decoded = EncodingUtils.base64urlDecode((String) item.get("input"));
            assertEquals(item.get("output"), new String(decoded, StandardCharsets.UTF_8));
        }

        for (Map<String, Object> item : (List<Map<String, Object>>) ((Map<?, ?>) fixtures.get("collection")).get("first")) {
            List<Integer> input = (List<Integer>) item.get("input");
            if (item.get("output") == null) {
                assertNull(CollectionUtils.first(input));
            } else {
                assertEquals(item.get("output"), CollectionUtils.first(input));
            }
        }

        for (Map<String, Object> item : (List<Map<String, Object>>) ((Map<?, ?>) fixtures.get("collection")).get("last")) {
            List<Integer> input = (List<Integer>) item.get("input");
            if (item.get("output") == null) {
                assertNull(CollectionUtils.last(input));
            } else {
                assertEquals(item.get("output"), CollectionUtils.last(input));
            }
        }

        Map<String, Object> hasPathSource = new LinkedHashMap<>();
        Map<String, Object> user = new LinkedHashMap<>();
        user.put("name", "Ada");
        hasPathSource.put("user", user);
        for (Map<String, Object> item : (List<Map<String, Object>>) ((Map<?, ?>) fixtures.get("object")).get("has_path")) {
            assertEquals(item.get("exists"), ObjectUtils.hasPath(hasPathSource, (String) item.get("path")));
        }

        for (Map<String, Object> item : (List<Map<String, Object>>) ((Map<?, ?>) fixtures.get("boolean")).get("parse_bool")) {
            if (item.get("output") == null) {
                assertNull(BooleanUtils.parseBool((String) item.get("input")));
            } else {
                assertEquals(item.get("output"), BooleanUtils.parseBool((String) item.get("input")));
            }
        }

        for (Map<String, Object> item : (List<Map<String, Object>>) ((Map<?, ?>) fixtures.get("boolean")).get("is_truthy")) {
            assertEquals(item.get("output"), BooleanUtils.isTruthy((String) item.get("input")));
        }

        for (Map<String, Object> item : (List<Map<String, Object>>) stringCases.get("repeat")) {
            assertEquals(item.get("output"), StringUtils.repeat((String) item.get("input"), ((Number) item.get("count")).intValue()));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) stringCases.get("template")) {
            @SuppressWarnings("unchecked")
            Map<String, String> values = (Map<String, String>) item.get("values");
            assertEquals(item.get("output"), StringUtils.template((String) item.get("template"), values));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) stringCases.get("trim")) {
            assertEquals(item.get("output"), StringUtils.trim((String) item.get("input")));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) stringCases.get("truncate")) {
            assertEquals(
                    item.get("output"),
                    StringUtils.truncate(
                            (String) item.get("input"),
                            ((Number) item.get("max_len")).intValue(),
                            (String) item.get("suffix")));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) stringCases.get("capitalize")) {
            assertEquals(item.get("output"), StringUtils.capitalize((String) item.get("input")));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) stringCases.get("camel_case")) {
            assertEquals(item.get("output"), StringUtils.camelCase((String) item.get("input")));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) stringCases.get("snake_case")) {
            assertEquals(item.get("output"), StringUtils.snakeCase((String) item.get("input")));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) stringCases.get("kebab_case")) {
            assertEquals(item.get("output"), StringUtils.kebabCase((String) item.get("input")));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) stringCases.get("normalize_whitespace")) {
            assertEquals(item.get("output"), StringUtils.normalizeWhitespace((String) item.get("input")));
        }

        Map<String, Object> datetimeCases = (Map<String, Object>) fixtures.get("datetime");
        for (Map<String, Object> item : (List<Map<String, Object>>) datetimeCases.get("now")) {
            assertEquals(item.get("valid"), DateTimeUtils.now() != null);
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) datetimeCases.get("format")) {
            var parsed = DateTimeUtils.parseDatetime((String) item.get("input"), DateTimeUtils.DEFAULT_PATTERN);
            assertEquals(item.get("output"), DateTimeUtils.formatDatetime(parsed, DateTimeUtils.DEFAULT_PATTERN));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) datetimeCases.get("is_same_instant")) {
            var left = DateTimeUtils.parseDatetime((String) item.get("left"), DateTimeUtils.DEFAULT_PATTERN);
            var right = DateTimeUtils.parseDatetime((String) item.get("right"), DateTimeUtils.DEFAULT_PATTERN);
            assertEquals(item.get("output"), DateTimeUtils.isSameInstant(left, right));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) datetimeCases.get("parse")) {
            assertEquals(item.get("valid"), DateTimeUtils.parseDatetime((String) item.get("input"), DateTimeUtils.DEFAULT_PATTERN) != null);
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) datetimeCases.get("add_days")) {
            var parsed = DateTimeUtils.parseDatetime((String) item.get("input"), DateTimeUtils.DEFAULT_PATTERN);
            var result = DateTimeUtils.formatDatetime(
                    DateTimeUtils.addDays(parsed, ((Number) item.get("days")).longValue()),
                    DateTimeUtils.DEFAULT_PATTERN);
            assertEquals(item.get("output"), result);
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) datetimeCases.get("add_hours")) {
            var parsed = DateTimeUtils.parseDatetime((String) item.get("input"), DateTimeUtils.DEFAULT_PATTERN);
            var result = DateTimeUtils.formatDatetime(
                    DateTimeUtils.addHours(parsed, ((Number) item.get("hours")).longValue()),
                    DateTimeUtils.DEFAULT_PATTERN);
            assertEquals(item.get("output"), result);
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) datetimeCases.get("add_minutes")) {
            var parsed = DateTimeUtils.parseDatetime((String) item.get("input"), DateTimeUtils.DEFAULT_PATTERN);
            var result = DateTimeUtils.formatDatetime(
                    DateTimeUtils.addMinutes(parsed, ((Number) item.get("minutes")).longValue()),
                    DateTimeUtils.DEFAULT_PATTERN);
            assertEquals(item.get("output"), result);
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) datetimeCases.get("is_before")) {
            var left = DateTimeUtils.parseDatetime((String) item.get("left"), DateTimeUtils.DEFAULT_PATTERN);
            var right = DateTimeUtils.parseDatetime((String) item.get("right"), DateTimeUtils.DEFAULT_PATTERN);
            assertEquals(item.get("output"), DateTimeUtils.isBefore(left, right));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) datetimeCases.get("is_after")) {
            var left = DateTimeUtils.parseDatetime((String) item.get("left"), DateTimeUtils.DEFAULT_PATTERN);
            var right = DateTimeUtils.parseDatetime((String) item.get("right"), DateTimeUtils.DEFAULT_PATTERN);
            assertEquals(item.get("output"), DateTimeUtils.isAfter(left, right));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) datetimeCases.get("start_of_day_utc")) {
            var parsed = DateTimeUtils.parseDatetime((String) item.get("input"), DateTimeUtils.DEFAULT_PATTERN);
            assertEquals(
                    item.get("output"),
                    DateTimeUtils.formatDatetime(DateTimeUtils.startOfDayUtc(parsed), DateTimeUtils.DEFAULT_PATTERN));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) datetimeCases.get("end_of_day_utc")) {
            var parsed = DateTimeUtils.parseDatetime((String) item.get("input"), DateTimeUtils.DEFAULT_PATTERN);
            assertEquals(
                    item.get("output"),
                    DateTimeUtils.formatDatetime(DateTimeUtils.endOfDayUtc(parsed), DateTimeUtils.DEFAULT_PATTERN));
        }

        Map<String, Object> numberCases = (Map<String, Object>) fixtures.get("number");
        for (Map<String, Object> item : (List<Map<String, Object>>) numberCases.get("round")) {
            assertEquals(((Number) item.get("output")).doubleValue(), NumberUtils.round(((Number) item.get("value")).doubleValue(), ((Number) item.get("decimals")).intValue()), 0.001);
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) numberCases.get("in_range")) {
            assertEquals(item.get("output"), NumberUtils.inRange(((Number) item.get("value")).doubleValue(), ((Number) item.get("min")).doubleValue(), ((Number) item.get("max")).doubleValue()));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) numberCases.get("format_number")) {
            assertEquals(item.get("output"), NumberUtils.formatNumber(((Number) item.get("value")).doubleValue(), ((Number) item.get("decimals")).intValue()));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) numberCases.get("parse_number")) {
            assertEquals(item.get("output"), NumberUtils.parseNumber((String) item.get("input")));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) numberCases.get("is_integer")) {
            assertEquals(item.get("output"), NumberUtils.isInteger(((Number) item.get("value")).doubleValue()));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) numberCases.get("abs")) {
            assertEquals(((Number) item.get("output")).doubleValue(), NumberUtils.abs(((Number) item.get("input")).doubleValue()), 0.001);
        }

        Map<String, Object> collectionCases = (Map<String, Object>) fixtures.get("collection");
        for (Map<String, Object> item : (List<Map<String, Object>>) collectionCases.get("unique")) {
            assertEquals(item.get("output"), CollectionUtils.unique((List<Integer>) item.get("input")));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) collectionCases.get("chunk")) {
            assertEquals(item.get("output"), CollectionUtils.chunk((List<Integer>) item.get("input"), ((Number) item.get("size")).intValue()));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) collectionCases.get("flatten")) {
            assertEquals(item.get("output"), CollectionUtils.flatten((List<List<Integer>>) item.get("input")));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) collectionCases.get("compact")) {
            assertEquals(item.get("output"), CollectionUtils.compact((List<Object>) item.get("input")));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) collectionCases.get("group_by")) {
            List<Map<String, Object>> input = (List<Map<String, Object>>) item.get("input");
            assertEquals(item.get("output"), CollectionUtils.groupBy(input, entry -> (String) entry.get("type")));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) collectionCases.get("sort_by")) {
            List<Map<String, Object>> input = (List<Map<String, Object>>) item.get("input");
            assertEquals(item.get("output"), CollectionUtils.sortBy(input, entry -> (String) entry.get("k")));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) collectionCases.get("key_by")) {
            List<Map<String, Object>> input = (List<Map<String, Object>>) item.get("input");
            assertEquals(item.get("output"), CollectionUtils.keyBy(input, entry -> (String) entry.get("id")));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) collectionCases.get("filter")) {
            List<Integer> input = (List<Integer>) item.get("input");
            int threshold = ((Number) item.get("threshold")).intValue();
            assertEquals(item.get("output"), CollectionUtils.filter(input, value -> value > threshold));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) collectionCases.get("find")) {
            List<Integer> input = (List<Integer>) item.get("input");
            int threshold = ((Number) item.get("threshold")).intValue();
            assertEquals(item.get("output"), CollectionUtils.find(input, value -> value > threshold));
        }

        Map<String, Object> validationCases = (Map<String, Object>) fixtures.get("validation");
        for (Map<String, Object> item : (List<Map<String, Object>>) validationCases.get("is_uuid")) {
            assertEquals(item.get("output"), ValidationUtils.isUuid((String) item.get("input")));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) validationCases.get("is_url")) {
            assertEquals(item.get("output"), ValidationUtils.isUrl((String) item.get("input")));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) validationCases.get("is_numeric")) {
            assertEquals(item.get("output"), ValidationUtils.isNumeric((String) item.get("input")));
        }

        Map<String, Object> idCases = (Map<String, Object>) fixtures.get("id");
        for (Map<String, Object> item : (List<Map<String, Object>>) idCases.get("uuid")) {
            assertTrue(IdUtils.uuid().matches("^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$"));
            assertEquals("uuid-v4", item.get("pattern"));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) idCases.get("random_string")) {
            String value = IdUtils.randomString(((Number) item.get("length")).intValue());
            assertEquals(((Number) item.get("length")).intValue(), value.length());
            assertTrue(value.matches("^[A-Za-z0-9]+$"));
        }

        Map<String, Object> encodingCases = (Map<String, Object>) fixtures.get("encoding");
        for (Map<String, Object> item : (List<Map<String, Object>>) encodingCases.get("base64_encode")) {
            assertEquals(item.get("output"), EncodingUtils.base64Encode(((String) item.get("input")).getBytes(StandardCharsets.UTF_8)));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) encodingCases.get("base64_decode")) {
            byte[] decoded = EncodingUtils.base64Decode((String) item.get("input"));
            assertEquals(item.get("output"), new String(decoded, StandardCharsets.UTF_8));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) encodingCases.get("hex_encode")) {
            assertEquals(item.get("output"), EncodingUtils.hexEncode(((String) item.get("input")).getBytes(StandardCharsets.UTF_8)));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) encodingCases.get("hex_decode")) {
            byte[] decoded = EncodingUtils.hexDecode((String) item.get("input"));
            assertEquals(item.get("output"), new String(decoded, StandardCharsets.UTF_8));
        }

        Map<String, Object> pathCases = (Map<String, Object>) fixtures.get("path");
        for (Map<String, Object> item : (List<Map<String, Object>>) pathCases.get("join_path")) {
            List<String> segments = (List<String>) item.get("segments");
            assertEquals(item.get("output"), PathUtils.joinPath(segments.toArray(new String[0])));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) pathCases.get("normalize_path")) {
            assertEquals(item.get("output"), PathUtils.normalizePath((String) item.get("input")));
        }

        Map<String, Object> objectCases = (Map<String, Object>) fixtures.get("object");
        for (Map<String, Object> item : (List<Map<String, Object>>) objectCases.get("keys")) {
            assertEquals(item.get("output"), ObjectUtils.keys((Map<String, Object>) item.get("input")));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) objectCases.get("values")) {
            assertEquals(item.get("output"), ObjectUtils.values((Map<String, Object>) item.get("input")));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) objectCases.get("pick")) {
            assertEquals(item.get("output"), ObjectUtils.pick((Map<String, Object>) item.get("source"), (List<String>) item.get("keys")));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) objectCases.get("omit")) {
            assertEquals(item.get("output"), ObjectUtils.omit((Map<String, Object>) item.get("source"), (List<String>) item.get("keys")));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) objectCases.get("get_path")) {
            assertEquals(item.get("output"), ObjectUtils.getPath(item.get("source"), (String) item.get("path")));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) objectCases.get("set_path")) {
            Map<String, Object> target = ObjectUtils.setPath(new LinkedHashMap<>(), (String) item.get("path"), item.get("value"));
            assertEquals(item.get("read_back"), ObjectUtils.getPath(target, (String) item.get("path")));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) objectCases.get("compact")) {
            assertEquals(item.get("output"), ObjectUtils.compact((Map<String, Object>) item.get("input")));
        }

        Map<String, Object> optionalCases = (Map<String, Object>) fixtures.get("optional");
        for (Map<String, Object> item : (List<Map<String, Object>>) optionalCases.get("default_if_blank")) {
            assertEquals(item.get("output"), OptionalUtils.defaultIfBlank((String) item.get("input"), (String) item.get("default")));
        }

        Map<String, Object> resultCases = (Map<String, Object>) fixtures.get("result");
        for (Map<String, Object> item : (List<Map<String, Object>>) resultCases.get("is_ok")) {
            ResultUtils.ResultValue<Integer> result = "ok".equals(item.get("kind"))
                    ? ResultUtils.ResultValue.ok(((Number) item.get("value")).intValue())
                    : ResultUtils.ResultValue.err((String) item.get("message"));
            assertEquals(item.get("output"), ResultUtils.isOk(result));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) resultCases.get("is_err")) {
            ResultUtils.ResultValue<Integer> result = "ok".equals(item.get("kind"))
                    ? ResultUtils.ResultValue.ok(((Number) item.get("value")).intValue())
                    : ResultUtils.ResultValue.err((String) item.get("message"));
            assertEquals(item.get("output"), ResultUtils.isErr(result));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) resultCases.get("map")) {
            ResultUtils.ResultValue<Integer> mapped = ResultUtils.map(
                    ResultUtils.ResultValue.ok(((Number) item.get("value")).intValue()),
                    value -> value * 2);
            assertEquals(((Number) item.get("output")).intValue(), mapped.value());
        }

        Map<String, Object> compareCases = (Map<String, Object>) fixtures.get("compare");
        for (Map<String, Object> item : (List<Map<String, Object>>) compareCases.get("deep_equal")) {
            assertEquals(item.get("output"), CompareUtils.deepEqual(item.get("left"), item.get("right")));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) compareCases.get("deep_clone")) {
            Map<String, Object> cloned = (Map<String, Object>) CompareUtils.deepClone(item.get("input"));
            assertEquals(item.get("output"), cloned);
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) ((Map<?, ?>) fixtures.get("i18n")).get("parse_number_locale")) {
            assertEquals(item.get("output"), I18nUtils.parseNumberLocale((String) item.get("input"), (String) item.get("locale")));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) ((Map<?, ?>) fixtures.get("i18n")).get("format_datetime_locale")) {
            var parsed = DateTimeUtils.parseDatetime((String) item.get("input"), DateTimeUtils.DEFAULT_PATTERN);
            String formatted = I18nUtils.formatDatetimeLocale(parsed, (String) item.get("locale"));
            assertTrue(formatted.contains((String) item.get("contains")));
        }

        Map<String, Object> currencyCases = (Map<String, Object>) fixtures.get("currency");
        for (Map<String, Object> item : (List<Map<String, Object>>) currencyCases.get("is_currency_code")) {
            assertEquals(item.get("output"), CurrencyUtils.isCurrencyCode((String) item.get("input")));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) currencyCases.get("minor_unit_exponent")) {
            assertEquals(item.get("output"), CurrencyUtils.minorUnitExponent((String) item.get("code")));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) currencyCases.get("to_minor_units")) {
            assertEquals(((Number) item.get("output")).longValue(), CurrencyUtils.toMinorUnits(((Number) item.get("amount")).doubleValue(), (String) item.get("code")).longValue());
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) currencyCases.get("from_minor_units")) {
            assertEquals(item.get("output"), CurrencyUtils.fromMinorUnits(((Number) item.get("minor")).longValue(), (String) item.get("code")));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) currencyCases.get("format_currency")) {
            assertEquals(item.get("output"), CurrencyUtils.formatCurrency(((Number) item.get("amount")).doubleValue(), (String) item.get("code"), (String) item.get("locale")));
        }

        Map<String, Object> bloomCases = (Map<String, Object>) fixtures.get("bloom");
        for (Map<String, Object> item : (List<Map<String, Object>>) bloomCases.get("create")) {
            BloomUtils.BloomFilter filter = BloomUtils.create(((Number) item.get("expected_items")).intValue(), ((Number) item.get("false_positive_rate")).doubleValue());
            assertEquals(item.get("bit_count"), filter.bitCount());
            assertEquals(item.get("hash_count"), filter.hashCount());
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) bloomCases.get("estimate_bit_count")) {
            assertEquals(item.get("output"), BloomUtils.estimateBitCount(((Number) item.get("expected_items")).intValue(), ((Number) item.get("false_positive_rate")).doubleValue()));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) bloomCases.get("estimate_hash_count")) {
            assertEquals(item.get("output"), BloomUtils.estimateHashCount(((Number) item.get("expected_items")).intValue(), ((Number) item.get("bit_count")).intValue()));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) bloomCases.get("might_contain")) {
            BloomUtils.BloomFilter filter = BloomUtils.create(128, 0.01);
            for (Object value : (List<?>) item.get("added")) {
                BloomUtils.add(filter, (String) value);
            }
            assertEquals(item.get("present_output"), BloomUtils.mightContain(filter, (String) item.get("present")));
            assertEquals(item.get("absent_output"), BloomUtils.mightContain(filter, (String) item.get("absent")));
        }

        Map<String, Object> bytesCases = (Map<String, Object>) fixtures.get("bytes");
        for (Map<String, Object> item : (List<Map<String, Object>>) bytesCases.get("format_bytes")) {
            assertEquals(item.get("output"), BytesUtils.formatBytes(((Number) item.get("value")).longValue(), ((Number) item.get("decimals")).intValue()));
        }
    }
}
