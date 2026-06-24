package com.sdkwork.utils

object ObjectUtils {
    fun pick(source: Map<String, Any?>, keys: List<String>): Map<String, Any?> {
        val result = LinkedHashMap<String, Any?>()
        for (key in keys) {
            if (source.containsKey(key)) {
                result[key] = source[key]
            }
        }
        return result
    }

    fun omit(source: Map<String, Any?>, keys: List<String>): Map<String, Any?> {
        val result = LinkedHashMap<String, Any?>()
        for ((key, value) in source) {
            if (key !in keys) {
                result[key] = value
            }
        }
        return result
    }

    @Suppress("UNCHECKED_CAST")
    fun getPath(source: Any?, path: String): Any? {
        var current: Any? = source
        for (segment in splitPath(path)) {
            val map = current as? Map<*, *> ?: return null
            if (!map.containsKey(segment)) {
                return null
            }
            current = map[segment]
        }
        return current
    }

    fun hasPath(source: Any?, path: String): Boolean {
        return getPath(source, path) != null
    }

    @Suppress("UNCHECKED_CAST")
    fun shallowMerge(base: Any?, overlay: Any?): Any? {
        if (base is Map<*, *> && overlay is Map<*, *>) {
            val result = LinkedHashMap(base as Map<String, Any?>)
            for ((key, value) in overlay) {
                val stringKey = key?.toString() ?: continue
                result[stringKey] = value
            }
            return result
        }
        return overlay
    }

    @Suppress("UNCHECKED_CAST")
    fun setPath(source: Map<String, Any?>, path: String, value: Any?): Map<String, Any?> {
        val segments = splitPath(path)
        if (segments.isEmpty()) {
            return source
        }

        val root = LinkedHashMap(source)
        var current: MutableMap<String, Any?> = root
        for (index in 0 until segments.size - 1) {
            val segment = segments[index]
            val next = current[segment]
            val nextMap = if (next !is Map<*, *>) {
                LinkedHashMap<String, Any?>()
            } else {
                LinkedHashMap(next as Map<String, Any?>)
            }
            current[segment] = nextMap
            current = nextMap
        }
        current[segments[segments.size - 1]] = value
        return root
    }

    @Suppress("UNCHECKED_CAST")
    fun deepMerge(base: Any?, overlay: Any?): Any? {
        if (base is Map<*, *> && overlay is Map<*, *>) {
            val result = LinkedHashMap(base as Map<String, Any?>)
            for ((key, value) in overlay) {
                val stringKey = key?.toString() ?: continue
                result[stringKey] = if (result.containsKey(stringKey)) {
                    deepMerge(result[stringKey], value)
                } else {
                    value
                }
            }
            return result
        }
        return overlay
    }

    fun compact(source: Map<String, Any?>): Map<String, Any?> {
        return source.filterValues { it != null }
    }

    fun keys(source: Map<String, Any?>): List<String> {
        return source.keys.toList()
    }

    fun values(source: Map<String, Any?>): List<Any?> {
        return source.values.toList()
    }

    private fun splitPath(path: String): List<String> {
        return path.split(".")
            .filter { it.isNotEmpty() }
    }
}
