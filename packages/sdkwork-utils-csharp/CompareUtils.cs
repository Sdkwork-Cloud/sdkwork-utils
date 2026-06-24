namespace Sdkwork.Utils;

public static class CompareUtils
{
    public static bool DeepEqual(object? left, object? right)
    {
        if (ReferenceEquals(left, right))
        {
            return true;
        }

        if (left is null || right is null)
        {
            return false;
        }

        if (left is IDictionary<string, object?> leftMap && right is IDictionary<string, object?> rightMap)
        {
            if (leftMap.Count != rightMap.Count)
            {
                return false;
            }

            foreach (var entry in leftMap)
            {
                if (!rightMap.TryGetValue(entry.Key, out var other) || !DeepEqual(entry.Value, other))
                {
                    return false;
                }
            }

            return true;
        }

        if (left is IList<object?> leftList && right is IList<object?> rightList)
        {
            if (leftList.Count != rightList.Count)
            {
                return false;
            }

            for (var index = 0; index < leftList.Count; index++)
            {
                if (!DeepEqual(leftList[index], rightList[index]))
                {
                    return false;
                }
            }

            return true;
        }

        return Equals(left, right);
    }

    public static object? DeepClone(object? value)
    {
        return value switch
        {
            IDictionary<string, object?> map => map.ToDictionary(entry => entry.Key, entry => DeepClone(entry.Value)),
            IList<object?> list => list.Select(DeepClone).ToList(),
            _ => value
        };
    }
}
