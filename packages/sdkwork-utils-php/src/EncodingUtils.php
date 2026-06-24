<?php

declare(strict_types=1);

namespace Sdkwork\Utils;

final class EncodingUtils
{
    public static function base64Encode(string $value): string
    {
        return base64_encode($value);
    }

    public static function base64Decode(string $value): ?string
    {
        $decoded = base64_decode(trim($value), true);
        return $decoded === false ? null : $decoded;
    }

    public static function hexEncode(string $value): string
    {
        return bin2hex($value);
    }

    public static function hexDecode(string $value): ?string
    {
        $trimmed = trim($value);
        if ($trimmed === '' || strlen($trimmed) % 2 !== 0) {
            return null;
        }
        $decoded = hex2bin($trimmed);
        return $decoded === false ? null : $decoded;
    }

    public static function urlEncode(string $value): string
    {
        return rawurlencode($value);
    }

    public static function urlDecode(string $value): ?string
    {
        return rawurldecode($value);
    }

    public static function base64urlEncode(string $value): string
    {
        return rtrim(strtr(base64_encode($value), '+/', '-_'), '=');
    }

    public static function base64urlDecode(string $value): ?string
    {
        $trimmed = trim($value);
        if ($trimmed === '') {
            return null;
        }
        $padded = str_pad($trimmed, strlen($trimmed) + (4 - strlen($trimmed) % 4) % 4, '=', STR_PAD_RIGHT);
        $decoded = base64_decode(strtr($padded, '-_', '+/'), true);
        return $decoded === false ? null : $decoded;
    }
}
