namespace Sdkwork.Utils;

public static class CollectionUtils
{
    public static List<T> Unique<T>(IEnumerable<T> items)
    {
        var seen = new HashSet<T>();
        var result = new List<T>();
        foreach (var item in items)
        {
            if (seen.Add(item)) result.Add(item);
        }
        return result;
    }

    public static List<List<T>> Chunk<T>(IReadOnlyList<T> items, int size)
    {
        if (size <= 0) return [];
        var result = new List<List<T>>();
        for (var index = 0; index < items.Count; index += size)
        {
            result.Add(items.Skip(index).Take(size).ToList());
        }
        return result;
    }

    public static Dictionary<TKey, List<T>> GroupBy<T, TKey>(
        IEnumerable<T> items,
        Func<T, TKey> keyFn) where TKey : notnull
    {
        var groups = new Dictionary<TKey, List<T>>();
        foreach (var item in items)
        {
            var key = keyFn(item);
            if (!groups.TryGetValue(key, out var group))
            {
                group = [];
                groups[key] = group;
            }
            group.Add(item);
        }
        return groups;
    }

    public static List<T> Flatten<T>(IEnumerable<IEnumerable<T>> items) => items.SelectMany(item => item).ToList();

    public static List<T> Compact<T>(IEnumerable<T?> items) where T : class =>
        items.Where(item => item is not null).Cast<T>().ToList();

    public static object? First<T>(IReadOnlyList<T> items) =>
        items.Count > 0 ? items[0] : null;

    public static object? Last<T>(IReadOnlyList<T> items) =>
        items.Count > 0 ? items[^1] : null;

    public static List<T> SortBy<T, TKey>(IEnumerable<T> items, Func<T, TKey> keyFn)
        where TKey : IComparable<TKey> =>
        items.OrderBy(keyFn).ToList();

    public static Dictionary<TKey, T> KeyBy<T, TKey>(IEnumerable<T> items, Func<T, TKey> keyFn)
        where TKey : notnull
    {
        var result = new Dictionary<TKey, T>();
        foreach (var item in items)
        {
            result[keyFn(item)] = item;
        }

        return result;
    }

    public static List<T> Filter<T>(IEnumerable<T> items, Func<T, bool> predicate) =>
        items.Where(predicate).ToList();

    public static T? Find<T>(IEnumerable<T> items, Func<T, bool> predicate) =>
        items.FirstOrDefault(predicate);
}
