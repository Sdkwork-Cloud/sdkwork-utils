using System.Globalization;

namespace Sdkwork.Utils;

public static class I18nUtils
{
    public static string FormatNumberLocale(double value, string locale, int decimals = 0)
    {
        var culture = locale.Equals("de-DE", StringComparison.OrdinalIgnoreCase)
            ? new CultureInfo("de-DE")
            : new CultureInfo("en-US");
        return value.ToString($"N{decimals}", culture);
    }

    public static string FormatDatetimeLocale(DateTimeOffset value, string locale)
    {
        var normalized = locale.ToLowerInvariant();
        var format = normalized.StartsWith("de") ? "dd.MM.yyyy HH:mm"
            : normalized.StartsWith("zh") ? "yyyy-MM-dd HH:mm"
            : "MM/dd/yyyy HH:mm";
        return value.ToUniversalTime().ToString(format, CultureInfo.InvariantCulture);
    }

    public static string? FormatDatetimeLocaleStr(string value, string locale)
    {
        var parsed = DateTimeUtils.ParseDatetime(value);
        return parsed is null ? null : FormatDatetimeLocale(parsed.Value, locale);
    }

    public static double? ParseNumberLocale(string input, string locale)
    {
        var trimmed = input.Trim();
        if (trimmed.Length == 0)
        {
            return null;
        }

        var decimalSeparator = locale.Equals("de-DE", StringComparison.OrdinalIgnoreCase) ? "," : ".";
        var groupingSeparator = locale.Equals("de-DE", StringComparison.OrdinalIgnoreCase) ? "." : ",";
        var normalized = trimmed.Replace(groupingSeparator, "").Replace(decimalSeparator, ".");
        return NumberUtils.ParseNumber(normalized);
    }
}
