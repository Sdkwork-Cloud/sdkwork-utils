import { hmacSha256Hex, sha256Hex } from "./runtime/sha256.js";

export function sha256Hash(value: string | Uint8Array): string {
  return sha256Hex(value);
}

export function hmacSha256(value: string | Uint8Array, secret: string | Uint8Array): string {
  return hmacSha256Hex(value, secret);
}

export function secureCompare(left: string, right: string): boolean {
  if (left.length !== right.length) {
    return false;
  }
  let result = 0;
  for (let index = 0; index < left.length; index += 1) {
    result |= left.charCodeAt(index) ^ right.charCodeAt(index);
  }
  return result === 0;
}
