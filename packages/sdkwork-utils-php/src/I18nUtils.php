<?php

declare(strict_types=1);

namespace Sdkwork\Utils;

final class I18nUtils
{
    public static function formatNumberLocale(float $value, string $locale, int $decimals = 0): string
    {
        return number_format($value, $decimals, $locale === 'de-DE' ? ',' : '.', $locale === 'de-DE' ? '.' : ',');
    }

    public static function formatDatetimeLocale(\DateTimeImmutable $value, string $locale): string
    {
        $utc = $value->setTimezone(new \DateTimeZone('UTC'));
        $normalized = strtolower($locale);
        if (str_starts_with($normalized, 'de')) {
            return $utc->format('d.m.Y H:i');
        }
        if (str_starts_with($normalized, 'zh')) {
            return $utc->format('Y-m-d H:i');
        }
        return $utc->format('m/d/Y H:i');
    }

    public static function formatDatetimeLocaleStr(string $value, string $locale): ?string
    {
        $parsed = DateTimeUtils::parseDatetime($value);
        return $parsed === null ? null : self::formatDatetimeLocale($parsed, $locale);
    }

    public static function parseNumberLocale(string $input, string $locale): ?float
    {
        $trimmed = trim($input);
        if ($trimmed === '') {
            return null;
        }
        $decimalSeparator = $locale === 'de-DE' ? ',' : '.';
        $groupingSeparator = $locale === 'de-DE' ? '.' : ',';
        $normalized = str_replace([$groupingSeparator, $decimalSeparator], ['', '.'], $trimmed);
        return NumberUtils::parseNumber($normalized);
    }
}
