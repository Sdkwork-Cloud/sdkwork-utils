package utils

import (
	"crypto/sha256"
	"encoding/binary"
	"math"
)

const minBitCount = 64
const ln2 = math.Ln2

type BloomFilter struct {
	BitCount  int
	HashCount int
	Bits      []byte
}

func EstimateBitCount(expectedItems int, falsePositiveRate float64) int {
	if expectedItems <= 0 {
		return minBitCount
	}
	rate := math.Min(0.25, math.Max(0.0001, falsePositiveRate))
	size := (-float64(expectedItems) * math.Log(rate)) / (ln2 * ln2)
	return int(math.Max(minBitCount, math.Ceil(size)))
}

func EstimateHashCount(expectedItems, bitCount int) int {
	if expectedItems <= 0 {
		return 1
	}
	count := (float64(bitCount) / float64(expectedItems)) * ln2
	return int(math.Max(1, math.Round(count)))
}

func Create(expectedItems int, falsePositiveRate float64) *BloomFilter {
	bitCount := EstimateBitCount(expectedItems, falsePositiveRate)
	hashCount := EstimateHashCount(expectedItems, bitCount)
	return &BloomFilter{
		BitCount:  bitCount,
		HashCount: hashCount,
		Bits:      make([]byte, (bitCount+7)/8),
	}
}

func Add(filter *BloomFilter, value string) {
	for _, index := range hashPositions([]byte(value), filter.BitCount, filter.HashCount) {
		byteIndex := index / 8
		bitIndex := index % 8
		filter.Bits[byteIndex] |= 1 << bitIndex
	}
}

func MightContain(filter *BloomFilter, value string) bool {
	for _, index := range hashPositions([]byte(value), filter.BitCount, filter.HashCount) {
		byteIndex := index / 8
		bitIndex := index % 8
		if (filter.Bits[byteIndex] & (1 << bitIndex)) == 0 {
			return false
		}
	}
	return true
}

func hashPositions(value []byte, bitCount, hashCount int) []int {
	digest := sha256.Sum256(value)
	h1 := binary.BigEndian.Uint32(digest[0:4])
	h2 := binary.BigEndian.Uint32(digest[4:8])
	if h2 == 0 {
		h2 = 1
	}

	modulus := uint64(bitCount)
	positions := make([]int, hashCount)
	for index := 0; index < hashCount; index++ {
		position := (uint64(h1) + uint64(index)*uint64(h2)) % modulus
		positions[index] = int(position)
	}
	return positions
}
