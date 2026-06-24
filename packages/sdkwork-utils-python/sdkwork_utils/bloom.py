from __future__ import annotations

import hashlib
import math
from dataclasses import dataclass

LN2 = math.log(2)
MIN_BIT_COUNT = 64


@dataclass
class BloomFilter:
    bit_count: int
    hash_count: int
    bits: bytearray


def estimate_bit_count(expected_items: int, false_positive_rate: float) -> int:
    if expected_items <= 0:
        return MIN_BIT_COUNT
    rate = min(0.25, max(0.0001, false_positive_rate))
    size = (-expected_items * math.log(rate)) / (LN2 * LN2)
    return max(MIN_BIT_COUNT, math.ceil(size))


def estimate_hash_count(expected_items: int, bit_count: int) -> int:
    if expected_items <= 0:
        return 1
    count = (bit_count / expected_items) * LN2
    return max(1, round(count))


def create(expected_items: int, false_positive_rate: float) -> BloomFilter:
    bit_count = estimate_bit_count(expected_items, false_positive_rate)
    hash_count = estimate_hash_count(expected_items, bit_count)
    return BloomFilter(bit_count, hash_count, bytearray(math.ceil(bit_count / 8)))


def add(filter: BloomFilter, value: str) -> None:
    for index in _hash_positions(value.encode("utf-8"), filter.bit_count, filter.hash_count):
        byte_index = index // 8
        bit_index = index % 8
        filter.bits[byte_index] |= 1 << bit_index


def might_contain(filter: BloomFilter, value: str) -> bool:
    return all(
        (filter.bits[index // 8] & (1 << (index % 8))) != 0
        for index in _hash_positions(value.encode("utf-8"), filter.bit_count, filter.hash_count)
    )


def _hash_positions(value: bytes, bit_count: int, hash_count: int) -> list[int]:
    digest = hashlib.sha256(value).digest()
    h1 = int.from_bytes(digest[0:4], "big")
    h2 = int.from_bytes(digest[4:8], "big") or 1
    modulus = bit_count
    return [((h1 + index * h2) % modulus) for index in range(hash_count)]
