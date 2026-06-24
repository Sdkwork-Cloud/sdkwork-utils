using System.Globalization;

namespace Sdkwork.Utils;

public static class DateTimeUtils
{
    public const string DefaultPattern = "iso8601";

    public static DateTimeOffset Now() => DateTimeOffset.UtcNow;

    public static string FormatDatetime(DateTimeOffset value, string? pattern = DefaultPattern) =>
        pattern == DefaultPattern
            ? value.ToUniversalTime().ToString("yyyy-MM-dd'T'HH:mm:ss.fff'Z'", CultureInfo.InvariantCulture)
            : throw new ArgumentException("Unsupported pattern");

    public static DateTimeOffset? ParseDatetime(string value, string? pattern = DefaultPattern) =>
        pattern == DefaultPattern
        && DateTimeOffset.TryParse(value, CultureInfo.InvariantCulture, DateTimeStyles.AssumeUniversal, out var parsed)
            ? parsed.ToUniversalTime()
            : null;

    public static DateTimeOffset AddDays(DateTimeOffset value, int days) => value.AddDays(days);
    public static DateTimeOffset AddHours(DateTimeOffset value, int hours) => value.AddHours(hours);
    public static DateTimeOffset AddMinutes(DateTimeOffset value, int minutes) => value.AddMinutes(minutes);

    public static long DiffMillis(DateTimeOffset earlier, DateTimeOffset later) =>
        (long)(later - earlier).TotalMilliseconds;

    public static bool IsBefore(DateTimeOffset first, DateTimeOffset second) => first < second;
    public static bool IsAfter(DateTimeOffset first, DateTimeOffset second) => first > second;

    public static DateTimeOffset StartOfDayUtc(DateTimeOffset value)
    {
        var utc = value.ToUniversalTime();
        return new DateTimeOffset(utc.Year, utc.Month, utc.Day, 0, 0, 0, TimeSpan.Zero);
    }

    public static DateTimeOffset EndOfDayUtc(DateTimeOffset value) =>
        StartOfDayUtc(value).AddDays(1).AddMilliseconds(-1);

    public static long ToUnixMillis(DateTimeOffset value) => value.ToUniversalTime().ToUnixTimeMilliseconds();

    public static DateTimeOffset? FromUnixMillis(long value)
    {
        try
        {
            return DateTimeOffset.FromUnixTimeMilliseconds(value).ToUniversalTime();
        }
        catch (ArgumentOutOfRangeException)
        {
            return null;
        }
    }

    public static bool IsSameInstant(DateTimeOffset first, DateTimeOffset second) =>
        first.ToUniversalTime().ToUnixTimeMilliseconds() == second.ToUniversalTime().ToUnixTimeMilliseconds();
}
