<?php

declare(strict_types=1);

namespace Sdkwork\Utils;

final class BloomFilter
{
    public function __construct(
        public readonly int $bitCount,
        public readonly int $hashCount,
        public string $bits,
    ) {
    }
}

final class BloomUtils
{
    private const MIN_BIT_COUNT = 64;
    private const LN2 = 0.6931471805599453;

    public static function estimateBitCount(int $expectedItems, float $falsePositiveRate): int
    {
        if ($expectedItems <= 0) {
            return self::MIN_BIT_COUNT;
        }

        $rate = min(0.25, max(0.0001, $falsePositiveRate));
        $size = (-$expectedItems * log($rate)) / (self::LN2 * self::LN2);

        return max(self::MIN_BIT_COUNT, (int) ceil($size));
    }

    public static function estimateHashCount(int $expectedItems, int $bitCount): int
    {
        if ($expectedItems <= 0) {
            return 1;
        }

        $count = ($bitCount / $expectedItems) * self::LN2;

        return max(1, (int) round($count));
    }

    public static function create(int $expectedItems, float $falsePositiveRate): BloomFilter
    {
        $bitCount = self::estimateBitCount($expectedItems, $falsePositiveRate);
        $hashCount = self::estimateHashCount($expectedItems, $bitCount);

        return new BloomFilter($bitCount, $hashCount, str_repeat("\0", intdiv($bitCount + 7, 8)));
    }

    public static function add(BloomFilter $filter, string $value): void
    {
        foreach (self::hashPositions($value, $filter->bitCount, $filter->hashCount) as $index) {
            $byteIndex = intdiv($index, 8);
            $bitIndex = $index % 8;
            $filter->bits[$byteIndex] = chr(ord($filter->bits[$byteIndex]) | (1 << $bitIndex));
        }
    }

    public static function mightContain(BloomFilter $filter, string $value): bool
    {
        foreach (self::hashPositions($value, $filter->bitCount, $filter->hashCount) as $index) {
            $byteIndex = intdiv($index, 8);
            $bitIndex = $index % 8;
            if ((ord($filter->bits[$byteIndex]) & (1 << $bitIndex)) === 0) {
                return false;
            }
        }

        return true;
    }

    /** @return list<int> */
    private static function hashPositions(string $value, int $bitCount, int $hashCount): array
    {
        $digest = hash('sha256', $value, true);
        $h1 = self::readU32($digest, 0);
        $h2 = self::readU32($digest, 4);
        if ($h2 === 0) {
            $h2 = 1;
        }

        $positions = [];
        for ($index = 0; $index < $hashCount; $index++) {
            $positions[] = (int) (($h1 + $index * $h2) % $bitCount);
        }

        return $positions;
    }

    private static function readU32(string $digest, int $offset): int
    {
        return unpack('N', substr($digest, $offset, 4))[1];
    }
}
