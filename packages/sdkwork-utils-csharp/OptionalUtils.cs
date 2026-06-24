namespace Sdkwork.Utils;

public static class OptionalUtils
{
    public static string? Coalesce(params string?[] values)
    {
        foreach (var value in values)
        {
            if (!StringUtils.IsBlank(value)) return value!.Trim();
        }
        return null;
    }

    public static string DefaultIfBlank(string? value, string defaultValue) =>
        StringUtils.IsBlank(value) ? defaultValue : value!.Trim();
}
