using System.Text.Json;
using Sdkwork.Utils;
using Xunit;

namespace Sdkwork.Utils.Tests;

public class ConformanceTests
{
    private static readonly JsonDocument Fixtures = LoadFixtures();

    private static JsonDocument LoadFixtures()
    {
        var path = Path.GetFullPath(Path.Combine(AppContext.BaseDirectory, "../../../../../../specs/conformance/fixtures.json"));
        return JsonDocument.Parse(File.ReadAllText(path));
    }

    [Fact]
    public void ConformanceFixtures()
    {
        var root = Fixtures.RootElement;

        foreach (var item in root.GetProperty("string").GetProperty("is_blank").EnumerateArray())
        {
            var input = item.GetProperty("input").ValueKind == JsonValueKind.Null ? null : item.GetProperty("input").GetString();
            Assert.Equal(item.GetProperty("output").GetBoolean(), StringUtils.IsBlank(input));
        }

        foreach (var item in root.GetProperty("string").GetProperty("trim").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("output").GetString(), StringUtils.Trim(item.GetProperty("input").GetString()));
        }

        foreach (var item in root.GetProperty("string").GetProperty("truncate").EnumerateArray())
        {
            Assert.Equal(
                item.GetProperty("output").GetString(),
                StringUtils.Truncate(
                    item.GetProperty("input").GetString()!,
                    item.GetProperty("max_len").GetInt32(),
                    item.GetProperty("suffix").GetString()!));
        }

        foreach (var item in root.GetProperty("string").GetProperty("camel_case").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("output").GetString(), StringUtils.CamelCase(item.GetProperty("input").GetString()!));
        }

        var slug = root.GetProperty("string").GetProperty("slugify")[0];
        Assert.Equal(slug.GetProperty("output").GetString(), StringUtils.Slugify(slug.GetProperty("input").GetString()!));

        foreach (var item in root.GetProperty("string").GetProperty("starts_with").EnumerateArray())
        {
            Assert.Equal(
                item.GetProperty("output").GetBoolean(),
                StringUtils.StartsWith(item.GetProperty("input").GetString()!, item.GetProperty("prefix").GetString()!));
        }

        foreach (var item in root.GetProperty("string").GetProperty("ends_with").EnumerateArray())
        {
            Assert.Equal(
                item.GetProperty("output").GetBoolean(),
                StringUtils.EndsWith(item.GetProperty("input").GetString()!, item.GetProperty("suffix").GetString()!));
        }

        foreach (var item in root.GetProperty("string").GetProperty("contains").EnumerateArray())
        {
            Assert.Equal(
                item.GetProperty("output").GetBoolean(),
                StringUtils.Contains(item.GetProperty("input").GetString()!, item.GetProperty("substring").GetString()!));
        }

        foreach (var item in root.GetProperty("string").GetProperty("replace_all").EnumerateArray())
        {
            Assert.Equal(
                item.GetProperty("output").GetString(),
                StringUtils.ReplaceAll(
                    item.GetProperty("input").GetString()!,
                    item.GetProperty("search").GetString()!,
                    item.GetProperty("replacement").GetString()!));
        }

        foreach (var item in root.GetProperty("string").GetProperty("split").EnumerateArray())
        {
            var expected = item.GetProperty("output").EnumerateArray().Select(part => part.GetString()!).ToList();
            var actual = StringUtils.Split(
                item.GetProperty("input").GetString()!,
                item.GetProperty("delimiter").GetString()!,
                item.GetProperty("trim_parts").GetBoolean());
            Assert.Equal(expected, actual);
        }

        foreach (var item in root.GetProperty("string").GetProperty("join").EnumerateArray())
        {
            var parts = item.GetProperty("parts").EnumerateArray().Select(part => part.GetString()!).ToList();
            Assert.Equal(item.GetProperty("output").GetString(), StringUtils.Join(parts, item.GetProperty("separator").GetString()!));
        }

        var diff = root.GetProperty("datetime").GetProperty("diff_millis");
        var earlier = DateTimeUtils.ParseDatetime(diff.GetProperty("earlier").GetString()!)!.Value;
        var later = DateTimeUtils.ParseDatetime(diff.GetProperty("later").GetString()!)!.Value;
        Assert.Equal(diff.GetProperty("output").GetInt64(), DateTimeUtils.DiffMillis(earlier, later));

        foreach (var item in root.GetProperty("datetime").GetProperty("to_unix_millis").EnumerateArray())
        {
            var parsed = DateTimeUtils.ParseDatetime(item.GetProperty("input").GetString()!)!.Value;
            Assert.Equal(item.GetProperty("output").GetInt64(), DateTimeUtils.ToUnixMillis(parsed));
        }

        foreach (var item in root.GetProperty("datetime").GetProperty("from_unix_millis").EnumerateArray())
        {
            var parsed = DateTimeUtils.FromUnixMillis(item.GetProperty("input").GetInt64())!.Value;
            Assert.Equal(item.GetProperty("output").GetString(), DateTimeUtils.FormatDatetime(parsed));
        }

        var hello = root.GetProperty("encoding").GetProperty("base64_encode")[0].GetProperty("input").GetString()!;
        Assert.Equal(
            root.GetProperty("encoding").GetProperty("base64_encode")[0].GetProperty("output").GetString(),
            EncodingUtils.Base64Encode(hello));
        Assert.Equal(
            root.GetProperty("encoding").GetProperty("hex_encode")[0].GetProperty("output").GetString(),
            EncodingUtils.HexEncode(System.Text.Encoding.UTF8.GetBytes(hello)));

        foreach (var item in root.GetProperty("encoding").GetProperty("url_encode").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("output").GetString(), EncodingUtils.UrlEncode(item.GetProperty("input").GetString()!));
        }

        foreach (var item in root.GetProperty("encoding").GetProperty("url_decode").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("output").GetString(), EncodingUtils.UrlDecode(item.GetProperty("input").GetString()!));
        }
        foreach (var item in root.GetProperty("encoding").GetProperty("base64url_encode").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("output").GetString(), EncodingUtils.Base64UrlEncode(item.GetProperty("input").GetString()!));
        }
        foreach (var item in root.GetProperty("encoding").GetProperty("base64url_decode").EnumerateArray())
        {
            var decoded = EncodingUtils.Base64UrlDecode(item.GetProperty("input").GetString()!);
            Assert.Equal(item.GetProperty("output").GetString(), System.Text.Encoding.UTF8.GetString(decoded!));
        }

        var merge = root.GetProperty("object").GetProperty("deep_merge");
        AssertDeepEqual(
            JsonElementToObject(merge.GetProperty("output")),
            ObjectUtils.DeepMerge(
                JsonElementToDictionary(merge.GetProperty("base")),
                JsonElementToDictionary(merge.GetProperty("overlay"))));

        var shallow = root.GetProperty("object").GetProperty("shallow_merge");
        AssertDeepEqual(
            JsonElementToObject(shallow.GetProperty("output")),
            ObjectUtils.ShallowMerge(
                JsonElementToDictionary(shallow.GetProperty("base")),
                JsonElementToDictionary(shallow.GetProperty("overlay"))));

        var pathCase = root.GetProperty("object").GetProperty("set_get_path");
        var target = ObjectUtils.SetPath([], pathCase.GetProperty("path").GetString()!, pathCase.GetProperty("value").GetString());
        Assert.Equal(pathCase.GetProperty("output").GetString(), ObjectUtils.GetPath(target, pathCase.GetProperty("path").GetString()!));

        var hasPathBase = new Dictionary<string, object?>
        {
            ["user"] = new Dictionary<string, object?> { ["name"] = "Ada" },
        };
        foreach (var item in root.GetProperty("object").GetProperty("has_path").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("exists").GetBoolean(), ObjectUtils.HasPath(hasPathBase, item.GetProperty("path").GetString()!));
        }

        var sha = root.GetProperty("crypto").GetProperty("sha256_hash")[0];
        Assert.Equal(sha.GetProperty("output").GetString(), CryptoUtils.Sha256Hash(sha.GetProperty("input").GetString()!));

        var hmac = root.GetProperty("crypto").GetProperty("hmac_sha256")[0];
        Assert.Equal(
            hmac.GetProperty("output").GetString(),
            CryptoUtils.HmacSha256(hmac.GetProperty("input").GetString()!, hmac.GetProperty("secret").GetString()!));

        foreach (var item in root.GetProperty("crypto").GetProperty("secure_compare").EnumerateArray())
        {
            Assert.Equal(
                item.GetProperty("output").GetBoolean(),
                CryptoUtils.SecureCompare(item.GetProperty("left").GetString()!, item.GetProperty("right").GetString()!));
        }

        foreach (var item in root.GetProperty("i18n").GetProperty("format_number_locale").EnumerateArray())
        {
            Assert.Equal(
                item.GetProperty("output").GetString(),
                I18nUtils.FormatNumberLocale(
                    item.GetProperty("value").GetDouble(),
                    item.GetProperty("locale").GetString()!,
                    item.GetProperty("decimals").GetInt32()));
        }

        var coalesce = root.GetProperty("optional").GetProperty("coalesce")[0];
        Assert.Equal(
            coalesce.GetProperty("output").GetString(),
            OptionalUtils.Coalesce(null, "", "  ", "ok"));

        var clamp = root.GetProperty("number").GetProperty("clamp")[0];
        Assert.Equal(
            clamp.GetProperty("output").GetDouble(),
            NumberUtils.Clamp(clamp.GetProperty("value").GetDouble(), clamp.GetProperty("min").GetDouble(), clamp.GetProperty("max").GetDouble()),
            3);

        foreach (var item in root.GetProperty("number").GetProperty("parse_int").EnumerateArray())
        {
            var expected = item.GetProperty("output").ValueKind == JsonValueKind.Null
                ? null
                : (long?)item.GetProperty("output").GetInt64();
            Assert.Equal(expected, NumberUtils.ParseInt(item.GetProperty("input").GetString()!));
        }

        foreach (var item in root.GetProperty("number").GetProperty("percent_format").EnumerateArray())
        {
            Assert.Equal(
                item.GetProperty("output").GetString(),
                NumberUtils.PercentFormat(item.GetProperty("value").GetDouble(), item.GetProperty("decimals").GetInt32()));
        }

        var unique = root.GetProperty("collection").GetProperty("unique")[0];
        var expectedUnique = unique.GetProperty("output").EnumerateArray().Select(item => item.GetInt32()).ToList();
        var actualUnique = CollectionUtils.Unique(unique.GetProperty("input").EnumerateArray().Select(item => item.GetInt32())).ToList();
        Assert.Equal(expectedUnique, actualUnique);

        foreach (var item in root.GetProperty("collection").GetProperty("first").EnumerateArray())
        {
            var input = item.GetProperty("input").EnumerateArray().Select(value => value.GetInt32()).ToList();
            object? expected = item.GetProperty("output").ValueKind == JsonValueKind.Null
                ? null
                : item.GetProperty("output").GetInt32();
            Assert.Equal(expected, CollectionUtils.First(input));
        }

        foreach (var item in root.GetProperty("collection").GetProperty("last").EnumerateArray())
        {
            var input = item.GetProperty("input").EnumerateArray().Select(value => value.GetInt32()).ToList();
            object? expected = item.GetProperty("output").ValueKind == JsonValueKind.Null
                ? null
                : item.GetProperty("output").GetInt32();
            Assert.Equal(expected, CollectionUtils.Last(input));
        }

        var email = root.GetProperty("validation").GetProperty("is_email")[0];
        Assert.Equal(email.GetProperty("output").GetBoolean(), ValidationUtils.IsEmail(email.GetProperty("input").GetString()!));

        foreach (var item in root.GetProperty("validation").GetProperty("is_ipv4").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("output").GetBoolean(), ValidationUtils.IsIpv4(item.GetProperty("input").GetString()!));
        }
        foreach (var item in root.GetProperty("validation").GetProperty("is_ipv6").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("output").GetBoolean(), ValidationUtils.IsIpv6(item.GetProperty("input").GetString()!));
        }
        foreach (var item in root.GetProperty("validation").GetProperty("is_phone_e164").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("output").GetBoolean(), ValidationUtils.IsPhoneE164(item.GetProperty("input").GetString()!));
        }

        var pathJoin = root.GetProperty("path").GetProperty("join_path")[0];
        Assert.Equal(
            pathJoin.GetProperty("output").GetString(),
            PathUtils.JoinPath(pathJoin.GetProperty("segments").EnumerateArray().Select(item => item.GetString()!).ToArray()));

        foreach (var item in root.GetProperty("result").GetProperty("unwrap_or").EnumerateArray())
        {
            var result = item.GetProperty("kind").GetString() == "ok"
                ? ResultValue<int>.Success(item.GetProperty("value").GetInt32())
                : ResultValue<int>.Failure(item.GetProperty("message").GetString()!);
            Assert.Equal(item.GetProperty("output").GetInt32(), ResultUtils.UnwrapOr(result, item.GetProperty("default").GetInt32()));
        }

        foreach (var item in root.GetProperty("boolean").GetProperty("parse_bool").EnumerateArray())
        {
            var expected = item.GetProperty("output").ValueKind == JsonValueKind.Null
                ? null
                : (bool?)item.GetProperty("output").GetBoolean();
            Assert.Equal(expected, BooleanUtils.ParseBool(item.GetProperty("input").GetString()!));
        }

        foreach (var item in root.GetProperty("boolean").GetProperty("is_truthy").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("output").GetBoolean(), BooleanUtils.IsTruthy(item.GetProperty("input").GetString()));
        }

        foreach (var item in root.GetProperty("string").GetProperty("repeat").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("output").GetString(), StringUtils.Repeat(item.GetProperty("input").GetString()!, item.GetProperty("count").GetInt32()));
        }

        foreach (var item in root.GetProperty("datetime").GetProperty("now").EnumerateArray())
        {
            Assert.NotNull(DateTimeUtils.Now());
        }

        foreach (var item in root.GetProperty("datetime").GetProperty("is_same_instant").EnumerateArray())
        {
            var left = DateTimeUtils.ParseDatetime(item.GetProperty("left").GetString()!)!.Value;
            var right = DateTimeUtils.ParseDatetime(item.GetProperty("right").GetString()!)!.Value;
            Assert.Equal(item.GetProperty("output").GetBoolean(), DateTimeUtils.IsSameInstant(left, right));
        }

        foreach (var item in root.GetProperty("number").GetProperty("round").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("output").GetDouble(), NumberUtils.Round(item.GetProperty("value").GetDouble(), item.GetProperty("decimals").GetInt32()), 3);
        }

        foreach (var item in root.GetProperty("number").GetProperty("in_range").EnumerateArray())
        {
            Assert.Equal(
                item.GetProperty("output").GetBoolean(),
                NumberUtils.InRange(item.GetProperty("value").GetDouble(), item.GetProperty("min").GetDouble(), item.GetProperty("max").GetDouble()));
        }

        foreach (var item in root.GetProperty("object").GetProperty("keys").EnumerateArray())
        {
            var input = JsonElementToDictionary(item.GetProperty("input"));
            var expected = item.GetProperty("output").EnumerateArray().Select(value => value.GetString()!).ToList();
            Assert.Equal(expected, ObjectUtils.Keys(input));
        }

        foreach (var item in root.GetProperty("object").GetProperty("values").EnumerateArray())
        {
            var input = JsonElementToDictionary(item.GetProperty("input"));
            var expected = item.GetProperty("output").EnumerateArray().Select(JsonElementToObject).ToList();
            var actual = ObjectUtils.Values(input);
            Assert.Equal(expected.Count, actual.Count);
            for (var index = 0; index < expected.Count; index++)
            {
                Assert.Equal(expected[index], actual[index]);
            }
        }

        foreach (var item in root.GetProperty("compare").GetProperty("deep_equal").EnumerateArray())
        {
            Assert.Equal(
                item.GetProperty("output").GetBoolean(),
                CompareUtils.DeepEqual(JsonElementToObject(item.GetProperty("left")), JsonElementToObject(item.GetProperty("right"))));
        }
        foreach (var item in root.GetProperty("compare").GetProperty("deep_clone").EnumerateArray())
        {
            var cloned = CompareUtils.DeepClone(JsonElementToObject(item.GetProperty("input")));
            AssertDeepEqual(JsonElementToObject(item.GetProperty("output")), cloned);
        }
        foreach (var item in root.GetProperty("i18n").GetProperty("parse_number_locale").EnumerateArray())
        {
            double? expected = item.GetProperty("output").ValueKind == JsonValueKind.Null
                ? null
                : item.GetProperty("output").GetDouble();
            Assert.Equal(expected, I18nUtils.ParseNumberLocale(item.GetProperty("input").GetString()!, item.GetProperty("locale").GetString()!));
        }

        foreach (var item in root.GetProperty("currency").GetProperty("is_currency_code").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("output").GetBoolean(), CurrencyUtils.IsCurrencyCode(item.GetProperty("input").GetString()!));
        }
        foreach (var item in root.GetProperty("currency").GetProperty("minor_unit_exponent").EnumerateArray())
        {
            int? expected = item.GetProperty("output").ValueKind == JsonValueKind.Null
                ? null
                : item.GetProperty("output").GetInt32();
            Assert.Equal(expected, CurrencyUtils.MinorUnitExponent(item.GetProperty("code").GetString()!));
        }
        foreach (var item in root.GetProperty("currency").GetProperty("to_minor_units").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("output").GetInt64(), CurrencyUtils.ToMinorUnits(item.GetProperty("amount").GetDouble(), item.GetProperty("code").GetString()!));
        }
        foreach (var item in root.GetProperty("currency").GetProperty("from_minor_units").EnumerateArray())
        {
            var actual = CurrencyUtils.FromMinorUnits(item.GetProperty("minor").GetInt64(), item.GetProperty("code").GetString()!);
            Assert.NotNull(actual);
            Assert.Equal(item.GetProperty("output").GetDouble(), actual.Value, 3);
        }
        foreach (var item in root.GetProperty("currency").GetProperty("format_currency").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("output").GetString(), CurrencyUtils.FormatCurrency(item.GetProperty("amount").GetDouble(), item.GetProperty("code").GetString()!, item.GetProperty("locale").GetString()!));
        }

        foreach (var item in root.GetProperty("i18n").GetProperty("parse_number_locale").EnumerateArray())
        {
            double? expected = item.GetProperty("output").ValueKind == JsonValueKind.Null
                ? null
                : item.GetProperty("output").GetDouble();
            Assert.Equal(expected, I18nUtils.ParseNumberLocale(item.GetProperty("input").GetString()!, item.GetProperty("locale").GetString()!));
        }
        foreach (var item in root.GetProperty("i18n").GetProperty("format_datetime_locale").EnumerateArray())
        {
            var formatted = I18nUtils.FormatDatetimeLocaleStr(item.GetProperty("input").GetString()!, item.GetProperty("locale").GetString()!);
            Assert.NotNull(formatted);
            Assert.Contains(item.GetProperty("contains").GetString()!, formatted);
        }

        foreach (var item in root.GetProperty("string").GetProperty("capitalize").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("output").GetString(), StringUtils.Capitalize(item.GetProperty("input").GetString()!));
        }
        foreach (var item in root.GetProperty("string").GetProperty("snake_case").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("output").GetString(), StringUtils.SnakeCase(item.GetProperty("input").GetString()!));
        }
        foreach (var item in root.GetProperty("string").GetProperty("kebab_case").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("output").GetString(), StringUtils.KebabCase(item.GetProperty("input").GetString()!));
        }
        foreach (var item in root.GetProperty("string").GetProperty("mask").EnumerateArray())
        {
            Assert.Equal(
                item.GetProperty("output").GetString(),
                StringUtils.Mask(
                    item.GetProperty("input").GetString()!,
                    item.GetProperty("visible_start").GetInt32(),
                    item.GetProperty("visible_end").GetInt32()));
        }
        foreach (var item in root.GetProperty("string").GetProperty("pad_start").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("output").GetString(), StringUtils.PadStart(item.GetProperty("input").GetString()!, item.GetProperty("target_len").GetInt32()));
        }
        foreach (var item in root.GetProperty("string").GetProperty("pad_end").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("output").GetString(), StringUtils.PadEnd(item.GetProperty("input").GetString()!, item.GetProperty("target_len").GetInt32()));
        }
        foreach (var item in root.GetProperty("string").GetProperty("normalize_whitespace").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("output").GetString(), StringUtils.NormalizeWhitespace(item.GetProperty("input").GetString()!));
        }
        foreach (var item in root.GetProperty("string").GetProperty("template").EnumerateArray())
        {
            var values = item.GetProperty("values").EnumerateObject().ToDictionary(entry => entry.Name, entry => entry.Value.GetString()!);
            Assert.Equal(item.GetProperty("output").GetString(), StringUtils.Template(item.GetProperty("template").GetString()!, values));
        }

        foreach (var item in root.GetProperty("datetime").GetProperty("add_days").EnumerateArray())
        {
            var parsed = DateTimeUtils.ParseDatetime(item.GetProperty("input").GetString()!)!.Value;
            Assert.Equal(item.GetProperty("output").GetString(), DateTimeUtils.FormatDatetime(DateTimeUtils.AddDays(parsed, item.GetProperty("days").GetInt32())));
        }
        foreach (var item in root.GetProperty("datetime").GetProperty("add_hours").EnumerateArray())
        {
            var parsed = DateTimeUtils.ParseDatetime(item.GetProperty("input").GetString()!)!.Value;
            Assert.Equal(item.GetProperty("output").GetString(), DateTimeUtils.FormatDatetime(DateTimeUtils.AddHours(parsed, item.GetProperty("hours").GetInt32())));
        }
        foreach (var item in root.GetProperty("datetime").GetProperty("add_minutes").EnumerateArray())
        {
            var parsed = DateTimeUtils.ParseDatetime(item.GetProperty("input").GetString()!)!.Value;
            Assert.Equal(item.GetProperty("output").GetString(), DateTimeUtils.FormatDatetime(DateTimeUtils.AddMinutes(parsed, item.GetProperty("minutes").GetInt32())));
        }
        foreach (var item in root.GetProperty("datetime").GetProperty("is_before").EnumerateArray())
        {
            var left = DateTimeUtils.ParseDatetime(item.GetProperty("left").GetString()!)!.Value;
            var right = DateTimeUtils.ParseDatetime(item.GetProperty("right").GetString()!)!.Value;
            Assert.Equal(item.GetProperty("output").GetBoolean(), DateTimeUtils.IsBefore(left, right));
        }
        foreach (var item in root.GetProperty("datetime").GetProperty("is_after").EnumerateArray())
        {
            var left = DateTimeUtils.ParseDatetime(item.GetProperty("left").GetString()!)!.Value;
            var right = DateTimeUtils.ParseDatetime(item.GetProperty("right").GetString()!)!.Value;
            Assert.Equal(item.GetProperty("output").GetBoolean(), DateTimeUtils.IsAfter(left, right));
        }
        foreach (var item in root.GetProperty("datetime").GetProperty("start_of_day_utc").EnumerateArray())
        {
            var parsed = DateTimeUtils.ParseDatetime(item.GetProperty("input").GetString()!)!.Value;
            Assert.Equal(item.GetProperty("output").GetString(), DateTimeUtils.FormatDatetime(DateTimeUtils.StartOfDayUtc(parsed)));
        }
        foreach (var item in root.GetProperty("datetime").GetProperty("end_of_day_utc").EnumerateArray())
        {
            var parsed = DateTimeUtils.ParseDatetime(item.GetProperty("input").GetString()!)!.Value;
            Assert.Equal(item.GetProperty("output").GetString(), DateTimeUtils.FormatDatetime(DateTimeUtils.EndOfDayUtc(parsed)));
        }

        foreach (var item in root.GetProperty("number").GetProperty("format_number").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("output").GetString(), NumberUtils.FormatNumber(item.GetProperty("value").GetDouble(), item.GetProperty("decimals").GetInt32()));
        }
        foreach (var item in root.GetProperty("number").GetProperty("parse_number").EnumerateArray())
        {
            double? expected = item.GetProperty("output").ValueKind == JsonValueKind.Null ? null : item.GetProperty("output").GetDouble();
            Assert.Equal(expected, NumberUtils.ParseNumber(item.GetProperty("input").GetString()!));
        }
        foreach (var item in root.GetProperty("number").GetProperty("is_integer").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("output").GetBoolean(), NumberUtils.IsInteger(item.GetProperty("value").GetDouble()));
        }
        foreach (var item in root.GetProperty("number").GetProperty("abs").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("output").GetDouble(), NumberUtils.Abs(item.GetProperty("input").GetDouble()), 3);
        }

        foreach (var item in root.GetProperty("collection").GetProperty("chunk").EnumerateArray())
        {
            var input = item.GetProperty("input").EnumerateArray().Select(value => value.GetInt32()).ToList();
            var expected = item.GetProperty("output").EnumerateArray()
                .Select(chunk => chunk.EnumerateArray().Select(value => value.GetInt32()).ToList())
                .ToList();
            Assert.Equal(expected, CollectionUtils.Chunk(input, item.GetProperty("size").GetInt32()));
        }
        foreach (var item in root.GetProperty("collection").GetProperty("flatten").EnumerateArray())
        {
            var input = item.GetProperty("input").EnumerateArray()
                .Select(chunk => chunk.EnumerateArray().Select(value => value.GetInt32()).ToList())
                .ToList();
            var expected = item.GetProperty("output").EnumerateArray().Select(value => value.GetInt32()).ToList();
            Assert.Equal(expected, CollectionUtils.Flatten(input));
        }
        foreach (var item in root.GetProperty("collection").GetProperty("compact").EnumerateArray())
        {
            var input = item.GetProperty("input").EnumerateArray().Select(value => value.ValueKind == JsonValueKind.Null ? null : (object?)value.GetInt32()).ToList();
            var expected = item.GetProperty("output").EnumerateArray().Select(value => (object?)value.GetInt32()).ToList();
            Assert.Equal(expected, CollectionUtils.Compact(input));
        }
        foreach (var item in root.GetProperty("collection").GetProperty("group_by").EnumerateArray())
        {
            var input = item.GetProperty("input").EnumerateArray().Select(JsonElementToDictionary).ToList();
            var grouped = CollectionUtils.GroupBy(input, entry => entry["type"]!.ToString()!);
            AssertDeepEqual(JsonElementToObject(item.GetProperty("output")), grouped);
        }
        foreach (var item in root.GetProperty("collection").GetProperty("sort_by").EnumerateArray())
        {
            var input = item.GetProperty("input").EnumerateArray().Select(JsonElementToDictionary).ToList();
            var sorted = CollectionUtils.SortBy(input, entry => entry["k"]!.ToString()!);
            AssertDeepEqual(JsonElementToObject(item.GetProperty("output")), sorted);
        }
        foreach (var item in root.GetProperty("collection").GetProperty("key_by").EnumerateArray())
        {
            var input = item.GetProperty("input").EnumerateArray().Select(JsonElementToDictionary).ToList();
            var keyed = CollectionUtils.KeyBy(input, entry => entry["id"]!.ToString()!);
            AssertDeepEqual(JsonElementToObject(item.GetProperty("output")), keyed);
        }
        foreach (var item in root.GetProperty("collection").GetProperty("filter").EnumerateArray())
        {
            var input = item.GetProperty("input").EnumerateArray().Select(value => value.GetInt32()).ToList();
            var threshold = item.GetProperty("threshold").GetInt32();
            var expected = item.GetProperty("output").EnumerateArray().Select(value => value.GetInt32()).ToList();
            Assert.Equal(expected, CollectionUtils.Filter(input, value => value > threshold));
        }
        foreach (var item in root.GetProperty("collection").GetProperty("find").EnumerateArray())
        {
            var input = item.GetProperty("input").EnumerateArray().Select(value => value.GetInt32()).ToList();
            var threshold = item.GetProperty("threshold").GetInt32();
            Assert.Equal(item.GetProperty("output").GetInt32(), CollectionUtils.Find(input, value => value > threshold));
        }

        foreach (var item in root.GetProperty("validation").GetProperty("is_uuid").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("output").GetBoolean(), ValidationUtils.IsUuid(item.GetProperty("input").GetString()!));
        }
        foreach (var item in root.GetProperty("validation").GetProperty("is_url").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("output").GetBoolean(), ValidationUtils.IsUrl(item.GetProperty("input").GetString()!));
        }
        foreach (var item in root.GetProperty("validation").GetProperty("is_numeric").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("output").GetBoolean(), ValidationUtils.IsNumeric(item.GetProperty("input").GetString()!));
        }

        foreach (var item in root.GetProperty("id").GetProperty("uuid").EnumerateArray())
        {
            Assert.Matches("^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$", IdUtils.Uuid());
            Assert.Equal("uuid-v4", item.GetProperty("pattern").GetString());
        }
        foreach (var item in root.GetProperty("id").GetProperty("random_string").EnumerateArray())
        {
            var value = IdUtils.RandomString(item.GetProperty("length").GetInt32());
            Assert.Equal(item.GetProperty("length").GetInt32(), value.Length);
            Assert.Matches("^[A-Za-z0-9]+$", value);
        }

        foreach (var item in root.GetProperty("encoding").GetProperty("base64_decode").EnumerateArray())
        {
            var decoded = EncodingUtils.Base64Decode(item.GetProperty("input").GetString()!);
            Assert.Equal(item.GetProperty("output").GetString(), System.Text.Encoding.UTF8.GetString(decoded!));
        }
        foreach (var item in root.GetProperty("encoding").GetProperty("hex_decode").EnumerateArray())
        {
            var decoded = EncodingUtils.HexDecode(item.GetProperty("input").GetString()!);
            Assert.Equal(item.GetProperty("output").GetString(), System.Text.Encoding.UTF8.GetString(decoded!));
        }
        foreach (var item in root.GetProperty("encoding").GetProperty("base64url_encode").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("output").GetString(), EncodingUtils.Base64UrlEncode(item.GetProperty("input").GetString()!));
        }
        foreach (var item in root.GetProperty("encoding").GetProperty("base64url_decode").EnumerateArray())
        {
            var decoded = EncodingUtils.Base64UrlDecode(item.GetProperty("input").GetString()!);
            Assert.Equal(item.GetProperty("output").GetString(), System.Text.Encoding.UTF8.GetString(decoded!));
        }

        foreach (var item in root.GetProperty("path").GetProperty("normalize_path").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("output").GetString(), PathUtils.NormalizePath(item.GetProperty("input").GetString()!));
        }

        foreach (var item in root.GetProperty("object").GetProperty("pick").EnumerateArray())
        {
            AssertDeepEqual(
                JsonElementToObject(item.GetProperty("output")),
                ObjectUtils.Pick(JsonElementToDictionary(item.GetProperty("source")), item.GetProperty("keys").EnumerateArray().Select(key => key.GetString()!).ToList()));
        }
        foreach (var item in root.GetProperty("object").GetProperty("omit").EnumerateArray())
        {
            AssertDeepEqual(
                JsonElementToObject(item.GetProperty("output")),
                ObjectUtils.Omit(JsonElementToDictionary(item.GetProperty("source")), item.GetProperty("keys").EnumerateArray().Select(key => key.GetString()!).ToList()));
        }
        foreach (var item in root.GetProperty("object").GetProperty("compact").EnumerateArray())
        {
            AssertDeepEqual(JsonElementToObject(item.GetProperty("output")), ObjectUtils.Compact(JsonElementToDictionary(item.GetProperty("input"))));
        }

        foreach (var item in root.GetProperty("optional").GetProperty("default_if_blank").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("output").GetString(), OptionalUtils.DefaultIfBlank(item.GetProperty("input").GetString()!, item.GetProperty("default").GetString()!));
        }

        foreach (var item in root.GetProperty("result").GetProperty("ok").EnumerateArray())
        {
            var result = ResultValue<int>.Success(item.GetProperty("value").GetInt32());
            Assert.Equal(item.GetProperty("is_ok").GetBoolean(), ResultUtils.IsOk(result));
            Assert.Equal(item.GetProperty("is_err").GetBoolean(), ResultUtils.IsErr(result));
        }
        foreach (var item in root.GetProperty("result").GetProperty("err").EnumerateArray())
        {
            var result = ResultValue<int>.Failure(item.GetProperty("message").GetString()!);
            Assert.Equal(item.GetProperty("is_ok").GetBoolean(), ResultUtils.IsOk(result));
            Assert.Equal(item.GetProperty("is_err").GetBoolean(), ResultUtils.IsErr(result));
        }
        foreach (var item in root.GetProperty("result").GetProperty("is_ok").EnumerateArray())
        {
            var result = item.GetProperty("kind").GetString() == "ok"
                ? ResultValue<int>.Success(item.GetProperty("value").GetInt32())
                : ResultValue<int>.Failure(item.GetProperty("message").GetString()!);
            Assert.Equal(item.GetProperty("output").GetBoolean(), ResultUtils.IsOk(result));
        }
        foreach (var item in root.GetProperty("result").GetProperty("is_err").EnumerateArray())
        {
            var result = item.GetProperty("kind").GetString() == "ok"
                ? ResultValue<int>.Success(item.GetProperty("value").GetInt32())
                : ResultValue<int>.Failure(item.GetProperty("message").GetString()!);
            Assert.Equal(item.GetProperty("output").GetBoolean(), ResultUtils.IsErr(result));
        }
        foreach (var item in root.GetProperty("result").GetProperty("map").EnumerateArray())
        {
            var mapped = ResultUtils.Map(ResultValue<int>.Success(item.GetProperty("value").GetInt32()), value => value * 2);
            Assert.Equal(item.GetProperty("output").GetInt32(), mapped.Value);
        }

        foreach (var item in root.GetProperty("bytes").GetProperty("format_bytes").EnumerateArray())
        {
            Assert.Equal(item.GetProperty("output").GetString(), BytesUtils.FormatBytes(item.GetProperty("value").GetInt64(), item.GetProperty("decimals").GetInt32()));
        }

        foreach (var item in root.GetProperty("bloom").GetProperty("create").EnumerateArray())
        {
            var filter = BloomUtils.Create(item.GetProperty("expected_items").GetInt32(), item.GetProperty("false_positive_rate").GetDouble());
            Assert.Equal(item.GetProperty("bit_count").GetInt32(), filter.BitCount);
            Assert.Equal(item.GetProperty("hash_count").GetInt32(), filter.HashCount);
        }

        foreach (var item in root.GetProperty("bloom").GetProperty("estimate_bit_count").EnumerateArray())
        {
            Assert.Equal(
                item.GetProperty("output").GetInt32(),
                BloomUtils.EstimateBitCount(item.GetProperty("expected_items").GetInt32(), item.GetProperty("false_positive_rate").GetDouble()));
        }

        foreach (var item in root.GetProperty("bloom").GetProperty("estimate_hash_count").EnumerateArray())
        {
            Assert.Equal(
                item.GetProperty("output").GetInt32(),
                BloomUtils.EstimateHashCount(item.GetProperty("expected_items").GetInt32(), item.GetProperty("bit_count").GetInt32()));
        }

        foreach (var item in root.GetProperty("bloom").GetProperty("might_contain").EnumerateArray())
        {
            var filter = BloomUtils.Create(128, 0.01);
            foreach (var value in item.GetProperty("added").EnumerateArray())
            {
                BloomUtils.Add(filter, value.GetString()!);
            }

            Assert.Equal(item.GetProperty("present_output").GetBoolean(), BloomUtils.MightContain(filter, item.GetProperty("present").GetString()!));
            Assert.Equal(item.GetProperty("absent_output").GetBoolean(), BloomUtils.MightContain(filter, item.GetProperty("absent").GetString()!));
        }
    }

    private static void AssertDeepEqual(object? expected, object? actual)
    {
        if (expected is Dictionary<string, object?> expectedMap && actual is Dictionary<string, object?> actualMap)
        {
            Assert.Equal(expectedMap.Keys.OrderBy(key => key), actualMap.Keys.OrderBy(key => key));
            foreach (var key in expectedMap.Keys)
            {
                AssertDeepEqual(expectedMap[key], actualMap[key]);
            }
            return;
        }

        if (expected is List<object?> expectedList && actual is List<object?> actualList)
        {
            Assert.Equal(expectedList.Count, actualList.Count);
            for (var index = 0; index < expectedList.Count; index++)
            {
                AssertDeepEqual(expectedList[index], actualList[index]);
            }
            return;
        }

        Assert.Equal(expected, actual);
    }

    private static Dictionary<string, object?> JsonElementToDictionary(JsonElement element)
    {
        var result = new Dictionary<string, object?>();
        foreach (var property in element.EnumerateObject())
        {
            result[property.Name] = JsonElementToObject(property.Value);
        }
        return result;
    }

    private static object? JsonElementToObject(JsonElement element) =>
        element.ValueKind switch
        {
            JsonValueKind.Object => JsonElementToDictionary(element),
            JsonValueKind.Array => element.EnumerateArray().Select(JsonElementToObject).ToList(),
            JsonValueKind.String => element.GetString(),
            JsonValueKind.Number when element.TryGetInt64(out var integer) => integer,
            JsonValueKind.Number => element.GetDouble(),
            JsonValueKind.True => true,
            JsonValueKind.False => false,
            JsonValueKind.Null => null,
            _ => element.GetRawText(),
        };
}
