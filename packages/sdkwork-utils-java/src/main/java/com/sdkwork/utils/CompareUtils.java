package com.sdkwork.utils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class CompareUtils {
    private CompareUtils() {
    }

    public static boolean deepEqual(Object left, Object right) {
        if (left == right) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }
        if (left instanceof Map<?, ?> leftMap && right instanceof Map<?, ?> rightMap) {
            if (leftMap.size() != rightMap.size()) {
                return false;
            }
            for (Map.Entry<?, ?> entry : leftMap.entrySet()) {
                Object key = entry.getKey();
                if (!rightMap.containsKey(key)) {
                    return false;
                }
                if (!deepEqual(entry.getValue(), rightMap.get(key))) {
                    return false;
                }
            }
            return true;
        }
        if (left instanceof List<?> leftList && right instanceof List<?> rightList) {
            if (leftList.size() != rightList.size()) {
                return false;
            }
            for (int index = 0; index < leftList.size(); index++) {
                if (!deepEqual(leftList.get(index), rightList.get(index))) {
                    return false;
                }
            }
            return true;
        }
        return Objects.equals(left, right);
    }

    @SuppressWarnings("unchecked")
    public static Object deepClone(Object value) {
        if (value instanceof Map<?, ?> map) {
            Map<Object, Object> cloned = new java.util.LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                cloned.put(entry.getKey(), deepClone(entry.getValue()));
            }
            return cloned;
        }
        if (value instanceof List<?> list) {
            List<Object> cloned = new java.util.ArrayList<>(list.size());
            for (Object item : list) {
                cloned.add(deepClone(item));
            }
            return cloned;
        }
        return value;
    }
}
