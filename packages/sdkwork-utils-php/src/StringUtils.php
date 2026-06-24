<?php

declare(strict_types=1);

namespace Sdkwork\Utils;

final class StringUtils
{
    public static function isBlank(?string $value): bool
    {
        return $value === null || trim($value) === '';
    }

    public static function trim(?string $value): string
    {
        return trim($value ?? '');
    }

    public static function truncate(string $value, int $maxLen, string $suffix = '...'): string
    {
        if ($maxLen <= 0) {
            return '';
        }
        if (mb_strlen($value) <= $maxLen) {
            return $value;
        }
        if (mb_strlen($suffix) >= $maxLen) {
            return mb_substr($suffix, 0, $maxLen);
        }
        return mb_substr($value, 0, $maxLen - mb_strlen($suffix)) . $suffix;
    }

    public static function capitalize(string $value): string
    {
        if ($value === '') {
            return '';
        }
        return strtoupper($value[0]) . strtolower(substr($value, 1));
    }

    public static function camelCase(string $value): string
    {
        $parts = self::camelParts($value);
        if ($parts === []) {
            return '';
        }
        return $parts[0] . implode('', array_map([self::class, 'capitalize'], array_slice($parts, 1)));
    }

    public static function snakeCase(string $value): string
    {
        return implode('_', self::camelParts($value));
    }

    public static function kebabCase(string $value): string
    {
        return implode('-', self::camelParts($value));
    }

    public static function slugify(string $value): string
    {
        return trim(preg_replace('/[^a-z0-9-]/', '', self::kebabCase($value)) ?? '', '-');
    }

    public static function mask(string $value, int $visibleStart, int $visibleEnd, string $maskChar = '*'): string
    {
        if ($visibleStart + $visibleEnd >= strlen($value)) {
            return $value;
        }
        $hidden = strlen($value) - $visibleStart - $visibleEnd;
        return substr($value, 0, $visibleStart) . str_repeat($maskChar, $hidden) . substr($value, -$visibleEnd);
    }

    public static function padStart(string $value, int $targetLen, string $padChar = ' '): string
    {
        if (mb_strlen($value) >= $targetLen) {
            return $value;
        }
        return str_repeat($padChar, $targetLen - mb_strlen($value)) . $value;
    }

    public static function padEnd(string $value, int $targetLen, string $padChar = ' '): string
    {
        if (mb_strlen($value) >= $targetLen) {
            return $value;
        }
        return $value . str_repeat($padChar, $targetLen - mb_strlen($value));
    }

    public static function startsWith(string $value, string $prefix): bool
    {
        return str_starts_with($value, $prefix);
    }

    public static function endsWith(string $value, string $suffix): bool
    {
        return str_ends_with($value, $suffix);
    }

    public static function contains(string $value, string $substring): bool
    {
        return str_contains($value, $substring);
    }

    public static function replaceAll(string $value, string $search, string $replacement): string
    {
        return str_replace($search, $replacement, $value);
    }

    public static function split(string $value, string $delimiter, bool $trimParts = true): array
    {
        $parts = explode($delimiter, $value);
        if (!$trimParts) {
            return $parts;
        }

        $result = [];
        foreach ($parts as $part) {
            $text = trim($part);
            if ($text !== '') {
                $result[] = $text;
            }
        }
        return $result;
    }

    public static function join(array $parts, string $separator): string
    {
        return implode($separator, $parts);
    }

    public static function repeat(string $value, int $count): string
    {
        if ($count < 0) {
            throw new \InvalidArgumentException('repeat count must be >= 0');
        }
        return str_repeat($value, $count);
    }

    public static function normalizeWhitespace(string $value): string
    {
        return trim(preg_replace('/\s+/u', ' ', trim($value)) ?? '');
    }

    public static function template(string $pattern, array $values): string
    {
        return preg_replace_callback(
            '/\{([a-zA-Z_][a-zA-Z0-9_]*)\}/',
            static function (array $matches) use ($values): string {
                return $values[$matches[1]] ?? $matches[0];
            },
            $pattern,
        ) ?? $pattern;
    }

    private static function camelParts(string $value): array
    {
        $normalized = preg_replace('/([a-z0-9])([A-Z])|([A-Z]+)([A-Z][a-z])/', '$1$3 $2$4', trim($value)) ?? '';
        $parts = preg_split('/[^a-zA-Z0-9]+/', $normalized) ?: [];
        return array_values(array_filter(array_map('strtolower', $parts)));
    }
}
