package com.sdkwork.utils

import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class ConformanceTest {
    private val fixtures: JSONObject by lazy {
        val path = Path.of("..", "..", "specs", "conformance", "fixtures.json").normalize()
        JSONObject(Files.readString(path))
    }

    @Test
    fun conformanceFixtures() {
        val stringCases = fixtures.getJSONObject("string")
        for (item in stringCases.getJSONArray("is_blank")) {
            val entry = item as JSONObject
            val input = if (entry.isNull("input")) null else entry.getString("input")
            assertEquals(entry.getBoolean("output"), StringUtils.isBlank(input))
        }

        val slug = stringCases.getJSONArray("slugify").getJSONObject(0)
        assertEquals(slug.getString("output"), StringUtils.slugify(slug.getString("input")))

        val mask = stringCases.getJSONArray("mask").getJSONObject(0)
        assertEquals(
            mask.getString("output"),
            StringUtils.mask(mask.getString("input"), mask.getInt("visible_start"), mask.getInt("visible_end"))
        )

        val padStart = stringCases.getJSONArray("pad_start").getJSONObject(0)
        assertEquals(
            padStart.getString("output"),
            StringUtils.padStart(padStart.getString("input"), padStart.getInt("target_len"))
        )

        val padEnd = stringCases.getJSONArray("pad_end").getJSONObject(0)
        assertEquals(
            padEnd.getString("output"),
            StringUtils.padEnd(padEnd.getString("input"), padEnd.getInt("target_len"))
        )

        val startsWith = stringCases.getJSONArray("starts_with").getJSONObject(0)
        assertEquals(
            startsWith.getBoolean("output"),
            StringUtils.startsWith(startsWith.getString("input"), startsWith.getString("prefix"))
        )

        val endsWith = stringCases.getJSONArray("ends_with").getJSONObject(0)
        assertEquals(
            endsWith.getBoolean("output"),
            StringUtils.endsWith(endsWith.getString("input"), endsWith.getString("suffix"))
        )

        val contains = stringCases.getJSONArray("contains").getJSONObject(0)
        assertEquals(
            contains.getBoolean("output"),
            StringUtils.contains(contains.getString("input"), contains.getString("substring"))
        )

        val replaceAll = stringCases.getJSONArray("replace_all").getJSONObject(0)
        assertEquals(
            replaceAll.getString("output"),
            StringUtils.replaceAll(
                replaceAll.getString("input"),
                replaceAll.getString("search"),
                replaceAll.getString("replacement")
            )
        )

        val split = stringCases.getJSONArray("split").getJSONObject(0)
        assertEquals(
            jsonArrayToStringList(split.getJSONArray("output")),
            StringUtils.split(split.getString("input"), split.getString("delimiter"), split.getBoolean("trim_parts"))
        )

        val join = stringCases.getJSONArray("join").getJSONObject(0)
        assertEquals(
            join.getString("output"),
            StringUtils.join(jsonArrayToStringList(join.getJSONArray("parts")), join.getString("separator"))
        )

        val diff = fixtures.getJSONObject("datetime").getJSONObject("diff_millis")
        val earlier = DateTimeUtils.parseDatetime(diff.getString("earlier"), DateTimeUtils.DEFAULT_PATTERN)
        val later = DateTimeUtils.parseDatetime(diff.getString("later"), DateTimeUtils.DEFAULT_PATTERN)
        assertNotNull(earlier)
        assertNotNull(later)
        assertEquals(diff.getLong("output"), DateTimeUtils.diffMillis(earlier!!, later!!))

        val toUnix = fixtures.getJSONObject("datetime").getJSONArray("to_unix_millis").getJSONObject(0)
        val parsedInstant = DateTimeUtils.parseDatetime(toUnix.getString("input"), DateTimeUtils.DEFAULT_PATTERN)
        assertNotNull(parsedInstant)
        assertEquals(toUnix.getLong("output"), DateTimeUtils.toUnixMillis(parsedInstant!!))

        val fromUnix = fixtures.getJSONObject("datetime").getJSONArray("from_unix_millis").getJSONObject(0)
        assertEquals(
            fromUnix.getString("output"),
            DateTimeUtils.formatDatetime(DateTimeUtils.fromUnixMillis(fromUnix.getLong("input")), DateTimeUtils.DEFAULT_PATTERN)
        )

        val sha = fixtures.getJSONObject("crypto").getJSONArray("sha256_hash").getJSONObject(0)
        assertEquals(sha.getString("output"), CryptoUtils.sha256Hash(sha.getString("input")))

        val hmac = fixtures.getJSONObject("crypto").getJSONArray("hmac_sha256").getJSONObject(0)
        assertEquals(
            hmac.getString("output"),
            CryptoUtils.hmacSha256(hmac.getString("input"), hmac.getString("secret"))
        )

        val secureCompareCases = fixtures.getJSONObject("crypto").getJSONArray("secure_compare")
        for (index in 0 until secureCompareCases.length()) {
            val item = secureCompareCases.getJSONObject(index)
            assertEquals(
                item.getBoolean("output"),
                CryptoUtils.secureCompare(item.getString("left"), item.getString("right"))
            )
        }

        val numberLocales = fixtures.getJSONObject("i18n").getJSONArray("format_number_locale")
        for (index in 0 until numberLocales.length()) {
            val item = numberLocales.getJSONObject(index)
            assertEquals(
                item.getString("output"),
                I18nUtils.formatNumberLocale(
                    item.getDouble("value"),
                    item.getString("locale"),
                    item.getInt("decimals")
                )
            )
        }

        val coalesce = fixtures.getJSONObject("optional").getJSONArray("coalesce").getJSONObject(0)
        assertEquals(coalesce.getString("output"), OptionalUtils.coalesce(null, "", "  ", "ok"))

        val clamp = fixtures.getJSONObject("number").getJSONArray("clamp").getJSONObject(0)
        assertEquals(
            clamp.getDouble("output"),
            NumberUtils.clamp(clamp.getDouble("value"), clamp.getDouble("min"), clamp.getDouble("max")),
            0.001
        )

        val parseIntCases = fixtures.getJSONObject("number").getJSONArray("parse_int")
        for (index in 0 until parseIntCases.length()) {
            val item = parseIntCases.getJSONObject(index)
            if (item.isNull("output")) {
                assertNull(NumberUtils.parseInt(item.getString("input")))
            } else {
                assertEquals(item.getLong("output"), NumberUtils.parseInt(item.getString("input")))
            }
        }

        val percent = fixtures.getJSONObject("number").getJSONArray("percent_format").getJSONObject(0)
        assertEquals(
            percent.getString("output"),
            NumberUtils.percentFormat(percent.getDouble("value"), percent.getInt("decimals"))
        )

        val email = fixtures.getJSONObject("validation").getJSONArray("is_email").getJSONObject(0)
        assertEquals(email.getBoolean("output"), ValidationUtils.isEmail(email.getString("input")))

        val ipv4Cases = fixtures.getJSONObject("validation").getJSONArray("is_ipv4")
        for (index in 0 until ipv4Cases.length()) {
            val item = ipv4Cases.getJSONObject(index)
            assertEquals(item.getBoolean("output"), ValidationUtils.isIpv4(item.getString("input")))
        }
        val ipv6Cases = fixtures.getJSONObject("validation").getJSONArray("is_ipv6")
        for (index in 0 until ipv6Cases.length()) {
            val item = ipv6Cases.getJSONObject(index)
            assertEquals(item.getBoolean("output"), ValidationUtils.isIpv6(item.getString("input")))
        }
        val e164Cases = fixtures.getJSONObject("validation").getJSONArray("is_phone_e164")
        for (index in 0 until e164Cases.length()) {
            val item = e164Cases.getJSONObject(index)
            assertEquals(item.getBoolean("output"), ValidationUtils.isPhoneE164(item.getString("input")))
        }

        val unwrapCases = fixtures.getJSONObject("result").getJSONArray("unwrap_or")
        for (index in 0 until unwrapCases.length()) {
            val item = unwrapCases.getJSONObject(index)
            val result = if (item.getString("kind") == "ok") {
                ResultValue.ok(item.getInt("value"))
            } else {
                ResultValue.err(item.getString("message"))
            }
            assertEquals(item.getInt("output"), ResultUtils.unwrapOr(result, item.getInt("default")))
        }

        val merge = fixtures.getJSONObject("object").getJSONObject("deep_merge")
        val merged = ObjectUtils.deepMerge(
            jsonObjectToMap(merge.getJSONObject("base")),
            jsonObjectToMap(merge.getJSONObject("overlay"))
        ) as Map<*, *>
        val nested = merged["nested"] as Map<*, *>
        assertEquals(1, nested["x"])
        assertEquals(2, nested["y"])

        val shallow = fixtures.getJSONObject("object").getJSONObject("shallow_merge")
        val shallowResult = ObjectUtils.shallowMerge(
            jsonObjectToMap(shallow.getJSONObject("base")),
            jsonObjectToMap(shallow.getJSONObject("overlay"))
        ) as Map<*, *>
        val shallowNested = shallowResult["nested"] as Map<*, *>
        assertEquals(2, shallowNested["y"])

        val urlEncode = fixtures.getJSONObject("encoding").getJSONArray("url_encode").getJSONObject(0)
        assertEquals(urlEncode.getString("output"), EncodingUtils.urlEncode(urlEncode.getString("input")))

        val urlDecode = fixtures.getJSONObject("encoding").getJSONArray("url_decode").getJSONObject(0)
        assertEquals(urlDecode.getString("output"), EncodingUtils.urlDecode(urlDecode.getString("input")))

        val base64UrlEncodeCases = fixtures.getJSONObject("encoding").getJSONArray("base64url_encode")
        for (index in 0 until base64UrlEncodeCases.length()) {
            val item = base64UrlEncodeCases.getJSONObject(index)
            assertEquals(item.getString("output"), EncodingUtils.base64urlEncode(item.getString("input").toByteArray()))
        }
        val base64UrlDecodeCases = fixtures.getJSONObject("encoding").getJSONArray("base64url_decode")
        for (index in 0 until base64UrlDecodeCases.length()) {
            val item = base64UrlDecodeCases.getJSONObject(index)
            val decoded = EncodingUtils.base64urlDecode(item.getString("input"))
            assertEquals(item.getString("output"), String(decoded!!))
        }

        val firstCases = fixtures.getJSONObject("collection").getJSONArray("first")
        for (index in 0 until firstCases.length()) {
            val item = firstCases.getJSONObject(index)
            val input = jsonArrayToIntList(item.getJSONArray("input"))
            if (item.isNull("output")) {
                assertNull(CollectionUtils.first(input))
            } else {
                assertEquals(item.getInt("output"), CollectionUtils.first(input))
            }
        }

        val lastCases = fixtures.getJSONObject("collection").getJSONArray("last")
        for (index in 0 until lastCases.length()) {
            val item = lastCases.getJSONObject(index)
            val input = jsonArrayToIntList(item.getJSONArray("input"))
            if (item.isNull("output")) {
                assertNull(CollectionUtils.last(input))
            } else {
                assertEquals(item.getInt("output"), CollectionUtils.last(input))
            }
        }

        val hasPathSource = linkedMapOf<String, Any?>("user" to linkedMapOf("name" to "Ada"))
        val hasPathCases = fixtures.getJSONObject("object").getJSONArray("has_path")
        for (index in 0 until hasPathCases.length()) {
            val item = hasPathCases.getJSONObject(index)
            assertEquals(item.getBoolean("exists"), ObjectUtils.hasPath(hasPathSource, item.getString("path")))
        }

        val parseBoolCases = fixtures.getJSONObject("boolean").getJSONArray("parse_bool")
        for (index in 0 until parseBoolCases.length()) {
            val item = parseBoolCases.getJSONObject(index)
            if (item.isNull("output")) {
                assertNull(BooleanUtils.parseBool(item.getString("input")))
            } else {
                assertEquals(item.getBoolean("output"), BooleanUtils.parseBool(item.getString("input")))
            }
        }

        val isTruthyCases = fixtures.getJSONObject("boolean").getJSONArray("is_truthy")
        for (index in 0 until isTruthyCases.length()) {
            val item = isTruthyCases.getJSONObject(index)
            assertEquals(item.getBoolean("output"), BooleanUtils.isTruthy(item.getString("input")))
        }

        val pathCase = fixtures.getJSONObject("object").getJSONObject("set_get_path")
        val updated = ObjectUtils.setPath(linkedMapOf(), pathCase.getString("path"), pathCase.getString("value"))
        assertEquals(pathCase.getString("output"), ObjectUtils.getPath(updated, pathCase.getString("path")))

        val formatted = I18nUtils.formatDatetimeLocale(
            DateTimeUtils.parseDatetime("2024-06-15T14:30:00.000Z", DateTimeUtils.DEFAULT_PATTERN)!!,
            "en-US"
        )
        assertTrue(formatted.contains("2024"))

        for (index in 0 until stringCases.getJSONArray("repeat").length()) {
            val item = stringCases.getJSONArray("repeat").getJSONObject(index)
            assertEquals(item.getString("output"), StringUtils.repeat(item.getString("input"), item.getInt("count")))
        }

        val datetimeCases = fixtures.getJSONObject("datetime")
        for (index in 0 until datetimeCases.getJSONArray("now").length()) {
            assertNotNull(DateTimeUtils.now())
        }
        for (index in 0 until datetimeCases.getJSONArray("is_same_instant").length()) {
            val item = datetimeCases.getJSONArray("is_same_instant").getJSONObject(index)
            val left = DateTimeUtils.parseDatetime(item.getString("left"), DateTimeUtils.DEFAULT_PATTERN)!!
            val right = DateTimeUtils.parseDatetime(item.getString("right"), DateTimeUtils.DEFAULT_PATTERN)!!
            assertEquals(item.getBoolean("output"), DateTimeUtils.isSameInstant(left, right))
        }

        val numberCases = fixtures.getJSONObject("number")
        for (index in 0 until numberCases.getJSONArray("round").length()) {
            val item = numberCases.getJSONArray("round").getJSONObject(index)
            assertEquals(item.getDouble("output"), NumberUtils.round(item.getDouble("value"), item.getInt("decimals")), 0.001)
        }
        for (index in 0 until numberCases.getJSONArray("in_range").length()) {
            val item = numberCases.getJSONArray("in_range").getJSONObject(index)
            assertEquals(item.getBoolean("output"), NumberUtils.inRange(item.getDouble("value"), item.getDouble("min"), item.getDouble("max")))
        }

        val objectCases = fixtures.getJSONObject("object")
        for (index in 0 until objectCases.getJSONArray("keys").length()) {
            val item = objectCases.getJSONArray("keys").getJSONObject(index)
            val input = linkedMapOf("b" to 2, "a" to 1)
            assertEquals(jsonArrayToStringList(item.getJSONArray("output")), ObjectUtils.keys(input))
        }
        for (index in 0 until objectCases.getJSONArray("values").length()) {
            val item = objectCases.getJSONArray("values").getJSONObject(index)
            val input = linkedMapOf("b" to 2, "a" to 1)
            assertEquals(jsonArrayToIntList(item.getJSONArray("output")), ObjectUtils.values(input))
        }

        val compareCases = fixtures.getJSONObject("compare")
        for (index in 0 until compareCases.getJSONArray("deep_equal").length()) {
            val item = compareCases.getJSONArray("deep_equal").getJSONObject(index)
            assertEquals(
                item.getBoolean("output"),
                CompareUtils.deepEqual(jsonValueToAny(item.get("left")), jsonValueToAny(item.get("right")))
            )
        }
        for (index in 0 until compareCases.getJSONArray("deep_clone").length()) {
            val item = compareCases.getJSONArray("deep_clone").getJSONObject(index)
            val cloned = CompareUtils.deepClone(jsonValueToAny(item.get("input")))
            assertEquals(jsonValueToAny(item.get("output")), cloned)
        }
        val parseNumberCases = fixtures.getJSONObject("i18n").getJSONArray("parse_number_locale")
        for (index in 0 until parseNumberCases.length()) {
            val item = parseNumberCases.getJSONObject(index)
            val expected = if (item.isNull("output")) null else item.getDouble("output")
            assertEquals(expected, I18nUtils.parseNumberLocale(item.getString("input"), item.getString("locale")))
        }
        val formatDatetimeCases = fixtures.getJSONObject("i18n").getJSONArray("format_datetime_locale")
        for (index in 0 until formatDatetimeCases.length()) {
            val item = formatDatetimeCases.getJSONObject(index)
            val parsed = DateTimeUtils.parseDatetime(item.getString("input"), DateTimeUtils.DEFAULT_PATTERN)
            assertNotNull(parsed)
            val formatted = I18nUtils.formatDatetimeLocale(parsed!!, item.getString("locale"))
            assertTrue(formatted.contains(item.getString("contains")))
        }

        for (index in 0 until stringCases.getJSONArray("trim").length()) {
            val item = stringCases.getJSONArray("trim").getJSONObject(index)
            assertEquals(item.getString("output"), StringUtils.trim(item.getString("input")))
        }
        for (index in 0 until stringCases.getJSONArray("truncate").length()) {
            val item = stringCases.getJSONArray("truncate").getJSONObject(index)
            assertEquals(item.getString("output"), StringUtils.truncate(item.getString("input"), item.getInt("max_len"), item.getString("suffix")))
        }
        for (index in 0 until stringCases.getJSONArray("capitalize").length()) {
            val item = stringCases.getJSONArray("capitalize").getJSONObject(index)
            assertEquals(item.getString("output"), StringUtils.capitalize(item.getString("input")))
        }
        for (index in 0 until stringCases.getJSONArray("camel_case").length()) {
            val item = stringCases.getJSONArray("camel_case").getJSONObject(index)
            assertEquals(item.getString("output"), StringUtils.camelCase(item.getString("input")))
        }
        for (index in 0 until stringCases.getJSONArray("snake_case").length()) {
            val item = stringCases.getJSONArray("snake_case").getJSONObject(index)
            assertEquals(item.getString("output"), StringUtils.snakeCase(item.getString("input")))
        }
        for (index in 0 until stringCases.getJSONArray("kebab_case").length()) {
            val item = stringCases.getJSONArray("kebab_case").getJSONObject(index)
            assertEquals(item.getString("output"), StringUtils.kebabCase(item.getString("input")))
        }
        for (index in 0 until stringCases.getJSONArray("normalize_whitespace").length()) {
            val item = stringCases.getJSONArray("normalize_whitespace").getJSONObject(index)
            assertEquals(item.getString("output"), StringUtils.normalizeWhitespace(item.getString("input")))
        }
        for (index in 0 until stringCases.getJSONArray("template").length()) {
            val item = stringCases.getJSONArray("template").getJSONObject(index)
            val values = jsonObjectToMap(item.getJSONObject("values")).mapValues { it.value as String }
            assertEquals(item.getString("output"), StringUtils.template(item.getString("template"), values))
        }

        for (index in 0 until datetimeCases.getJSONArray("parse").length()) {
            val item = datetimeCases.getJSONArray("parse").getJSONObject(index)
            assertEquals(item.getBoolean("valid"), DateTimeUtils.parseDatetime(item.getString("input"), DateTimeUtils.DEFAULT_PATTERN) != null)
        }
        for (index in 0 until datetimeCases.getJSONArray("add_days").length()) {
            val item = datetimeCases.getJSONArray("add_days").getJSONObject(index)
            val parsed = DateTimeUtils.parseDatetime(item.getString("input"), DateTimeUtils.DEFAULT_PATTERN)!!
            assertEquals(item.getString("output"), DateTimeUtils.formatDatetime(DateTimeUtils.addDays(parsed, item.getLong("days")), DateTimeUtils.DEFAULT_PATTERN))
        }
        for (index in 0 until datetimeCases.getJSONArray("add_hours").length()) {
            val item = datetimeCases.getJSONArray("add_hours").getJSONObject(index)
            val parsed = DateTimeUtils.parseDatetime(item.getString("input"), DateTimeUtils.DEFAULT_PATTERN)!!
            assertEquals(item.getString("output"), DateTimeUtils.formatDatetime(DateTimeUtils.addHours(parsed, item.getLong("hours")), DateTimeUtils.DEFAULT_PATTERN))
        }
        for (index in 0 until datetimeCases.getJSONArray("add_minutes").length()) {
            val item = datetimeCases.getJSONArray("add_minutes").getJSONObject(index)
            val parsed = DateTimeUtils.parseDatetime(item.getString("input"), DateTimeUtils.DEFAULT_PATTERN)!!
            assertEquals(item.getString("output"), DateTimeUtils.formatDatetime(DateTimeUtils.addMinutes(parsed, item.getLong("minutes")), DateTimeUtils.DEFAULT_PATTERN))
        }
        for (index in 0 until datetimeCases.getJSONArray("is_before").length()) {
            val item = datetimeCases.getJSONArray("is_before").getJSONObject(index)
            val left = DateTimeUtils.parseDatetime(item.getString("left"), DateTimeUtils.DEFAULT_PATTERN)!!
            val right = DateTimeUtils.parseDatetime(item.getString("right"), DateTimeUtils.DEFAULT_PATTERN)!!
            assertEquals(item.getBoolean("output"), DateTimeUtils.isBefore(left, right))
        }
        for (index in 0 until datetimeCases.getJSONArray("is_after").length()) {
            val item = datetimeCases.getJSONArray("is_after").getJSONObject(index)
            val left = DateTimeUtils.parseDatetime(item.getString("left"), DateTimeUtils.DEFAULT_PATTERN)!!
            val right = DateTimeUtils.parseDatetime(item.getString("right"), DateTimeUtils.DEFAULT_PATTERN)!!
            assertEquals(item.getBoolean("output"), DateTimeUtils.isAfter(left, right))
        }
        for (index in 0 until datetimeCases.getJSONArray("start_of_day_utc").length()) {
            val item = datetimeCases.getJSONArray("start_of_day_utc").getJSONObject(index)
            val parsed = DateTimeUtils.parseDatetime(item.getString("input"), DateTimeUtils.DEFAULT_PATTERN)!!
            assertEquals(item.getString("output"), DateTimeUtils.formatDatetime(DateTimeUtils.startOfDayUtc(parsed), DateTimeUtils.DEFAULT_PATTERN))
        }
        for (index in 0 until datetimeCases.getJSONArray("end_of_day_utc").length()) {
            val item = datetimeCases.getJSONArray("end_of_day_utc").getJSONObject(index)
            val parsed = DateTimeUtils.parseDatetime(item.getString("input"), DateTimeUtils.DEFAULT_PATTERN)!!
            assertEquals(item.getString("output"), DateTimeUtils.formatDatetime(DateTimeUtils.endOfDayUtc(parsed), DateTimeUtils.DEFAULT_PATTERN))
        }

        for (index in 0 until numberCases.getJSONArray("format_number").length()) {
            val item = numberCases.getJSONArray("format_number").getJSONObject(index)
            assertEquals(item.getString("output"), NumberUtils.formatNumber(item.getDouble("value"), item.getInt("decimals")))
        }
        for (index in 0 until numberCases.getJSONArray("parse_number").length()) {
            val item = numberCases.getJSONArray("parse_number").getJSONObject(index)
            val expected = if (item.isNull("output")) null else item.getDouble("output")
            assertEquals(expected, NumberUtils.parseNumber(item.getString("input")))
        }
        for (index in 0 until numberCases.getJSONArray("is_integer").length()) {
            val item = numberCases.getJSONArray("is_integer").getJSONObject(index)
            assertEquals(item.getBoolean("output"), NumberUtils.isInteger(item.getDouble("value")))
        }
        for (index in 0 until numberCases.getJSONArray("abs").length()) {
            val item = numberCases.getJSONArray("abs").getJSONObject(index)
            assertEquals(item.getDouble("output"), NumberUtils.abs(item.getDouble("input")), 0.001)
        }

        val collectionCases = fixtures.getJSONObject("collection")
        for (index in 0 until collectionCases.getJSONArray("unique").length()) {
            val item = collectionCases.getJSONArray("unique").getJSONObject(index)
            assertEquals(jsonArrayToIntList(item.getJSONArray("output")), CollectionUtils.unique(jsonArrayToIntList(item.getJSONArray("input"))))
        }
        for (index in 0 until collectionCases.getJSONArray("chunk").length()) {
            val item = collectionCases.getJSONArray("chunk").getJSONObject(index)
            val expected = (0 until item.getJSONArray("output").length()).map {
                jsonArrayToIntList(item.getJSONArray("output").getJSONArray(it))
            }
            assertEquals(expected, CollectionUtils.chunk(jsonArrayToIntList(item.getJSONArray("input")), item.getInt("size")))
        }
        for (index in 0 until collectionCases.getJSONArray("flatten").length()) {
            val item = collectionCases.getJSONArray("flatten").getJSONObject(index)
            val nested = item.getJSONArray("input")
            val input = (0 until nested.length()).map { jsonArrayToIntList(nested.getJSONArray(it)) }
            assertEquals(jsonArrayToIntList(item.getJSONArray("output")), CollectionUtils.flatten(input))
        }
        for (index in 0 until collectionCases.getJSONArray("compact").length()) {
            val item = collectionCases.getJSONArray("compact").getJSONObject(index)
            val inputArray = item.getJSONArray("input")
            val input = (0 until inputArray.length()).map { arrayIndex ->
                if (inputArray.isNull(arrayIndex)) null else inputArray.getInt(arrayIndex)
            }
            assertEquals(jsonArrayToIntList(item.getJSONArray("output")), CollectionUtils.compact(input))
        }
        for (index in 0 until collectionCases.getJSONArray("group_by").length()) {
            val item = collectionCases.getJSONArray("group_by").getJSONObject(index)
            val input = (0 until item.getJSONArray("input").length()).map { jsonObjectToMap(item.getJSONArray("input").getJSONObject(it)) }
            val grouped = CollectionUtils.groupBy(input) { entry -> entry["type"] as String }
            assertEquals(jsonValueToAny(item.get("output")), grouped)
        }
        for (index in 0 until collectionCases.getJSONArray("sort_by").length()) {
            val item = collectionCases.getJSONArray("sort_by").getJSONObject(index)
            val input = (0 until item.getJSONArray("input").length()).map { jsonObjectToMap(item.getJSONArray("input").getJSONObject(it)) }
            val sorted = CollectionUtils.sortBy(input) { entry -> entry["k"] as String }
            assertEquals(jsonValueToAny(item.get("output")), sorted)
        }
        for (index in 0 until collectionCases.getJSONArray("key_by").length()) {
            val item = collectionCases.getJSONArray("key_by").getJSONObject(index)
            val input = (0 until item.getJSONArray("input").length()).map { jsonObjectToMap(item.getJSONArray("input").getJSONObject(it)) }
            val keyed = CollectionUtils.keyBy(input) { entry -> entry["id"] as String }
            assertEquals(jsonValueToAny(item.get("output")), keyed)
        }
        for (index in 0 until collectionCases.getJSONArray("filter").length()) {
            val item = collectionCases.getJSONArray("filter").getJSONObject(index)
            val filtered = CollectionUtils.filter(jsonArrayToIntList(item.getJSONArray("input"))) { value -> value > item.getInt("threshold") }
            assertEquals(jsonArrayToIntList(item.getJSONArray("output")), filtered)
        }
        for (index in 0 until collectionCases.getJSONArray("find").length()) {
            val item = collectionCases.getJSONArray("find").getJSONObject(index)
            assertEquals(item.getInt("output"), CollectionUtils.find(jsonArrayToIntList(item.getJSONArray("input"))) { value -> value > item.getInt("threshold") })
        }

        val validationCases = fixtures.getJSONObject("validation")
        for (index in 0 until validationCases.getJSONArray("is_uuid").length()) {
            val item = validationCases.getJSONArray("is_uuid").getJSONObject(index)
            assertEquals(item.getBoolean("output"), ValidationUtils.isUuid(item.getString("input")))
        }
        for (index in 0 until validationCases.getJSONArray("is_url").length()) {
            val item = validationCases.getJSONArray("is_url").getJSONObject(index)
            assertEquals(item.getBoolean("output"), ValidationUtils.isUrl(item.getString("input")))
        }
        for (index in 0 until validationCases.getJSONArray("is_numeric").length()) {
            val item = validationCases.getJSONArray("is_numeric").getJSONObject(index)
            assertEquals(item.getBoolean("output"), ValidationUtils.isNumeric(item.getString("input")))
        }

        val idCases = fixtures.getJSONObject("id")
        for (index in 0 until idCases.getJSONArray("uuid").length()) {
            val item = idCases.getJSONArray("uuid").getJSONObject(index)
            assertTrue(IdUtils.uuid().matches(Regex("^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$", RegexOption.IGNORE_CASE)))
            assertEquals("uuid-v4", item.getString("pattern"))
        }
        for (index in 0 until idCases.getJSONArray("random_string").length()) {
            val item = idCases.getJSONArray("random_string").getJSONObject(index)
            val value = IdUtils.randomString(item.getInt("length"))
            assertEquals(item.getInt("length"), value.length)
            assertTrue(value.matches(Regex("^[A-Za-z0-9]+$")))
        }

        val encodingCases = fixtures.getJSONObject("encoding")
        for (index in 0 until encodingCases.getJSONArray("base64_encode").length()) {
            val item = encodingCases.getJSONArray("base64_encode").getJSONObject(index)
            assertEquals(item.getString("output"), EncodingUtils.base64Encode(item.getString("input").toByteArray()))
        }
        for (index in 0 until encodingCases.getJSONArray("base64_decode").length()) {
            val item = encodingCases.getJSONArray("base64_decode").getJSONObject(index)
            assertEquals(item.getString("output"), String(EncodingUtils.base64Decode(item.getString("input"))!!))
        }
        for (index in 0 until encodingCases.getJSONArray("hex_encode").length()) {
            val item = encodingCases.getJSONArray("hex_encode").getJSONObject(index)
            assertEquals(item.getString("output"), EncodingUtils.hexEncode(item.getString("input").toByteArray()))
        }
        for (index in 0 until encodingCases.getJSONArray("hex_decode").length()) {
            val item = encodingCases.getJSONArray("hex_decode").getJSONObject(index)
            assertEquals(item.getString("output"), String(EncodingUtils.hexDecode(item.getString("input"))!!))
        }

        val pathCases = fixtures.getJSONObject("path")
        for (index in 0 until pathCases.getJSONArray("join_path").length()) {
            val item = pathCases.getJSONArray("join_path").getJSONObject(index)
            assertEquals(item.getString("output"), PathUtils.joinPath(*jsonArrayToStringList(item.getJSONArray("segments")).toTypedArray()))
        }
        for (index in 0 until pathCases.getJSONArray("normalize_path").length()) {
            val item = pathCases.getJSONArray("normalize_path").getJSONObject(index)
            assertEquals(item.getString("output"), PathUtils.normalizePath(item.getString("input")))
        }

        for (index in 0 until objectCases.getJSONArray("pick").length()) {
            val item = objectCases.getJSONArray("pick").getJSONObject(index)
            assertEquals(jsonValueToAny(item.get("output")), ObjectUtils.pick(jsonObjectToMap(item.getJSONObject("source")), jsonArrayToStringList(item.getJSONArray("keys"))))
        }
        for (index in 0 until objectCases.getJSONArray("omit").length()) {
            val item = objectCases.getJSONArray("omit").getJSONObject(index)
            assertEquals(jsonValueToAny(item.get("output")), ObjectUtils.omit(jsonObjectToMap(item.getJSONObject("source")), jsonArrayToStringList(item.getJSONArray("keys"))))
        }
        for (index in 0 until objectCases.getJSONArray("compact").length()) {
            val item = objectCases.getJSONArray("compact").getJSONObject(index)
            assertEquals(jsonValueToAny(item.get("output")), ObjectUtils.compact(jsonObjectToMap(item.getJSONObject("input"))))
        }

        val optionalCases = fixtures.getJSONObject("optional")
        for (index in 0 until optionalCases.getJSONArray("default_if_blank").length()) {
            val item = optionalCases.getJSONArray("default_if_blank").getJSONObject(index)
            assertEquals(item.getString("output"), OptionalUtils.defaultIfBlank(item.getString("input"), item.getString("default")))
        }

        val resultCases = fixtures.getJSONObject("result")
        for (index in 0 until resultCases.getJSONArray("is_ok").length()) {
            val item = resultCases.getJSONArray("is_ok").getJSONObject(index)
            val result = if (item.getString("kind") == "ok") ResultValue.ok(item.getInt("value")) else ResultValue.err(item.getString("message"))
            assertEquals(item.getBoolean("output"), ResultUtils.isOk(result))
        }
        for (index in 0 until resultCases.getJSONArray("is_err").length()) {
            val item = resultCases.getJSONArray("is_err").getJSONObject(index)
            val result = if (item.getString("kind") == "ok") ResultValue.ok(item.getInt("value")) else ResultValue.err(item.getString("message"))
            assertEquals(item.getBoolean("output"), ResultUtils.isErr(result))
        }

        val bytesCases = fixtures.getJSONObject("bytes")
        for (index in 0 until bytesCases.getJSONArray("format_bytes").length()) {
            val item = bytesCases.getJSONArray("format_bytes").getJSONObject(index)
            assertEquals(item.getString("output"), BytesUtils.formatBytes(item.getLong("value"), item.getInt("decimals")))
        }

        val currencyCases = fixtures.getJSONObject("currency")
        for (index in 0 until currencyCases.getJSONArray("is_currency_code").length()) {
            val item = currencyCases.getJSONArray("is_currency_code").getJSONObject(index)
            assertEquals(item.getBoolean("output"), CurrencyUtils.isCurrencyCode(item.getString("input")))
        }
        for (index in 0 until currencyCases.getJSONArray("minor_unit_exponent").length()) {
            val item = currencyCases.getJSONArray("minor_unit_exponent").getJSONObject(index)
            val expected = if (item.isNull("output")) null else item.getInt("output")
            assertEquals(expected, CurrencyUtils.minorUnitExponent(item.getString("code")))
        }
        for (index in 0 until currencyCases.getJSONArray("to_minor_units").length()) {
            val item = currencyCases.getJSONArray("to_minor_units").getJSONObject(index)
            assertEquals(item.getLong("output"), CurrencyUtils.toMinorUnits(item.getDouble("amount"), item.getString("code")))
        }
        for (index in 0 until currencyCases.getJSONArray("from_minor_units").length()) {
            val item = currencyCases.getJSONArray("from_minor_units").getJSONObject(index)
            val actual = CurrencyUtils.fromMinorUnits(item.getLong("minor"), item.getString("code"))
            assertNotNull(actual)
            assertEquals(item.getDouble("output"), actual!!, 0.001)
        }
        for (index in 0 until currencyCases.getJSONArray("format_currency").length()) {
            val item = currencyCases.getJSONArray("format_currency").getJSONObject(index)
            assertEquals(item.getString("output"), CurrencyUtils.formatCurrency(item.getDouble("amount"), item.getString("code"), item.getString("locale")))
        }

        val bloomCases = fixtures.getJSONObject("bloom")
        for (index in 0 until bloomCases.getJSONArray("create").length()) {
            val item = bloomCases.getJSONArray("create").getJSONObject(index)
            val filter = BloomUtils.create(item.getInt("expected_items"), item.getDouble("false_positive_rate"))
            assertEquals(item.getInt("bit_count"), filter.bitCount)
            assertEquals(item.getInt("hash_count"), filter.hashCount)
        }
        for (index in 0 until bloomCases.getJSONArray("estimate_bit_count").length()) {
            val item = bloomCases.getJSONArray("estimate_bit_count").getJSONObject(index)
            assertEquals(item.getInt("output"), BloomUtils.estimateBitCount(item.getInt("expected_items"), item.getDouble("false_positive_rate")))
        }
        for (index in 0 until bloomCases.getJSONArray("estimate_hash_count").length()) {
            val item = bloomCases.getJSONArray("estimate_hash_count").getJSONObject(index)
            assertEquals(item.getInt("output"), BloomUtils.estimateHashCount(item.getInt("expected_items"), item.getInt("bit_count")))
        }
        for (index in 0 until bloomCases.getJSONArray("might_contain").length()) {
            val item = bloomCases.getJSONArray("might_contain").getJSONObject(index)
            val filter = BloomUtils.create(128, 0.01)
            val added = item.getJSONArray("added")
            for (addedIndex in 0 until added.length()) {
                BloomUtils.add(filter, added.getString(addedIndex))
            }
            assertEquals(item.getBoolean("present_output"), BloomUtils.mightContain(filter, item.getString("present")))
            assertEquals(item.getBoolean("absent_output"), BloomUtils.mightContain(filter, item.getString("absent")))
        }
    }

    private fun jsonObjectToMap(value: JSONObject): Map<String, Any?> {
        val result = linkedMapOf<String, Any?>()
        for (key in value.keys()) {
            result[key] = jsonValueToAny(value.get(key))
        }
        return result
    }

    private fun jsonArrayToStringList(value: org.json.JSONArray): List<String> {
        val result = ArrayList<String>(value.length())
        for (index in 0 until value.length()) {
            result.add(value.getString(index))
        }
        return result
    }

    private fun jsonArrayToIntList(value: org.json.JSONArray): List<Int> {
        val result = ArrayList<Int>(value.length())
        for (index in 0 until value.length()) {
            result.add(value.getInt(index))
        }
        return result
    }

    private fun jsonValueToAny(value: Any?): Any? {
        return when (value) {
            is JSONObject -> jsonObjectToMap(value)
            is org.json.JSONArray -> {
                val result = ArrayList<Any?>(value.length())
                for (index in 0 until value.length()) {
                    result.add(jsonValueToAny(value.get(index)))
                }
                result
            }
            JSONObject.NULL -> null
            else -> value
        }
    }
}
