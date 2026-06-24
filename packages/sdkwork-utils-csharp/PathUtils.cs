namespace Sdkwork.Utils;

public static class PathUtils
{
    public static string JoinPath(params string[] segments) =>
        string.Join('/', segments.Select(segment => segment.Trim('/')).Where(segment => segment.Length > 0));

    public static string NormalizePath(string value)
    {
        var joined = string.Join('/', value.Split('/', StringSplitOptions.RemoveEmptyEntries));
        return value.StartsWith('/') ? $"/{joined}" : joined;
    }
}
