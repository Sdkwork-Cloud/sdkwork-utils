use sha2::{Digest, Sha256};
use std::f64::consts::LN_2;

const MIN_BIT_COUNT: usize = 64;

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct BloomFilter {
    bits: Vec<u8>,
    bit_count: usize,
    hash_count: usize,
}

pub fn estimate_bit_count(expected_items: usize, false_positive_rate: f64) -> usize {
    if expected_items == 0 {
        return MIN_BIT_COUNT;
    }
    let rate = false_positive_rate.clamp(0.000_1, 0.25);
    let size = -(expected_items as f64) * rate.ln() / (LN_2 * LN_2);
    (size.ceil() as usize).max(MIN_BIT_COUNT)
}

pub fn estimate_hash_count(expected_items: usize, bit_count: usize) -> usize {
    if expected_items == 0 {
        return 1;
    }
    let count = (bit_count as f64 / expected_items as f64) * LN_2;
    count.round().max(1.0) as usize
}

pub fn create(expected_items: usize, false_positive_rate: f64) -> BloomFilter {
    let bit_count = estimate_bit_count(expected_items, false_positive_rate);
    let hash_count = estimate_hash_count(expected_items, bit_count);
    let byte_len = bit_count.div_ceil(8);
    BloomFilter {
        bits: vec![0; byte_len],
        bit_count,
        hash_count,
    }
}

pub fn add(filter: &mut BloomFilter, value: &str) {
    for index in hash_positions(value.as_bytes(), filter.bit_count, filter.hash_count) {
        let byte_index = index / 8;
        let bit_index = index % 8;
        filter.bits[byte_index] |= 1 << bit_index;
    }
}

pub fn might_contain(filter: &BloomFilter, value: &str) -> bool {
    hash_positions(value.as_bytes(), filter.bit_count, filter.hash_count)
        .iter()
        .all(|index| {
            let byte_index = index / 8;
            let bit_index = index % 8;
            (filter.bits[byte_index] & (1 << bit_index)) != 0
        })
}

pub fn bit_count(filter: &BloomFilter) -> usize {
    filter.bit_count
}

pub fn hash_count(filter: &BloomFilter) -> usize {
    filter.hash_count
}

fn hash_positions(value: &[u8], bit_count: usize, hash_count: usize) -> Vec<usize> {
    let digest = Sha256::digest(value);
    let h1 = read_u32(&digest[0..4]) as u64;
    let mut h2 = read_u32(&digest[4..8]) as u64;
    if h2 == 0 {
        h2 = 1;
    }

    let modulus = bit_count as u64;
    (0..hash_count)
        .map(|index| ((h1.wrapping_add(index as u64 * h2)) % modulus) as usize)
        .collect()
}

fn read_u32(bytes: &[u8]) -> u32 {
    u32::from_be_bytes([bytes[0], bytes[1], bytes[2], bytes[3]])
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn bloom_helpers() {
        assert_eq!(estimate_bit_count(128, 0.01), 1227);
        assert_eq!(estimate_hash_count(128, 1227), 7);

        let mut filter = create(128, 0.01);
        add(&mut filter, "hello");
        add(&mut filter, "world");
        assert!(might_contain(&filter, "hello"));
        assert!(might_contain(&filter, "world"));
        assert!(!might_contain(&filter, "__sdkwork_bloom_absent__"));
    }
}
