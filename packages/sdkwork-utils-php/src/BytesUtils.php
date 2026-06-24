<?php

declare(strict_types=1);

namespace Sdkwork\Utils;

final class BytesUtils
{
    /** @var list<string> */
    private const UNITS = ['B', 'KB', 'MB', 'GB', 'TB', 'PB'];

    public static function formatBytes(int $bytes, int $decimals = 1): string
    {
        $normalized = max(0, $bytes);
        if ($normalized < 1024) {
            return $normalized . ' B';
        }

        $size = (float) $normalized;
        $unitIndex = 0;
        while ($size >= 1024 && $unitIndex < count(self::UNITS) - 1) {
            $size /= 1024;
            $unitIndex++;
        }

        return sprintf('%.' . $decimals . 'f %s', $size, self::UNITS[$unitIndex]);
    }
}
