namespace Sdkwork.Utils;

public static class BytesUtils
{
    private static readonly string[] Units = ["B", "KB", "MB", "GB", "TB", "PB"];

    public static string FormatBytes(long bytes, int decimals = 1)
    {
        var normalized = Math.Max(0L, bytes);
        if (normalized < 1024L)
        {
            return $"{normalized} B";
        }

        var size = (double)normalized;
        var unitIndex = 0;
        while (size >= 1024.0 && unitIndex < Units.Length - 1)
        {
            size /= 1024.0;
            unitIndex++;
        }

        return $"{size.ToString($"F{decimals}", System.Globalization.CultureInfo.InvariantCulture)} {Units[unitIndex]}";
    }
}
