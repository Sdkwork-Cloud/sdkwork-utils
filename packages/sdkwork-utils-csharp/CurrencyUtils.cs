namespace Sdkwork.Utils;

public static class CurrencyUtils
{
    private sealed record CurrencyMeta(int Exponent, string Symbol);

    private static readonly Dictionary<string, CurrencyMeta> Known = new(StringComparer.OrdinalIgnoreCase)
    {
        ["USD"] = new(2, "$"),
        ["EUR"] = new(2, "€"),
        ["GBP"] = new(2, "£"),
        ["CNY"] = new(2, "¥"),
        ["JPY"] = new(0, "¥"),
        ["KRW"] = new(0, "₩"),
        ["HKD"] = new(2, "HK$"),
        ["TWD"] = new(2, "NT$"),
        ["CHF"] = new(2, "CHF"),
        ["CAD"] = new(2, "CA$"),
        ["AUD"] = new(2, "A$"),
        ["INR"] = new(2, "₹"),
        ["BHD"] = new(3, "BHD"),
        ["KWD"] = new(3, "KWD"),
    };

    private static CurrencyMeta? Lookup(string code)
    {
        var normalized = code.Trim();
        if (normalized.Length != 3 || normalized != normalized.ToUpperInvariant()
            || !normalized.All(char.IsLetter))
        {
            return null;
        }
        return Known.TryGetValue(normalized, out var meta) ? meta : null;
    }

    public static bool IsCurrencyCode(string value) => Lookup(value) != null;

    public static int? MinorUnitExponent(string code) => Lookup(code)?.Exponent;

    public static long? ToMinorUnits(double amount, string code)
    {
        var meta = Lookup(code);
        if (meta == null)
        {
            return null;
        }
        var factor = Math.Pow(10, meta.Exponent);
        return (long)NumberUtils.Round(amount * factor, 0);
    }

    public static double? FromMinorUnits(long minor, string code)
    {
        var meta = Lookup(code);
        if (meta == null)
        {
            return null;
        }
        var factor = Math.Pow(10, meta.Exponent);
        return minor / factor;
    }

    private static bool SuffixLocale(string locale)
    {
        var normalized = locale.ToLowerInvariant();
        return normalized.StartsWith("de") || normalized.StartsWith("fr") ||
               normalized.StartsWith("it") || normalized.StartsWith("es");
    }

    public static string? FormatCurrency(double amount, string code, string locale)
    {
        var meta = Lookup(code);
        if (meta == null)
        {
            return null;
        }
        var formatted = I18nUtils.FormatNumberLocale(amount, locale, meta.Exponent);
        return SuffixLocale(locale) ? $"{formatted} {meta.Symbol}" : $"{meta.Symbol}{formatted}";
    }
}
