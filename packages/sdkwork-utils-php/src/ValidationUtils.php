<?php

declare(strict_types=1);

namespace Sdkwork\Utils;

final class ValidationUtils
{
    public static function isEmail(string $value): bool
    {
        return preg_match('/^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$/', trim($value)) === 1;
    }

    public static function isUuid(string $value): bool
    {
        return preg_match('/^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$/', trim($value)) === 1;
    }

    public static function isUrl(string $value): bool
    {
        return preg_match('/^https?:\\/\\/[^\\s\\/$.?#].[^\\s]*$/', trim($value)) === 1;
    }

    public static function isNumeric(string $value): bool
    {
        return NumberUtils::parseNumber($value) !== null;
    }

    public static function isIpv4(string $value): bool
    {
        return preg_match('/^(25[0-5]|2[0-4]\d|1?\d?\d)(\.(25[0-5]|2[0-4]\d|1?\d?\d)){3}$/', trim($value)) === 1;
    }

    public static function isIpv6(string $value): bool
    {
        return self::isIpv6Shape(trim($value));
    }

    public static function isPhoneE164(string $value): bool
    {
        return preg_match('/^\+[1-9]\d{1,14}$/', trim($value)) === 1;
    }

    private static function isIpv6Shape(string $value): bool
    {
        if ($value === '' || preg_match('/^[0-9a-fA-F:]+$/', $value) !== 1) {
            return false;
        }
        if (substr_count($value, '::') > 1) {
            return false;
        }
        if (str_contains($value, '::')) {
            [$left, $right] = array_pad(explode('::', $value, 2), 2, '');
            $leftParts = $left === '' ? [] : array_values(array_filter(explode(':', $left), static fn (string $part): bool => $part !== ''));
            $rightParts = $right === '' ? [] : array_values(array_filter(explode(':', $right), static fn (string $part): bool => $part !== ''));
            foreach (array_merge($leftParts, $rightParts) as $part) {
                if (!self::isIpv6Part($part)) {
                    return false;
                }
            }
            return count($leftParts) + count($rightParts) < 8;
        }
        $segments = explode(':', $value);
        if (count($segments) !== 8) {
            return false;
        }
        foreach ($segments as $segment) {
            if (!self::isIpv6Part($segment)) {
                return false;
            }
        }
        return true;
    }

    private static function isIpv6Part(string $part): bool
    {
        return $part !== '' && strlen($part) <= 4;
    }
}
