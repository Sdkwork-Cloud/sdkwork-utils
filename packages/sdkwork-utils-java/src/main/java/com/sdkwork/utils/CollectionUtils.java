package com.sdkwork.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public final class CollectionUtils {
    private CollectionUtils() {
    }

    public static <T> List<T> unique(List<T> items) {
        Set<T> seen = new LinkedHashSet<>();
        List<T> result = new ArrayList<>();
        for (T item : items) {
            if (seen.add(item)) {
                result.add(item);
            }
        }
        return result;
    }

    public static <T> List<List<T>> chunk(List<T> items, int size) {
        if (size <= 0) {
            return List.of();
        }
        List<List<T>> result = new ArrayList<>();
        for (int index = 0; index < items.size(); index += size) {
            result.add(items.subList(index, Math.min(index + size, items.size())));
        }
        return result;
    }

    public static <T, K> Map<K, List<T>> groupBy(List<T> items, Function<T, K> keyFn) {
        Map<K, List<T>> groups = new LinkedHashMap<>();
        for (T item : items) {
            groups.computeIfAbsent(keyFn.apply(item), key -> new ArrayList<>()).add(item);
        }
        return groups;
    }

    public static <T> List<T> flatten(List<List<T>> items) {
        List<T> result = new ArrayList<>();
        for (List<T> group : items) {
            result.addAll(group);
        }
        return result;
    }

    public static <T> java.util.List<T> compact(java.util.List<T> items) {
        java.util.List<T> result = new java.util.ArrayList<>();
        for (T item : items) {
            if (item != null) {
                result.add(item);
            }
        }
        return result;
    }

    public static <T> T first(List<T> items) {
        return items.isEmpty() ? null : items.get(0);
    }

    public static <T> T last(List<T> items) {
        return items.isEmpty() ? null : items.get(items.size() - 1);
    }

    public static <T, K extends Comparable<? super K>> List<T> sortBy(List<T> items, Function<T, K> keyFn) {
        List<T> sorted = new ArrayList<>(items);
        sorted.sort(Comparator.comparing(keyFn));
        return sorted;
    }

    public static <T, K> Map<K, T> keyBy(List<T> items, Function<T, K> keyFn) {
        Map<K, T> result = new LinkedHashMap<>();
        for (T item : items) {
            result.put(keyFn.apply(item), item);
        }
        return result;
    }

    public static <T> List<T> filter(List<T> items, Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (T item : items) {
            if (predicate.test(item)) {
                result.add(item);
            }
        }
        return result;
    }

    public static <T> T find(List<T> items, Predicate<T> predicate) {
        for (T item : items) {
            if (predicate.test(item)) {
                return item;
            }
        }
        return null;
    }
}
