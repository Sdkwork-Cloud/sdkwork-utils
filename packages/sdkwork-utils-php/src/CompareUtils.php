<?php

declare(strict_types=1);

namespace Sdkwork\Utils;

final class CompareUtils
{
    public static function deepEqual(mixed $left, mixed $right): bool
    {
        if ($left === $right) {
            return true;
        }
        if ($left === null || $right === null) {
            return false;
        }
        if (is_array($left) && is_array($right) && self::isList($left) && self::isList($right)) {
            if (count($left) !== count($right)) {
                return false;
            }
            foreach ($left as $index => $value) {
                if (!self::deepEqual($value, $right[$index])) {
                    return false;
                }
            }
            return true;
        }
        if (is_array($left) && is_array($right) && !self::isList($left) && !self::isList($right)) {
            if (count($left) !== count($right)) {
                return false;
            }
            foreach ($left as $key => $value) {
                if (!array_key_exists($key, $right) || !self::deepEqual($value, $right[$key])) {
                    return false;
                }
            }
            return true;
        }
        return $left == $right;
    }

    public static function deepClone(mixed $value): mixed
    {
        if (!is_array($value)) {
            return $value;
        }
        $cloned = [];
        foreach ($value as $key => $nested) {
            $cloned[$key] = self::deepClone($nested);
        }
        return $cloned;
    }

    private static function isList(array $value): bool
    {
        return $value === [] || array_keys($value) === range(0, count($value) - 1);
    }
}
