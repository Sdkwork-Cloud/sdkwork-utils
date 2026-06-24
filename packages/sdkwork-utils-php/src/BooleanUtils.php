<?php

declare(strict_types=1);

namespace Sdkwork\Utils;

final class BooleanUtils
{
    public static function parseBool(string $value): ?bool
    {
        return match (strtolower(trim($value))) {
            'true', '1', 'yes', 'on' => true,
            'false', '0', 'no', 'off' => false,
            default => null,
        };
    }

    public static function isTruthy(?string $value): bool
    {
        if ($value === null || trim($value) === '') {
            return false;
        }

        return !in_array(strtolower(trim($value)), ['false', '0', 'no', 'off'], true);
    }
}
