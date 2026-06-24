namespace Sdkwork.Utils;

public readonly record struct ResultValue<T>(T? Value, string? Error, bool IsSuccess)
{
    public static ResultValue<T> Success(T value) => new(value, null, true);
    public static ResultValue<T> Failure(string message) => new(default, message, false);
}

public static class ResultUtils
{
    public static bool IsOk<T>(ResultValue<T> result) => result.IsSuccess;
    public static bool IsErr<T>(ResultValue<T> result) => !result.IsSuccess;

    public static T UnwrapOr<T>(ResultValue<T> result, T defaultValue) =>
        result.IsSuccess ? result.Value! : defaultValue;

    public static ResultValue<U> Map<T, U>(ResultValue<T> result, Func<T, U> mapper) =>
        result.IsSuccess ? ResultValue<U>.Success(mapper(result.Value!)) : ResultValue<U>.Failure(result.Error ?? "error");
}
