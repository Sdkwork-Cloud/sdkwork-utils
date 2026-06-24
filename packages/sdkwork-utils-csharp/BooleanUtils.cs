namespace Sdkwork.Utils;

public static class BooleanUtils
{
    public static bool? ParseBool(string value)
    {
        return value.Trim().ToLowerInvariant() switch
        {
            "true" or "1" or "yes" or "on" => true,
            "false" or "0" or "no" or "off" => false,
            _ => null,
        };
    }

    public static bool IsTruthy(string? value)
    {
        if (string.IsNullOrWhiteSpace(value)) return false;
        return value.Trim().ToLowerInvariant() switch
        {
            "false" or "0" or "no" or "off" => false,
            _ => true,
        };
    }
}
