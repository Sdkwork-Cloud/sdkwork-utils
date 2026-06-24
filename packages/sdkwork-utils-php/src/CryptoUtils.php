<?php

declare(strict_types=1);

namespace Sdkwork\Utils;

final class CryptoUtils
{
    public static function sha256Hash(string $value): string
    {
        return hash('sha256', $value);
    }

    public static function hmacSha256(string $value, string $secret): string
    {
        return hash_hmac('sha256', $value, $secret);
    }

    public static function secureCompare(string $left, string $right): bool
    {
        return hash_equals($left, $right);
    }
}
