import { randomBytes, randomUuid } from "./runtime/random.js";

const ALPHANUMERIC =
  "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

export { randomBytes };

export function uuid(): string {
  return randomUuid();
}

export function randomString(length: number): string {
  const bytes = randomBytes(length);
  let result = "";
  for (const byte of bytes) {
    result += ALPHANUMERIC.charAt(byte % ALPHANUMERIC.length);
  }
  return result;
}
