package com.sdkwork.utils

object CollectionUtils {
    fun <T> unique(items: List<T>): List<T> {
        val seen = LinkedHashSet<T>()
        val result = ArrayList<T>()
        for (item in items) {
            if (seen.add(item)) {
                result.add(item)
            }
        }
        return result
    }

    fun <T> chunk(items: List<T>, size: Int): List<List<T>> {
        if (size <= 0) {
            return emptyList()
        }
        val result = ArrayList<List<T>>()
        var index = 0
        while (index < items.size) {
            result.add(items.subList(index, minOf(index + size, items.size)))
            index += size
        }
        return result
    }

    fun <T, K> groupBy(items: List<T>, keyFn: (T) -> K): Map<K, List<T>> {
        val groups = LinkedHashMap<K, MutableList<T>>()
        for (item in items) {
            groups.getOrPut(keyFn(item)) { ArrayList() }.add(item)
        }
        return groups
    }

    fun <T> flatten(items: List<List<T>>): List<T> {
        val result = ArrayList<T>()
        for (group in items) {
            result.addAll(group)
        }
        return result
    }

    fun <T> compact(items: List<T?>): List<T> {
        return items.filterNotNull()
    }

    fun <T> first(items: List<T>): T? {
        return items.firstOrNull()
    }

    fun <T> last(items: List<T>): T? {
        return items.lastOrNull()
    }

    fun <T, K : Comparable<K>> sortBy(items: List<T>, keyFn: (T) -> K): List<T> {
        return items.sortedBy(keyFn)
    }

    fun <T, K> keyBy(items: List<T>, keyFn: (T) -> K): Map<K, T> {
        val result = LinkedHashMap<K, T>()
        for (item in items) {
            result[keyFn(item)] = item
        }
        return result
    }

    fun <T> filter(items: List<T>, predicate: (T) -> Boolean): List<T> =
        items.filter(predicate)

    fun <T> find(items: List<T>, predicate: (T) -> Boolean): T? =
        items.firstOrNull(predicate)
}
