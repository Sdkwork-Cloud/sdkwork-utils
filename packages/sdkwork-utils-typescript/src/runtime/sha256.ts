import { hexEncode, toUtf8 } from "./binary.js";

const K = new Uint32Array([
  0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
  0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
  0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
  0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
  0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
  0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
  0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
  0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2,
]);

const BLOCK_SIZE = 64;

function rotr(value: number, shift: number): number {
  return (value >>> shift) | (value << (32 - shift));
}

function uint32At(values: Uint32Array, index: number): number {
  const value = values[index];
  if (value === undefined) {
    throw new RangeError(`Uint32 index ${index} is out of range.`);
  }
  return value;
}

function sha256Block(state: Uint32Array, block: Uint8Array, offset: number): void {
  const words = new Uint32Array(64);
  const view = new DataView(block.buffer, block.byteOffset, block.byteLength);
  for (let index = 0; index < 16; index += 1) {
    const start = offset + index * 4;
    words[index] = view.getUint32(start, false);
  }

  for (let index = 16; index < 64; index += 1) {
    const word15 = uint32At(words, index - 15);
    const word2 = uint32At(words, index - 2);
    const s0 = rotr(word15, 7) ^ rotr(word15, 18) ^ (word15 >>> 3);
    const s1 = rotr(word2, 17) ^ rotr(word2, 19) ^ (word2 >>> 10);
    words[index] = (uint32At(words, index - 16) + s0 + uint32At(words, index - 7) + s1) >>> 0;
  }

  let a = uint32At(state, 0);
  let b = uint32At(state, 1);
  let c = uint32At(state, 2);
  let d = uint32At(state, 3);
  let e = uint32At(state, 4);
  let f = uint32At(state, 5);
  let g = uint32At(state, 6);
  let h = uint32At(state, 7);

  for (let index = 0; index < 64; index += 1) {
    const s1 = rotr(e, 6) ^ rotr(e, 11) ^ rotr(e, 25);
    const ch = (e & f) ^ (~e & g);
    const temp1 = (h + s1 + ch + uint32At(K, index) + uint32At(words, index)) >>> 0;
    const s0 = rotr(a, 2) ^ rotr(a, 13) ^ rotr(a, 22);
    const maj = (a & b) ^ (a & c) ^ (b & c);
    const temp2 = (s0 + maj) >>> 0;

    h = g;
    g = f;
    f = e;
    e = (d + temp1) >>> 0;
    d = c;
    c = b;
    b = a;
    a = (temp1 + temp2) >>> 0;
  }

  state[0] = (uint32At(state, 0) + a) >>> 0;
  state[1] = (uint32At(state, 1) + b) >>> 0;
  state[2] = (uint32At(state, 2) + c) >>> 0;
  state[3] = (uint32At(state, 3) + d) >>> 0;
  state[4] = (uint32At(state, 4) + e) >>> 0;
  state[5] = (uint32At(state, 5) + f) >>> 0;
  state[6] = (uint32At(state, 6) + g) >>> 0;
  state[7] = (uint32At(state, 7) + h) >>> 0;
}

export function sha256Digest(value: Uint8Array): Uint8Array {
  const bitLength = value.length * 8;
  const paddingLength = ((BLOCK_SIZE - ((value.length + 9) % BLOCK_SIZE)) % BLOCK_SIZE) + 9;
  const padded = new Uint8Array(value.length + paddingLength);
  padded.set(value);
  padded[value.length] = 0x80;

  const view = new DataView(padded.buffer);
  view.setUint32(padded.length - 4, bitLength >>> 0, false);
  view.setUint32(padded.length - 8, Math.floor(bitLength / 0x1_0000_0000), false);

  const state = new Uint32Array([
    0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a, 0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19,
  ]);

  for (let offset = 0; offset < padded.length; offset += BLOCK_SIZE) {
    sha256Block(state, padded, offset);
  }

  const digest = new Uint8Array(32);
  const digestView = new DataView(digest.buffer);
  for (let index = 0; index < state.length; index += 1) {
    digestView.setUint32(index * 4, uint32At(state, index), false);
  }
  return digest;
}

const SHA256_INITIAL_STATE = new Uint32Array([
  0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a, 0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19,
]);

export class Sha256Hasher {
  private readonly state = new Uint32Array(SHA256_INITIAL_STATE);
  private readonly buffer = new Uint8Array(BLOCK_SIZE);
  private bufferLength = 0;
  private totalLength = 0;

  update(chunk: Uint8Array): void {
    if (chunk.length === 0) {
      return;
    }

    this.totalLength += chunk.length;
    let offset = 0;

    if (this.bufferLength > 0) {
      const remaining = BLOCK_SIZE - this.bufferLength;
      if (chunk.length < remaining) {
        this.buffer.set(chunk, this.bufferLength);
        this.bufferLength += chunk.length;
        return;
      }

      this.buffer.set(chunk.subarray(0, remaining), this.bufferLength);
      sha256Block(this.state, this.buffer, 0);
      offset = remaining;
      this.bufferLength = 0;
    }

    while (offset + BLOCK_SIZE <= chunk.length) {
      sha256Block(this.state, chunk, offset);
      offset += BLOCK_SIZE;
    }

    if (offset < chunk.length) {
      const tail = chunk.subarray(offset);
      this.buffer.set(tail);
      this.bufferLength = tail.length;
    }
  }

  digest(): Uint8Array {
    const bitLength = this.totalLength * 8;
    const paddingLength = ((BLOCK_SIZE - ((this.bufferLength + 9) % BLOCK_SIZE)) % BLOCK_SIZE) + 9;
    const padded = new Uint8Array(this.bufferLength + paddingLength);
    padded.set(this.buffer.subarray(0, this.bufferLength));
    padded[this.bufferLength] = 0x80;

    const view = new DataView(padded.buffer);
    view.setUint32(padded.length - 4, bitLength >>> 0, false);
    view.setUint32(padded.length - 8, Math.floor(bitLength / 0x1_0000_0000), false);

    for (let offset = 0; offset < padded.length; offset += BLOCK_SIZE) {
      sha256Block(this.state, padded, offset);
    }

    const digest = new Uint8Array(32);
    const digestView = new DataView(digest.buffer);
    for (let index = 0; index < this.state.length; index += 1) {
      digestView.setUint32(index * 4, uint32At(this.state, index), false);
    }
    return digest;
  }
}

function concatBytes(left: Uint8Array, right: Uint8Array): Uint8Array {
  const combined = new Uint8Array(left.length + right.length);
  combined.set(left);
  combined.set(right, left.length);
  return combined;
}

function normalizeHmacKey(key: Uint8Array): Uint8Array {
  const normalized = key.length > BLOCK_SIZE ? sha256Digest(key) : key;
  if (normalized.length === BLOCK_SIZE) {
    return normalized;
  }

  const padded = new Uint8Array(BLOCK_SIZE);
  padded.set(normalized);
  return padded;
}

export function hmacSha256Digest(value: Uint8Array, secret: Uint8Array): Uint8Array {
  const key = normalizeHmacKey(secret);
  const outer = new Uint8Array(BLOCK_SIZE);
  const inner = new Uint8Array(BLOCK_SIZE);

  for (const [index, byte] of key.entries()) {
    outer[index] = byte ^ 0x5c;
    inner[index] = byte ^ 0x36;
  }

  return sha256Digest(concatBytes(outer, sha256Digest(concatBytes(inner, value))));
}

export function sha256Hex(value: string | Uint8Array): string {
  const bytes = typeof value === "string" ? toUtf8(value) : value;
  return hexEncode(sha256Digest(bytes));
}

export function hmacSha256Hex(value: string | Uint8Array, secret: string | Uint8Array): string {
  const message = typeof value === "string" ? toUtf8(value) : value;
  const key = typeof secret === "string" ? toUtf8(secret) : secret;
  return hexEncode(hmacSha256Digest(message, key));
}
