using System.Text;
using System.Text.RegularExpressions;

namespace Sdkwork.Utils;

public static partial class StringUtils
{
    public static bool IsBlank(string? value) => string.IsNullOrWhiteSpace(value);

    public static string Trim(string? value) => value?.Trim() ?? string.Empty;

    public static string Truncate(string value, int maxLen, string suffix = "...")
    {
        if (maxLen <= 0) return string.Empty;
        var runes = value.EnumerateRunes().ToArray();
        if (runes.Length <= maxLen) return value;
        var suffixRunes = suffix.EnumerateRunes().ToArray();
        if (suffixRunes.Length >= maxLen)
            return RunesToString(suffixRunes.AsSpan()[..maxLen]);
        var keep = maxLen - suffixRunes.Length;
        return RunesToString(runes.AsSpan()[..keep]) + suffix;
    }

    public static string Capitalize(string value)
    {
        if (string.IsNullOrEmpty(value)) return string.Empty;
        return char.ToUpperInvariant(value[0]) + value[1..].ToLowerInvariant();
    }

    public static string CamelCase(string value)
    {
        var parts = CamelParts(value).ToList();
        if (parts.Count == 0) return string.Empty;
        return parts[0] + string.Concat(parts.Skip(1).Select(Capitalize));
    }

    public static string SnakeCase(string value) => string.Join('_', CamelParts(value));
    public static string KebabCase(string value) => string.Join('-', CamelParts(value));

    public static string Slugify(string value) =>
        Regex.Replace(KebabCase(value), "[^a-z0-9-]", string.Empty).Trim('-');

    public static string Mask(string value, int visibleStart, int visibleEnd, char maskChar = '*')
    {
        var runes = value.EnumerateRunes().ToArray();
        if (visibleStart + visibleEnd >= runes.Length) return value;
        return RunesToString(runes.AsSpan()[..visibleStart])
            + new string(maskChar, runes.Length - visibleStart - visibleEnd)
            + RunesToString(runes.AsSpan()[^visibleEnd..]);
    }

    public static string PadStart(string value, int targetLen, char padChar = ' ')
    {
        var current = value.EnumerateRunes().Count();
        if (current >= targetLen) return value;
        return new string(padChar, targetLen - current) + value;
    }

    public static string PadEnd(string value, int targetLen, char padChar = ' ')
    {
        var current = value.EnumerateRunes().Count();
        if (current >= targetLen) return value;
        return value + new string(padChar, targetLen - current);
    }

    public static bool StartsWith(string value, string prefix) => value.StartsWith(prefix);

    public static bool EndsWith(string value, string suffix) => value.EndsWith(suffix);

    public static bool Contains(string value, string substring) => value.Contains(substring);

    public static string ReplaceAll(string value, string search, string replacement) =>
        value.Replace(search, replacement);

    public static List<string> Split(string value, string delimiter, bool trimParts = true)
    {
        var parts = value.Split(delimiter);
        var result = new List<string>(parts.Length);
        foreach (var part in parts)
        {
            var text = trimParts ? part.Trim() : part;
            if (!trimParts || text.Length > 0) result.Add(text);
        }
        return result;
    }

    public static string Join(IEnumerable<string> parts, string separator) => string.Join(separator, parts);

    public static string Repeat(string value, int count)
    {
        if (count < 0)
        {
            throw new ArgumentOutOfRangeException(nameof(count), "repeat count must be >= 0");
        }

        return string.Concat(Enumerable.Repeat(value, count));
    }

    public static string NormalizeWhitespace(string value) =>
        string.Join(' ', value.Trim().Split((char[]?)null, StringSplitOptions.RemoveEmptyEntries));

    private static readonly System.Text.RegularExpressions.Regex TemplateKey =
        new(@"\{([a-zA-Z_][a-zA-Z0-9_]*)\}", System.Text.RegularExpressions.RegexOptions.Compiled);

    public static string Template(string pattern, IReadOnlyDictionary<string, string> values) =>
        TemplateKey.Replace(pattern, match =>
        {
            var key = match.Groups[1].Value;
            return values.TryGetValue(key, out var replacement) ? replacement : match.Value;
        });

    private static IEnumerable<string> CamelParts(string value)
    {
        var normalized = CamelBoundary().Replace(value.Trim(), "$1$3 $2$4");
        return WordSplit().Split(normalized).Where(part => part.Length > 0).Select(part => part.ToLowerInvariant());
    }

    [GeneratedRegex("[^a-zA-Z0-9]+")]
    private static partial Regex WordSplit();

    [GeneratedRegex("([a-z0-9])([A-Z])|([A-Z]+)([A-Z][a-z])")]
    private static partial Regex CamelBoundary();

    private static string RunesToString(ReadOnlySpan<Rune> runes)
    {
        var builder = new StringBuilder();
        Span<char> buffer = stackalloc char[2];
        foreach (var rune in runes)
        {
            var length = rune.EncodeToUtf16(buffer);
            builder.Append(buffer[..length]);
        }
        return builder.ToString();
    }
}
