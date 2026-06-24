package com.sdkwork.utils

data class ResultValue<T>(val value: T? = null, val error: String? = null, val ok: Boolean = false) {
    companion object {
        fun <T> ok(value: T): ResultValue<T> = ResultValue(value = value, ok = true)
        fun <T> err(message: String): ResultValue<T> = ResultValue(error = message, ok = false)
    }
}

object ResultUtils {
    fun <T> isOk(result: ResultValue<T>): Boolean = result.ok
    fun <T> isErr(result: ResultValue<T>): Boolean = !result.ok
    fun <T> unwrapOr(result: ResultValue<T>, defaultValue: T): T = if (result.ok) result.value!! else defaultValue
    fun <T, U> map(result: ResultValue<T>, mapper: (T) -> U): ResultValue<U> {
        return if (result.ok) ResultValue.ok(mapper(result.value!!)) else ResultValue.err(result.error ?: "error")
    }
}
