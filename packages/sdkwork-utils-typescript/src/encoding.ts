import {
  base64Decode as decodeBase64,
  base64Encode as encodeBase64,
  base64UrlDecode as decodeBase64Url,
  base64UrlEncode as encodeBase64Url,
  hexDecode as decodeHex,
  hexEncode as encodeHex,
  toUtf8,
} from "./runtime/binary.js";

export function base64Encode(value: string | Uint8Array): string {
  const bytes = typeof value === "string" ? toUtf8(value) : value;
  return encodeBase64(bytes);
}

export function base64Decode(value: string): Uint8Array | null {
  return decodeBase64(value);
}

export function hexEncode(value: Uint8Array): string {
  return encodeHex(value);
}

export function hexDecode(value: string): Uint8Array | null {
  return decodeHex(value);
}

export function urlEncode(value: string): string {
  return encodeURIComponent(value);
}

export function urlDecode(value: string): string | null {
  try {
    return decodeURIComponent(value);
  } catch {
    return null;
  }
}

export function base64UrlEncode(value: string | Uint8Array): string {
  const bytes = typeof value === "string" ? toUtf8(value) : value;
  return encodeBase64Url(bytes);
}

export function base64UrlDecode(value: string): Uint8Array | null {
  return decodeBase64Url(value);
}
