const textEncoder = new TextEncoder();

export function toUtf8(value: string): Uint8Array {
  return textEncoder.encode(value);
}

export function readUInt32BE(bytes: Uint8Array, offset: number): number {
  return (
    ((bytes[offset] << 24) |
      (bytes[offset + 1] << 16) |
      (bytes[offset + 2] << 8) |
      bytes[offset + 3]) >>>
    0
  );
}

const HEX = "0123456789abcdef";

export function hexEncode(bytes: Uint8Array): string {
  let result = "";
  for (let index = 0; index < bytes.length; index += 1) {
    const byte = bytes[index];
    result += HEX[byte >> 4];
    result += HEX[byte & 0x0f];
  }
  return result;
}

export function hexDecode(value: string): Uint8Array | null {
  const trimmed = value.trim();
  if (trimmed.length % 2 !== 0 || !/^[0-9a-fA-F]*$/.test(trimmed)) {
    return null;
  }

  const bytes = new Uint8Array(trimmed.length / 2);
  for (let index = 0; index < bytes.length; index += 1) {
    const start = index * 2;
    bytes[index] = Number.parseInt(trimmed.slice(start, start + 2), 16);
  }
  return bytes;
}

function bytesToBinary(bytes: Uint8Array): string {
  let binary = "";
  for (let index = 0; index < bytes.length; index += 1) {
    binary += String.fromCharCode(bytes[index]);
  }
  return binary;
}

function binaryToBytes(binary: string): Uint8Array {
  const bytes = new Uint8Array(binary.length);
  for (let index = 0; index < binary.length; index += 1) {
    bytes[index] = binary.charCodeAt(index);
  }
  return bytes;
}

export function base64Encode(bytes: Uint8Array): string {
  return btoa(bytesToBinary(bytes));
}

export function base64Decode(value: string): Uint8Array | null {
  try {
    return binaryToBytes(atob(value.trim()));
  } catch {
    return null;
  }
}

export function base64UrlEncode(bytes: Uint8Array): string {
  return base64Encode(bytes).replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/g, "");
}

export function base64UrlDecode(value: string): Uint8Array | null {
  const trimmed = value.trim();
  if (trimmed.length === 0) {
    return new Uint8Array();
  }

  const normalized = trimmed.replace(/-/g, "+").replace(/_/g, "/");
  const padding = normalized.length % 4 === 0 ? "" : "=".repeat(4 - (normalized.length % 4));
  return base64Decode(`${normalized}${padding}`);
}
