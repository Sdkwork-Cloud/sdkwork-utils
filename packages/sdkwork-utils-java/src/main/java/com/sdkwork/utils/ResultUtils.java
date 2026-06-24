package com.sdkwork.utils;

import java.util.function.Function;

public final class ResultUtils {
    private ResultUtils() {
    }

    public record ResultValue<T>(T value, String error, boolean ok) {
        public static <T> ResultValue<T> ok(T value) {
            return new ResultValue<>(value, null, true);
        }

        public static <T> ResultValue<T> err(String message) {
            return new ResultValue<>(null, message, false);
        }
    }

    public static <T> boolean isOk(ResultValue<T> result) {
        return result.ok();
    }

    public static <T> boolean isErr(ResultValue<T> result) {
        return !result.ok();
    }

    public static <T> T unwrapOr(ResultValue<T> result, T defaultValue) {
        return result.ok() ? result.value() : defaultValue;
    }

    public static <T, U> ResultValue<U> map(ResultValue<T> result, Function<T, U> mapper) {
        return result.ok() ? ResultValue.ok(mapper.apply(result.value())) : ResultValue.err(result.error());
    }
}
