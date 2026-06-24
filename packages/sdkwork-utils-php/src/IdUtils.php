<?php

declare(strict_types=1);

namespace Sdkwork\Utils;

final class IdUtils
{
    public static function uuid(): string
    {
        $data = random_bytes(16);
        $data[6] = chr(ord($data[6]) & 0x0f | 0x40);
        $data[8] = chr(ord($data[8]) & 0x3f | 0x80);
        return vsprintf('%s%s-%s-%s-%s-%s%s%s', str_split(bin2hex($data), 4));
    }

    public static function randomString(int $length): string
    {
        $alphabet = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
        $result = '';
        for ($index = 0; $index < $length; $index++) {
            $result .= $alphabet[random_int(0, strlen($alphabet) - 1)];
        }
        return $result;
    }
}
