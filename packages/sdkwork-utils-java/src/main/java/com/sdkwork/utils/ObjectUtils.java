package com.sdkwork.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ObjectUtils {
    private ObjectUtils() {
    }

    public static Map<String, Object> pick(Map<String, Object> source, List<String> keys) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (String key : keys) {
            if (source.containsKey(key)) {
                result.put(key, source.get(key));
            }
        }
        return result;
    }

    public static Map<String, Object> omit(Map<String, Object> source, List<String> keys) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            if (!keys.contains(entry.getKey())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static Object getPath(Object source, String path) {
        Object current = source;
        for (String segment : splitPath(path)) {
            if (!(current instanceof Map<?, ?> map) || !map.containsKey(segment)) {
                return null;
            }
            current = map.get(segment);
        }
        return current;
    }

    public static boolean hasPath(Object source, String path) {
        Object value = getPath(source, path);
        return value != null;
    }

    @SuppressWarnings("unchecked")
    public static Object shallowMerge(Object base, Object overlay) {
        if (base instanceof Map<?, ?> baseMap && overlay instanceof Map<?, ?> overlayMap) {
            Map<String, Object> result = new LinkedHashMap<>((Map<String, Object>) baseMap);
            for (Map.Entry<?, ?> entry : overlayMap.entrySet()) {
                String key = Objects.toString(entry.getKey(), null);
                if (key != null) {
                    result.put(key, entry.getValue());
                }
            }
            return result;
        }
        return overlay;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> setPath(Map<String, Object> source, String path, Object value) {
        List<String> segments = splitPath(path);
        if (segments.isEmpty()) {
            return source;
        }

        Map<String, Object> root = new LinkedHashMap<>(source);
        Map<String, Object> current = root;
        for (int index = 0; index < segments.size() - 1; index++) {
            String segment = segments.get(index);
            Object next = current.get(segment);
            Map<String, Object> nextMap;
            if (next instanceof Map<?, ?> existing) {
                nextMap = new LinkedHashMap<>((Map<String, Object>) existing);
            } else {
                nextMap = new LinkedHashMap<>();
            }
            current.put(segment, nextMap);
            current = nextMap;
        }
        current.put(segments.get(segments.size() - 1), value);
        return root;
    }

    @SuppressWarnings("unchecked")
    public static Object deepMerge(Object base, Object overlay) {
        if (base instanceof Map<?, ?> baseMap && overlay instanceof Map<?, ?> overlayMap) {
            Map<String, Object> result = new LinkedHashMap<>((Map<String, Object>) baseMap);
            for (Map.Entry<?, ?> entry : overlayMap.entrySet()) {
                String key = Objects.toString(entry.getKey(), null);
                if (key == null) {
                    continue;
                }
                result.put(
                        key,
                        result.containsKey(key)
                                ? deepMerge(result.get(key), entry.getValue())
                                : entry.getValue());
            }
            return result;
        }
        return overlay;
    }

    public static Map<String, Object> compact(Map<String, Object> source) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            if (entry.getValue() != null) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    public static List<String> keys(Map<String, Object> source) {
        return new ArrayList<>(source.keySet());
    }

    public static List<Object> values(Map<String, Object> source) {
        return new ArrayList<>(source.values());
    }

    private static List<String> splitPath(String path) {
        return java.util.Arrays.stream(path.split("\\."))
                .filter(part -> !part.isEmpty())
                .toList();
    }
}
