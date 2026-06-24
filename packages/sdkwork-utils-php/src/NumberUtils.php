<?php

declare(strict_types=1);

namespace Sdkwork\Utils;

final class NumberUtils
{
    public static function clamp(float $value, float $min, float $max): float
    {
        return min($max, max($min, $value));
    }

    public static function round(float $value, int $decimals = 0): float
    {
        return round($value, $decimals);
    }

    public static function formatNumber(float $value, int $decimals = 0): string
    {
        return number_format($value, $decimals, '.', '');
    }

    public static function parseNumber(string $value): ?float
    {
        if (!is_numeric(trim($value))) {
            return null;
        }
        return (float) trim($value);
    }

    public static function isInteger(float $value): bool
    {
        return fmod($value, 1.0) === 0.0;
    }

    public static function parseInt(string $value): ?int
    {
        $trimmed = trim($value);
        if ($trimmed === '' || filter_var($trimmed, FILTER_VALIDATE_INT) === false) {
            return null;
        }
        return (int) $trimmed;
    }

    public static function percentFormat(float $value, int $decimals = 0): string
    {
        return self::formatNumber($value * 100, $decimals) . '%';
    }

    public static function inRange(float $value, float $min, float $max): bool
    {
        return $value >= $min && $value <= $max;
    }

    public static function abs(float $value): float
    {
        return $value >= 0 ? $value : -$value;
    }
}
