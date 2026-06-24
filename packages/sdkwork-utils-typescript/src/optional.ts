import { isBlank } from "./string.js";

export function coalesce(...values: Array<string | null | undefined>): string | undefined {
  for (const value of values) {
    if (!isBlank(value)) {
      return value!.trim();
    }
  }
  return undefined;
}

export function defaultIfBlank(
  value: string | null | undefined,
  defaultValue: string,
): string {
  return isBlank(value) ? defaultValue : value!.trim();
}
