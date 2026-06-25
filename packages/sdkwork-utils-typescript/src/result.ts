export type ResultValue<T> =
  | { ok: true; value: T }
  | { ok: false; error: string };

export function ok<T>(value: T): ResultValue<T> {
  return { ok: true, value };
}

export function err<T = never>(message: string): ResultValue<T> {
  return { ok: false, error: message };
}

export function isOk<T>(result: ResultValue<T>): result is { ok: true; value: T } {
  return result.ok;
}

export function isErr<T>(result: ResultValue<T>): result is { ok: false; error: string } {
  return !result.ok;
}

export function unwrapOr<T>(result: ResultValue<T>, defaultValue: T): T {
  return result.ok ? result.value : defaultValue;
}

export function map<T, U>(result: ResultValue<T>, mapper: (value: T) => U): ResultValue<U> {
  return result.ok ? ok(mapper(result.value)) : err(result.error);
}
