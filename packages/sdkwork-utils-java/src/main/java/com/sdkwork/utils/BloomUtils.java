package com.sdkwork.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class BloomUtils {
    private static final int MIN_BIT_COUNT = 64;
    private static final double LN2 = Math.log(2);

    private BloomUtils() {
    }

    public static final class BloomFilter {
        private final int bitCount;
        private final int hashCount;
        private final byte[] bits;

        public BloomFilter(int bitCount, int hashCount, byte[] bits) {
            this.bitCount = bitCount;
            this.hashCount = hashCount;
            this.bits = bits;
        }

        public int bitCount() {
            return bitCount;
        }

        public int hashCount() {
            return hashCount;
        }

        public byte[] bits() {
            return bits;
        }
    }

    public static int estimateBitCount(int expectedItems, double falsePositiveRate) {
        if (expectedItems <= 0) {
            return MIN_BIT_COUNT;
        }
        double rate = Math.min(0.25, Math.max(0.0001, falsePositiveRate));
        double size = (-expectedItems * Math.log(rate)) / (LN2 * LN2);
        return Math.max(MIN_BIT_COUNT, (int) Math.ceil(size));
    }

    public static int estimateHashCount(int expectedItems, int bitCount) {
        if (expectedItems <= 0) {
            return 1;
        }
        double count = ((double) bitCount / expectedItems) * LN2;
        return Math.max(1, (int) Math.round(count));
    }

    public static BloomFilter create(int expectedItems, double falsePositiveRate) {
        int bitCount = estimateBitCount(expectedItems, falsePositiveRate);
        int hashCount = estimateHashCount(expectedItems, bitCount);
        return new BloomFilter(bitCount, hashCount, new byte[(bitCount + 7) / 8]);
    }

    public static void add(BloomFilter filter, String value) {
        for (int index : hashPositions(value.getBytes(StandardCharsets.UTF_8), filter.bitCount(), filter.hashCount())) {
            int byteIndex = index / 8;
            int bitIndex = index % 8;
            filter.bits()[byteIndex] |= (byte) (1 << bitIndex);
        }
    }

    public static boolean mightContain(BloomFilter filter, String value) {
        for (int index : hashPositions(value.getBytes(StandardCharsets.UTF_8), filter.bitCount(), filter.hashCount())) {
            int byteIndex = index / 8;
            int bitIndex = index % 8;
            if ((filter.bits()[byteIndex] & (1 << bitIndex)) == 0) {
                return false;
            }
        }
        return true;
    }

    private static int[] hashPositions(byte[] value, int bitCount, int hashCount) {
        byte[] digest = sha256(value);
        long h1 = readU32(digest, 0);
        long h2 = readU32(digest, 4);
        if (h2 == 0) {
            h2 = 1;
        }

        int[] positions = new int[hashCount];
        long modulus = bitCount;
        for (int index = 0; index < hashCount; index++) {
            positions[index] = (int) ((h1 + (long) index * h2) % modulus);
        }
        return positions;
    }

    private static long readU32(byte[] digest, int offset) {
        return Integer.toUnsignedLong(ByteBuffer.wrap(digest, offset, 4).order(ByteOrder.BIG_ENDIAN).getInt());
    }

    private static byte[] sha256(byte[] value) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(value);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 unavailable", exception);
        }
    }
}
