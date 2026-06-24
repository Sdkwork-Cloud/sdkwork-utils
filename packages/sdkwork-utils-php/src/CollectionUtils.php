<?php

declare(strict_types=1);

namespace Sdkwork\Utils;

final class CollectionUtils
{
    public static function unique(array $items): array
    {
        $seen = [];
        $result = [];
        foreach ($items as $item) {
            $key = is_object($item) ? spl_object_hash($item) : (string) json_encode($item);
            if (isset($seen[$key])) {
                continue;
            }
            $seen[$key] = true;
            $result[] = $item;
        }
        return $result;
    }

    public static function chunk(array $items, int $size): array
    {
        if ($size <= 0) {
            return [];
        }
        return array_chunk($items, $size);
    }

    public static function groupBy(array $items, callable $keyFn): array
    {
        $groups = [];
        foreach ($items as $item) {
            $groups[$keyFn($item)][] = $item;
        }
        return $groups;
    }

    public static function flatten(array $items): array
    {
        $result = [];
        foreach ($items as $group) {
            foreach ($group as $item) {
                $result[] = $item;
            }
        }
        return $result;
    }

    public static function compact(array $items): array
    {
        return array_values(array_filter($items, static fn ($item) => $item !== null));
    }

    public static function first(array $items): mixed
    {
        return $items === [] ? null : $items[0];
    }

    public static function last(array $items): mixed
    {
        return $items === [] ? null : $items[array_key_last($items)];
    }

    public static function sortBy(array $items, callable $keyFn): array
    {
        usort($items, static function ($left, $right) use ($keyFn) {
            return $keyFn($left) <=> $keyFn($right);
        });
        return $items;
    }

    public static function keyBy(array $items, callable $keyFn): array
    {
        $result = [];
        foreach ($items as $item) {
            $result[$keyFn($item)] = $item;
        }
        return $result;
    }

    public static function filter(array $items, callable $predicate): array
    {
        return array_values(array_filter($items, $predicate));
    }

    public static function find(array $items, callable $predicate): mixed
    {
        foreach ($items as $item) {
            if ($predicate($item)) {
                return $item;
            }
        }
        return null;
    }
}
