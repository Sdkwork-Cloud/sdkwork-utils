<?php

declare(strict_types=1);

namespace Sdkwork\Utils;

final class ResultValue
{
    public function __construct(
        public readonly mixed $value = null,
        public readonly ?string $error = null,
        public readonly bool $ok = false,
    ) {
    }

    public static function ok(mixed $value): self
    {
        return new self(value: $value, ok: true);
    }

    public static function err(string $message): self
    {
        return new self(error: $message, ok: false);
    }

    public function isOk(): bool
    {
        return $this->ok;
    }

    public function isErr(): bool
    {
        return !$this->ok;
    }

    public function unwrapOr(mixed $default): mixed
    {
        return $this->ok ? $this->value : $default;
    }

    public function map(callable $mapper): self
    {
        return $this->ok ? self::ok($mapper($this->value)) : self::err($this->error ?? 'error');
    }
}

final class ResultUtils
{
    public static function ok(mixed $value): ResultValue
    {
        return ResultValue::ok($value);
    }

    public static function err(string $message): ResultValue
    {
        return ResultValue::err($message);
    }

    public static function unwrapOr(ResultValue $result, mixed $default): mixed
    {
        return $result->unwrapOr($default);
    }
}
