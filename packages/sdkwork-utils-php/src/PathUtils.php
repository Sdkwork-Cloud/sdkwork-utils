<?php

declare(strict_types=1);

namespace Sdkwork\Utils;

final class PathUtils
{
    public static function joinPath(string ...$segments): string
    {
        $parts = [];
        foreach ($segments as $segment) {
            $clean = trim($segment, '/');
            if ($clean !== '') {
                $parts[] = $clean;
            }
        }
        return implode('/', $parts);
    }

    public static function normalizePath(string $value): string
    {
        $parts = array_values(array_filter(explode('/', $value), static fn ($part) => $part !== ''));
        $joined = implode('/', $parts);
        return str_starts_with($value, '/') ? '/' . $joined : $joined;
    }
}
