const WORD_SPLIT = /[^a-zA-Z0-9]+/;
const CAMEL_BOUNDARY = /([a-z0-9])([A-Z])|([A-Z]+)([A-Z][a-z])/g;

function camelParts(input: string): string[] {
  const normalized = input.trim().replace(CAMEL_BOUNDARY, "$1$3 $2$4");
  return normalized
    .split(WORD_SPLIT)
    .filter(Boolean)
    .map((part) => part.toLowerCase());
}

export function isBlank(value: string | null | undefined): boolean {
  return value == null || value.trim().length === 0;
}

export function trim(value: string): string {
  return value.trim();
}

export function truncate(
  value: string,
  maxLen: number,
  suffix = "...",
): string {
  if (maxLen <= 0) {
    return "";
  }
  if (value.length <= maxLen) {
    return value;
  }
  if (suffix.length >= maxLen) {
    return suffix.slice(0, maxLen);
  }
  return `${value.slice(0, maxLen - suffix.length)}${suffix}`;
}

export function capitalize(value: string): string {
  if (value.length === 0) {
    return "";
  }
  return value.charAt(0).toUpperCase() + value.slice(1).toLowerCase();
}

export function camelCase(value: string): string {
  const parts = camelParts(value);
  if (parts.length === 0) {
    return "";
  }
  return parts[0] + parts.slice(1).map(capitalize).join("");
}

export function snakeCase(value: string): string {
  return camelParts(value).join("_");
}

export function kebabCase(value: string): string {
  return camelParts(value).join("-");
}

export function slugify(value: string): string {
  return kebabCase(value)
    .replace(/[^a-z0-9-]/g, "")
    .replace(/^-+|-+$/g, "");
}

export function mask(
  value: string,
  visibleStart: number,
  visibleEnd: number,
  maskChar = "*",
): string {
  if (visibleStart + visibleEnd >= value.length) {
    return value;
  }
  return (
    value.slice(0, visibleStart) +
    maskChar.repeat(value.length - visibleStart - visibleEnd) +
    value.slice(value.length - visibleEnd)
  );
}

export function padStart(value: string, targetLen: number, padChar = " "): string {
  if (value.length >= targetLen) {
    return value;
  }
  return padChar.repeat(targetLen - value.length) + value;
}

export function padEnd(value: string, targetLen: number, padChar = " "): string {
  if (value.length >= targetLen) {
    return value;
  }
  return value + padChar.repeat(targetLen - value.length);
}

export function startsWith(value: string, prefix: string): boolean {
  return value.startsWith(prefix);
}

export function endsWith(value: string, suffix: string): boolean {
  return value.endsWith(suffix);
}

export function contains(value: string, substring: string): boolean {
  return value.includes(substring);
}

export function replaceAll(value: string, search: string, replacement: string): string {
  return value.split(search).join(replacement);
}

export function split(value: string, delimiter: string, trimParts = true): string[] {
  return value
    .split(delimiter)
    .map((part) => (trimParts ? part.trim() : part))
    .filter((part) => !trimParts || part.length > 0);
}

export function join(parts: string[], separator: string): string {
  return parts.join(separator);
}

export function repeat(value: string, count: number): string {
  if (count < 0) {
    throw new RangeError("repeat count must be >= 0");
  }
  return value.repeat(count);
}

export function normalizeWhitespace(value: string): string {
  return value.trim().split(/\s+/).filter(Boolean).join(" ");
}

const TEMPLATE_KEY = /\{([a-zA-Z_][a-zA-Z0-9_]*)\}/g;

export function template(
  pattern: string,
  values: Record<string, string>,
): string {
  return pattern.replace(TEMPLATE_KEY, (match, key: string) => values[key] ?? match);
}
