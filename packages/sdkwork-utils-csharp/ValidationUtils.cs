using System.Text.RegularExpressions;

namespace Sdkwork.Utils;

public static partial class ValidationUtils
{
    public static bool IsEmail(string value) => Email().IsMatch(value.Trim());
    public static bool IsUuid(string value) => Uuid().IsMatch(value.Trim());
    public static bool IsUrl(string value) => Url().IsMatch(value.Trim());
    public static bool IsNumeric(string value) => NumberUtils.ParseNumber(value) is not null;

    public static bool IsIpv4(string value) => Ipv4().IsMatch(value.Trim());
    public static bool IsIpv6(string value) => IsIpv6Shape(value.Trim());
    public static bool IsPhoneE164(string value) => E164().IsMatch(value.Trim());

    private static bool IsIpv6Shape(string value)
    {
        if (value.Length == 0 || !Ipv6Chars().IsMatch(value))
        {
            return false;
        }

        if (value.Split("::", StringSplitOptions.None).Length > 2)
        {
            return false;
        }

        if (value.Contains("::"))
        {
            var parts = value.Split("::", 2);
            var leftParts = parts[0].Length == 0 ? Array.Empty<string>() : parts[0].Split(':');
            var rightParts = parts[1].Length == 0 ? Array.Empty<string>() : parts[1].Split(':');
            var segmentCount = 0;
            foreach (var part in leftParts.Concat(rightParts))
            {
                if (part.Length == 0)
                {
                    continue;
                }

                if (!IsIpv6Part(part))
                {
                    return false;
                }

                segmentCount++;
            }

            return segmentCount < 8;
        }

        var segments = value.Split(':');
        return segments.Length == 8 && segments.All(IsIpv6Part);
    }

    private static bool IsIpv6Part(string part) => part.Length > 0 && part.Length <= 4;

    [GeneratedRegex("^[0-9a-fA-F:]+$")]
    private static partial Regex Ipv6Chars();

    [GeneratedRegex("^\\+[1-9]\\d{1,14}$")]
    private static partial Regex E164();

    [GeneratedRegex("^(25[0-5]|2[0-4]\\d|1?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|1?\\d?\\d)){3}$")]
    private static partial Regex Ipv4();

    [GeneratedRegex("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$")]
    private static partial Regex Email();

    [GeneratedRegex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")]
    private static partial Regex Uuid();

    [GeneratedRegex("^https?://[^\\s/$.?#].[^\\s]*$")]
    private static partial Regex Url();
}
