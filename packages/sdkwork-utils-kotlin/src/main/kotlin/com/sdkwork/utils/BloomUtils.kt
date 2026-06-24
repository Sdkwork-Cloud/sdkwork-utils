package com.sdkwork.utils

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.MessageDigest
import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object BloomUtils {
    private const val MIN_BIT_COUNT = 64
    private val LN2 = ln(2.0)

    data class BloomFilter(
        val bitCount: Int,
        val hashCount: Int,
        val bits: ByteArray,
    )

    fun estimateBitCount(expectedItems: Int, falsePositiveRate: Double): Int {
        if (expectedItems <= 0) {
            return MIN_BIT_COUNT
        }
        val rate = min(0.25, max(0.0001, falsePositiveRate))
        val size = (-expectedItems * ln(rate)) / (LN2 * LN2)
        return max(MIN_BIT_COUNT, ceil(size).toInt())
    }

    fun estimateHashCount(expectedItems: Int, bitCount: Int): Int {
        if (expectedItems <= 0) {
            return 1
        }
        val count = (bitCount.toDouble() / expectedItems) * LN2
        return max(1, count.roundToInt())
    }

    fun create(expectedItems: Int, falsePositiveRate: Double): BloomFilter {
        val bitCount = estimateBitCount(expectedItems, falsePositiveRate)
        val hashCount = estimateHashCount(expectedItems, bitCount)
        return BloomFilter(bitCount, hashCount, ByteArray((bitCount + 7) / 8))
    }

    fun add(filter: BloomFilter, value: String) {
        for (index in hashPositions(value.toByteArray(Charsets.UTF_8), filter.bitCount, filter.hashCount)) {
            val byteIndex = index / 8
            val bitIndex = index % 8
            filter.bits[byteIndex] = (filter.bits[byteIndex].toInt() or (1 shl bitIndex)).toByte()
        }
    }

    fun mightContain(filter: BloomFilter, value: String): Boolean {
        for (index in hashPositions(value.toByteArray(Charsets.UTF_8), filter.bitCount, filter.hashCount)) {
            val byteIndex = index / 8
            val bitIndex = index % 8
            if ((filter.bits[byteIndex].toInt() and (1 shl bitIndex)) == 0) {
                return false
            }
        }
        return true
    }

    private fun hashPositions(value: ByteArray, bitCount: Int, hashCount: Int): IntArray {
        val digest = MessageDigest.getInstance("SHA-256").digest(value)
        var h1 = readU32(digest, 0)
        var h2 = readU32(digest, 4)
        if (h2 == 0L) {
            h2 = 1
        }

        val modulus = bitCount.toLong()
        return IntArray(hashCount) { index ->
            ((h1 + index.toLong() * h2) % modulus).toInt()
        }
    }

    private fun readU32(digest: ByteArray, offset: Int): Long {
        return Integer.toUnsignedLong(
            ByteBuffer.wrap(digest, offset, 4).order(ByteOrder.BIG_ENDIAN).int,
        )
    }
}
