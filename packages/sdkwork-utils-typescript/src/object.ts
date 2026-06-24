export type JsonObject = Record<string, unknown>;

function isObject(value: unknown): value is JsonObject {
  return typeof value === "object" && value !== null && !Array.isArray(value);
}

export function pick(source: JsonObject, keys: string[]): JsonObject {
  const result: JsonObject = {};
  for (const key of keys) {
    if (key in source) {
      result[key] = source[key];
    }
  }
  return result;
}

export function omit(source: JsonObject, keys: string[]): JsonObject {
  const blocked = new Set(keys);
  const result: JsonObject = {};
  for (const [key, value] of Object.entries(source)) {
    if (!blocked.has(key)) {
      result[key] = value;
    }
  }
  return result;
}

export function getPath(source: unknown, path: string): unknown {
  const segments = path.split(".").filter(Boolean);
  let current: unknown = source;
  for (const segment of segments) {
    if (!isObject(current) || !(segment in current)) {
      return undefined;
    }
    current = current[segment];
  }
  return current;
}

export function setPath(source: JsonObject, path: string, value: unknown): JsonObject {
  const segments = path.split(".").filter(Boolean);
  if (segments.length === 0) {
    return source;
  }

  const root: JsonObject = { ...source };
  let current: JsonObject = root;
  for (let index = 0; index < segments.length - 1; index += 1) {
    const segment = segments[index];
    const next = current[segment];
    current[segment] = isObject(next) ? { ...next } : {};
    current = current[segment] as JsonObject;
  }
  current[segments[segments.length - 1]] = value;
  return root;
}

export function hasPath(source: unknown, path: string): boolean {
  const value = getPath(source, path);
  return value !== undefined && value !== null;
}

export function shallowMerge(base: unknown, overlay: unknown): unknown {
  if (isObject(base) && isObject(overlay)) {
    return { ...base, ...overlay };
  }
  return overlay;
}

export function deepMerge(base: unknown, overlay: unknown): unknown {
  if (isObject(base) && isObject(overlay)) {
    const result: JsonObject = { ...base };
    for (const [key, value] of Object.entries(overlay)) {
      result[key] = key in result ? deepMerge(result[key], value) : value;
    }
    return result;
  }
  return overlay;
}

export function compactObject(source: JsonObject): JsonObject {
  const result: JsonObject = {};
  for (const [key, value] of Object.entries(source)) {
    if (value !== null && value !== undefined) {
      result[key] = value;
    }
  }
  return result;
}

export function keys(source: JsonObject): string[] {
  return Object.keys(source);
}

export function values(source: JsonObject): unknown[] {
  return Object.values(source);
}
