package com.sdkwork.utils

object CompareUtils {
    fun deepEqual(left: Any?, right: Any?): Boolean {
        if (left === right) {
            return true
        }
        if (left == null || right == null) {
            return false
        }
        if (left is Map<*, *> && right is Map<*, *>) {
            if (left.size != right.size) {
                return false
            }
            for ((key, value) in left) {
                if (!right.containsKey(key) || !deepEqual(value, right[key])) {
                    return false
                }
            }
            return true
        }
        if (left is List<*> && right is List<*>) {
            if (left.size != right.size) {
                return false
            }
            for (index in left.indices) {
                if (!deepEqual(left[index], right[index])) {
                    return false
                }
            }
            return true
        }
        return left == right
    }

    fun deepClone(value: Any?): Any? {
        return when (value) {
            is Map<*, *> -> value.entries.associate { (key, nested) -> key to deepClone(nested) }
            is List<*> -> value.map { deepClone(it) }
            else -> value
        }
    }
}
