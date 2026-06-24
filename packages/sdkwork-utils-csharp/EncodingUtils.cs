namespace Sdkwork.Utils;

public static class EncodingUtils
{
    public static string Base64Encode(byte[] value) => Convert.ToBase64String(value);
    public static string Base64Encode(string value) => Base64Encode(System.Text.Encoding.UTF8.GetBytes(value));

    public static byte[]? Base64Decode(string value)
    {
        try
        {
            return Convert.FromBase64String(value.Trim());
        }
        catch (FormatException)
        {
            return null;
        }
    }

    public static string HexEncode(byte[] value) => Convert.ToHexString(value).ToLowerInvariant();

    public static byte[]? HexDecode(string value)
    {
        var trimmed = value.Trim();
        if (trimmed.Length % 2 != 0) return null;
        try
        {
            return Convert.FromHexString(trimmed);
        }
        catch (FormatException)
        {
            return null;
        }
    }

    public static string UrlEncode(string value) => Uri.EscapeDataString(value);

    public static string? UrlDecode(string value)
    {
        try
        {
            return Uri.UnescapeDataString(value);
        }
        catch (UriFormatException)
        {
            return null;
        }
    }

    public static string Base64UrlEncode(byte[] value) =>
        Convert.ToBase64String(value).TrimEnd('=').Replace('+', '-').Replace('/', '_');

    public static string Base64UrlEncode(string value) => Base64UrlEncode(System.Text.Encoding.UTF8.GetBytes(value));

    public static byte[]? Base64UrlDecode(string value)
    {
        var trimmed = value.Trim();
        if (trimmed.Length == 0)
        {
            return null;
        }

        var padded = trimmed.PadRight(trimmed.Length + (4 - trimmed.Length % 4) % 4, '=')
            .Replace('-', '+')
            .Replace('_', '/');
        try
        {
            return Convert.FromBase64String(padded);
        }
        catch (FormatException)
        {
            return null;
        }
    }
}
