export function unique<T>(items: T[]): T[] {
  return [...new Set(items)];
}

export function chunk<T>(items: T[], size: number): T[][] {
  if (size <= 0) {
    return [];
  }
  const result: T[][] = [];
  for (let index = 0; index < items.length; index += size) {
    result.push(items.slice(index, index + size));
  }
  return result;
}

export function groupBy<T, K extends PropertyKey>(
  items: T[],
  keyFn: (item: T) => K,
): Record<K, T[]> {
  const groups = {} as Record<K, T[]>;
  for (const item of items) {
    const key = keyFn(item);
    (groups[key] ??= []).push(item);
  }
  return groups;
}

export function flatten<T>(items: T[][]): T[] {
  return items.flat();
}

export function compact<T>(items: Array<T | null | undefined>): T[] {
  return items.filter((item): item is T => item != null);
}

export function first<T>(items: T[]): T | null {
  return items.length > 0 ? items[0] : null;
}

export function last<T>(items: T[]): T | null {
  return items.length > 0 ? items[items.length - 1] : null;
}

export function sortBy<T>(items: T[], keyFn: (item: T) => string | number): T[] {
  return [...items].sort((left, right) => {
    const leftKey = keyFn(left);
    const rightKey = keyFn(right);
    if (leftKey < rightKey) {
      return -1;
    }
    if (leftKey > rightKey) {
      return 1;
    }
    return 0;
  });
}

export function keyBy<T, K extends PropertyKey>(
  items: T[],
  keyFn: (item: T) => K,
): Record<K, T> {
  const result = {} as Record<K, T>;
  for (const item of items) {
    result[keyFn(item)] = item;
  }
  return result;
}

export function filter<T>(items: T[], predicate: (item: T) => boolean): T[] {
  return items.filter(predicate);
}

export function find<T>(items: T[], predicate: (item: T) => boolean): T | null {
  const found = items.find(predicate);
  return found ?? null;
}
