<?php

declare(strict_types=1);

namespace Sdkwork\Utils;

final class CurrencyUtils
{
    /** @var array<string, array{int, string}> */
    private const KNOWN = [
        'USD' => [2, '$'],
        'EUR' => [2, '€'],
        'GBP' => [2, '£'],
        'CNY' => [2, '¥'],
        'JPY' => [0, '¥'],
        'KRW' => [0, '₩'],
        'HKD' => [2, 'HK$'],
        'TWD' => [2, 'NT$'],
        'CHF' => [2, 'CHF'],
        'CAD' => [2, 'CA$'],
        'AUD' => [2, 'A$'],
        'INR' => [2, '₹'],
        'BHD' => [3, 'BHD'],
        'KWD' => [3, 'KWD'],
    ];

    public static function isCurrencyCode(string $value): bool
    {
        return self::lookup($value) !== null;
    }

    public static function minorUnitExponent(string $code): ?int
    {
        $meta = self::lookup($code);
        return $meta[0] ?? null;
    }

    public static function toMinorUnits(float $amount, string $code): ?int
    {
        $meta = self::lookup($code);
        if ($meta === null) {
            return null;
        }
        [$exponent] = $meta;
        $factor = 10 ** $exponent;
        return (int) NumberUtils::round($amount * $factor, 0);
    }

    public static function fromMinorUnits(int $minor, string $code): ?float
    {
        $meta = self::lookup($code);
        if ($meta === null) {
            return null;
        }
        [$exponent] = $meta;
        $factor = 10 ** $exponent;
        return $minor / $factor;
    }

    public static function formatCurrency(float $amount, string $code, string $locale): ?string
    {
        $meta = self::lookup($code);
        if ($meta === null) {
            return null;
        }
        [$exponent, $symbol] = $meta;
        $formatted = I18nUtils::formatNumberLocale($amount, $locale, $exponent);
        if (self::suffixLocale($locale)) {
            return $formatted . ' ' . $symbol;
        }
        return $symbol . $formatted;
    }

    private static function lookup(string $code): ?array
    {
        $normalized = trim($code);
        if (strlen($normalized) !== 3 || $normalized !== strtoupper($normalized) || !ctype_alpha($normalized)) {
            return null;
        }
        return self::KNOWN[$normalized] ?? null;
    }

    private static function suffixLocale(string $locale): bool
    {
        $normalized = strtolower($locale);
        return str_starts_with($normalized, 'de')
            || str_starts_with($normalized, 'fr')
            || str_starts_with($normalized, 'it')
            || str_starts_with($normalized, 'es');
    }
}
