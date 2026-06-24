export function clamp(value: number, min: number, max: number): number {
  return Math.min(max, Math.max(min, value));
}

export function round(value: number, decimals = 0): number {
  const factor = 10 ** decimals;
  return Math.round(value * factor) / factor;
}

export function formatNumber(value: number, decimals = 0): string {
  return value.toFixed(decimals);
}

export function parseNumber(value: string): number | null {
  const parsed = Number.parseFloat(value.trim());
  return Number.isFinite(parsed) ? parsed : null;
}

export function isInteger(value: number): boolean {
  return Number.isFinite(value) && Number.isInteger(value);
}

export function parseInt(value: string): number | null {
  const trimmed = value.trim();
  if (!/^-?\d+$/.test(trimmed)) {
    return null;
  }
  const parsed = Number.parseInt(trimmed, 10);
  return Number.isSafeInteger(parsed) ? parsed : null;
}

export function percentFormat(value: number, decimals = 0): string {
  return `${formatNumber(value * 100, decimals)}%`;
}

export function inRange(value: number, min: number, max: number): boolean {
  return value >= min && value <= max;
}

export function abs(value: number): number {
  return Math.abs(value);
}
