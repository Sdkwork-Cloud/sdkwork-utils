namespace Sdkwork.Utils;

public static class NumberUtils
{
    public static double Clamp(double value, double min, double max) => Math.Min(max, Math.Max(min, value));

    public static double Round(double value, int decimals) =>
        Math.Round(value, decimals, MidpointRounding.AwayFromZero);

    public static string FormatNumber(double value, int decimals = 0) => value.ToString($"F{decimals}");

    public static double? ParseNumber(string value) =>
        double.TryParse(value.Trim(), out var parsed) ? parsed : null;

    public static bool IsInteger(double value) => double.IsFinite(value) && value == Math.Truncate(value);

    public static long? ParseInt(string value) =>
        long.TryParse(value.Trim(), out var parsed) ? parsed : null;

    public static string PercentFormat(double value, int decimals = 0) =>
        $"{FormatNumber(value * 100.0, decimals)}%";

    public static bool InRange(double value, double min, double max) => value >= min && value <= max;

    public static double Abs(double value) => Math.Abs(value);
}
