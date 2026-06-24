import { randomBytes, randomUuid } from "./runtime/random.js";

const ALPHANUMERIC =
  "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

export function uuid(): string {
  return randomUuid();
}

export function randomString(length: number): string {
  const bytes = randomBytes(length);
  let result = "";
  for (let index = 0; index < length; index += 1) {
    result += ALPHANUMERIC[bytes[index] % ALPHANUMERIC.length];
  }
  return result;
}
