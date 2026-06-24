namespace Sdkwork.Utils;

public static class ObjectUtils
{
    public static Dictionary<string, object?> Pick(Dictionary<string, object?> source, IEnumerable<string> keys)
    {
        var result = new Dictionary<string, object?>();
        foreach (var key in keys)
        {
            if (source.TryGetValue(key, out var value)) result[key] = value;
        }
        return result;
    }

    public static Dictionary<string, object?> Omit(Dictionary<string, object?> source, IEnumerable<string> keys)
    {
        var blocked = keys.ToHashSet();
        return source.Where(entry => !blocked.Contains(entry.Key)).ToDictionary(entry => entry.Key, entry => entry.Value);
    }

    public static object? GetPath(object? source, string path)
    {
        object? current = source;
        foreach (var segment in SplitPath(path))
        {
            if (current is not Dictionary<string, object?> map || !map.TryGetValue(segment, out current)) return null;
        }
        return current;
    }

    public static bool HasPath(object? source, string path)
    {
        var value = GetPath(source, path);
        return value is not null;
    }

    public static Dictionary<string, object?> ShallowMerge(
        Dictionary<string, object?> baseValue,
        Dictionary<string, object?> overlay)
    {
        var result = new Dictionary<string, object?>(baseValue);
        foreach (var (key, value) in overlay)
        {
            result[key] = value;
        }
        return result;
    }

    public static Dictionary<string, object?> SetPath(Dictionary<string, object?> source, string path, object? value)
    {
        var segments = SplitPath(path);
        if (segments.Count == 0) return source;

        var root = new Dictionary<string, object?>(source);
        var current = root;
        for (var index = 0; index < segments.Count - 1; index++)
        {
            var segment = segments[index];
            if (current.TryGetValue(segment, out var next) && next is Dictionary<string, object?> nextMap)
            {
                nextMap = new Dictionary<string, object?>(nextMap);
                current[segment] = nextMap;
                current = nextMap;
            }
            else
            {
                var created = new Dictionary<string, object?>();
                current[segment] = created;
                current = created;
            }
        }
        current[segments[^1]] = value;
        return root;
    }

    public static object? DeepMerge(object? baseValue, object? overlay)
    {
        if (baseValue is Dictionary<string, object?> baseMap && overlay is Dictionary<string, object?> overlayMap)
        {
            var result = new Dictionary<string, object?>(baseMap);
            foreach (var (key, overlayValue) in overlayMap)
            {
                result[key] = result.TryGetValue(key, out var existing) ? DeepMerge(existing, overlayValue) : overlayValue;
            }
            return result;
        }
        return overlay;
    }

    public static Dictionary<string, object?> Compact(Dictionary<string, object?> source) =>
        source.Where(entry => entry.Value is not null).ToDictionary(entry => entry.Key, entry => entry.Value);

    public static List<string> Keys(Dictionary<string, object?> source) => source.Keys.ToList();

    public static List<object?> Values(Dictionary<string, object?> source) => source.Values.ToList();

    private static List<string> SplitPath(string path) =>
        path.Split('.', StringSplitOptions.RemoveEmptyEntries).ToList();
}
