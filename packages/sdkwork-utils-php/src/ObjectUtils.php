<?php

declare(strict_types=1);

namespace Sdkwork\Utils;

final class ObjectUtils
{
    public static function pick(array $source, array $keys): array
    {
        $result = [];
        foreach ($keys as $key) {
            if (array_key_exists($key, $source)) {
                $result[$key] = $source[$key];
            }
        }
        return $result;
    }

    public static function omit(array $source, array $keys): array
    {
        $blocked = array_flip($keys);
        $result = [];
        foreach ($source as $key => $value) {
            if (!isset($blocked[$key])) {
                $result[$key] = $value;
            }
        }
        return $result;
    }

    public static function getPath(array $source, string $path): mixed
    {
        $current = $source;
        foreach (self::splitPath($path) as $segment) {
            if (!is_array($current) || !array_key_exists($segment, $current)) {
                return null;
            }
            $current = $current[$segment];
        }
        return $current;
    }

    public static function hasPath(array $source, string $path): bool
    {
        $value = self::getPath($source, $path);
        return $value !== null;
    }

    public static function shallowMerge(array $base, array $overlay): array
    {
        return array_merge($base, $overlay);
    }

    public static function setPath(array $source, string $path, mixed $value): array
    {
        $segments = self::splitPath($path);
        if ($segments === []) {
            return $source;
        }
        $current = &$source;
        foreach (array_slice($segments, 0, -1) as $segment) {
            if (!isset($current[$segment]) || !is_array($current[$segment])) {
                $current[$segment] = [];
            }
            $current = &$current[$segment];
        }
        $current[$segments[array_key_last($segments)]] = $value;
        return $source;
    }

    public static function deepMerge(array $base, array $overlay): array
    {
        $result = $base;
        foreach ($overlay as $key => $value) {
            if (isset($result[$key]) && is_array($result[$key]) && is_array($value)) {
                $result[$key] = self::deepMerge($result[$key], $value);
            } else {
                $result[$key] = $value;
            }
        }
        return $result;
    }

    public static function compact(array $source): array
    {
        return array_filter($source, static fn ($value) => $value !== null);
    }

    public static function keys(array $source): array
    {
        return array_keys($source);
    }

    public static function values(array $source): array
    {
        return array_values($source);
    }

    private static function splitPath(string $path): array
    {
        return array_values(array_filter(explode('.', $path), static fn ($part) => $part !== ''));
    }
}
