export const DEFAULT_PATTERN = "iso8601";

export function now(): Date {
  return new Date();
}

export function formatDatetime(value: Date, pattern = DEFAULT_PATTERN): string {
  if (pattern === DEFAULT_PATTERN) {
    return value.toISOString();
  }
  throw new Error(`Unsupported datetime pattern: ${pattern}`);
}

export function parseDatetime(value: string, pattern = DEFAULT_PATTERN): Date | null {
  if (pattern !== DEFAULT_PATTERN) {
    return null;
  }
  const parsed = Date.parse(value.trim());
  return Number.isNaN(parsed) ? null : new Date(parsed);
}

export function addDays(value: Date, days: number): Date {
  const result = new Date(value.getTime());
  result.setUTCDate(result.getUTCDate() + days);
  return result;
}

export function addHours(value: Date, hours: number): Date {
  return new Date(value.getTime() + hours * 3_600_000);
}

export function addMinutes(value: Date, minutes: number): Date {
  return new Date(value.getTime() + minutes * 60_000);
}

export function diffMillis(earlier: Date, later: Date): number {
  return later.getTime() - earlier.getTime();
}

export function isBefore(first: Date, second: Date): boolean {
  return first.getTime() < second.getTime();
}

export function isAfter(first: Date, second: Date): boolean {
  return first.getTime() > second.getTime();
}

export function startOfDayUtc(value: Date): Date {
  return new Date(
    Date.UTC(value.getUTCFullYear(), value.getUTCMonth(), value.getUTCDate(), 0, 0, 0, 0),
  );
}

export function endOfDayUtc(value: Date): Date {
  return new Date(
    Date.UTC(
      value.getUTCFullYear(),
      value.getUTCMonth(),
      value.getUTCDate(),
      23,
      59,
      59,
      999,
    ),
  );
}

export function toUnixMillis(value: Date): number {
  return value.getTime();
}

export function fromUnixMillis(value: number): Date | null {
  if (!Number.isFinite(value)) {
    return null;
  }
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? null : date;
}

export function isSameInstant(first: Date, second: Date): boolean {
  return first.getTime() === second.getTime();
}
