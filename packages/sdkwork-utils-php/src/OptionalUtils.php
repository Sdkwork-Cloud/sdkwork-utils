<?php

declare(strict_types=1);

namespace Sdkwork\Utils;

final class OptionalUtils
{
    public static function coalesce(mixed ...$values): mixed
    {
        foreach ($values as $value) {
            if ($value === null) {
                continue;
            }
            if (is_string($value) && StringUtils::isBlank($value)) {
                continue;
            }
            return is_string($value) ? trim($value) : $value;
        }
        return null;
    }

    public static function defaultIfBlank(?string $value, string $default): string
    {
        return StringUtils::isBlank($value) ? $default : trim((string) $value);
    }
}
