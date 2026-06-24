import { readUInt32BE, toUtf8 } from "./runtime/binary.js";
import { sha256Digest } from "./runtime/sha256.js";

const LN2 = Math.LN2;
const MIN_BIT_COUNT = 64;

export class BloomFilter {
  constructor(
    public readonly bitCount: number,
    public readonly hashCount: number,
    public readonly bits: Uint8Array,
  ) {}
}

export function estimateBitCount(expectedItems: number, falsePositiveRate: number): number {
  if (expectedItems <= 0) {
    return MIN_BIT_COUNT;
  }
  const rate = Math.min(0.25, Math.max(0.0001, falsePositiveRate));
  const size = (-expectedItems * Math.log(rate)) / (LN2 * LN2);
  return Math.max(MIN_BIT_COUNT, Math.ceil(size));
}

export function estimateHashCount(expectedItems: number, bitCount: number): number {
  if (expectedItems <= 0) {
    return 1;
  }
  const count = (bitCount / expectedItems) * LN2;
  return Math.max(1, Math.round(count));
}

export function create(expectedItems: number, falsePositiveRate: number): BloomFilter {
  const bitCount = estimateBitCount(expectedItems, falsePositiveRate);
  const hashCount = estimateHashCount(expectedItems, bitCount);
  return new BloomFilter(bitCount, hashCount, new Uint8Array(Math.ceil(bitCount / 8)));
}

export function add(filter: BloomFilter, value: string): void {
  for (const index of hashPositions(toUtf8(value), filter.bitCount, filter.hashCount)) {
    const byteIndex = Math.floor(index / 8);
    const bitIndex = index % 8;
    filter.bits[byteIndex] |= 1 << bitIndex;
  }
}

export function mightContain(filter: BloomFilter, value: string): boolean {
  return hashPositions(toUtf8(value), filter.bitCount, filter.hashCount).every((index) => {
    const byteIndex = Math.floor(index / 8);
    const bitIndex = index % 8;
    return (filter.bits[byteIndex] & (1 << bitIndex)) !== 0;
  });
}

function hashPositions(value: Uint8Array, bitCount: number, hashCount: number): number[] {
  const digest = sha256Digest(value);
  const h1 = readUInt32BE(digest, 0);
  let h2 = readUInt32BE(digest, 4);
  if (h2 === 0) {
    h2 = 1;
  }

  const positions: number[] = [];
  for (let index = 0; index < hashCount; index += 1) {
    const position = Number((BigInt(h1) + BigInt(index) * BigInt(h2)) % BigInt(bitCount));
    positions.push(position);
  }
  return positions;
}
