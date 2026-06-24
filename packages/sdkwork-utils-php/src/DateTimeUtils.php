<?php

declare(strict_types=1);

namespace Sdkwork\Utils;

final class DateTimeUtils
{
    public const DEFAULT_PATTERN = 'iso8601';

    public static function now(): \DateTimeImmutable
    {
        return new \DateTimeImmutable('now', new \DateTimeZone('UTC'));
    }

    public static function formatDatetime(\DateTimeImmutable $value, string $pattern = self::DEFAULT_PATTERN): string
    {
        if ($pattern !== self::DEFAULT_PATTERN) {
            throw new \InvalidArgumentException('Unsupported datetime pattern: ' . $pattern);
        }
        return $value->setTimezone(new \DateTimeZone('UTC'))->format('Y-m-d\TH:i:s.v\Z');
    }

    public static function parseDatetime(string $value, string $pattern = self::DEFAULT_PATTERN): ?\DateTimeImmutable
    {
        if ($pattern !== self::DEFAULT_PATTERN) {
            return null;
        }
        $normalized = str_replace('Z', '+00:00', trim($value));
        $parsed = \DateTimeImmutable::createFromFormat('Y-m-d\TH:i:s.vP', $normalized)
            ?: \DateTimeImmutable::createFromFormat(DATE_ATOM, $normalized);
        return $parsed?->setTimezone(new \DateTimeZone('UTC'));
    }

    public static function addDays(\DateTimeImmutable $value, int $days): \DateTimeImmutable
    {
        return $value->modify($days . ' days');
    }

    public static function addHours(\DateTimeImmutable $value, int $hours): \DateTimeImmutable
    {
        return $value->modify($hours . ' hours');
    }

    public static function addMinutes(\DateTimeImmutable $value, int $minutes): \DateTimeImmutable
    {
        return $value->modify($minutes . ' minutes');
    }

    public static function diffMillis(\DateTimeImmutable $earlier, \DateTimeImmutable $later): int
    {
        return (int) (($later->getTimestamp() - $earlier->getTimestamp()) * 1000
            + ((int) $later->format('v') - (int) $earlier->format('v')));
    }

    public static function isBefore(\DateTimeImmutable $first, \DateTimeImmutable $second): bool
    {
        return $first < $second;
    }

    public static function isAfter(\DateTimeImmutable $first, \DateTimeImmutable $second): bool
    {
        return $first > $second;
    }

    public static function startOfDayUtc(\DateTimeImmutable $value): \DateTimeImmutable
    {
        $utc = $value->setTimezone(new \DateTimeZone('UTC'));
        return $utc->setTime(0, 0, 0, 0);
    }

    public static function endOfDayUtc(\DateTimeImmutable $value): \DateTimeImmutable
    {
        return self::startOfDayUtc($value)->modify('+1 day -1 millisecond');
    }

    public static function toUnixMillis(\DateTimeImmutable $value): int
    {
        $utc = $value->setTimezone(new \DateTimeZone('UTC'));
        return $utc->getTimestamp() * 1000 + (int) $utc->format('v');
    }

    public static function fromUnixMillis(int $value): ?\DateTimeImmutable
    {
        $seconds = intdiv($value, 1000);
        $millis = abs($value % 1000);
        $parsed = \DateTimeImmutable::createFromFormat('U.v', sprintf('%d.%03d', $seconds, $millis), new \DateTimeZone('UTC'));
        return $parsed === false ? null : $parsed;
    }

    public static function isSameInstant(\DateTimeImmutable $first, \DateTimeImmutable $second): bool
    {
        return self::toUnixMillis($first) === self::toUnixMillis($second);
    }
}
