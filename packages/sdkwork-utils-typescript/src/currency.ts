import { formatNumberLocale } from "./i18n.js";
import { round } from "./number.js";

const KNOWN: Record<string, { exponent: number; symbol: string }> = {
  USD: { exponent: 2, symbol: '$' },
  EUR: { exponent: 2, symbol: '\u20AC' },
  GBP: { exponent: 2, symbol: '\u00A3' },
  CNY: { exponent: 2, symbol: '\u00A5' },
  JPY: { exponent: 0, symbol: '\u00A5' },
  KRW: { exponent: 0, symbol: '\u20A9' },
  HKD: { exponent: 2, symbol: 'HK$' },
  TWD: { exponent: 2, symbol: 'NT$' },
  CHF: { exponent: 2, symbol: 'CHF' },
  CAD: { exponent: 2, symbol: 'CA$' },
  AUD: { exponent: 2, symbol: 'A$' },
  INR: { exponent: 2, symbol: '\u20B9' },
  BHD: { exponent: 3, symbol: 'BHD' },
  KWD: { exponent: 3, symbol: 'KWD' },
};

function lookup(code: string) {
  const trimmed = code.trim();
  if (!/^[A-Z]{3}$/.test(trimmed)) {
    return undefined;
  }
  return KNOWN[trimmed];
}

export function isCurrencyCode(value: string): boolean {
  return lookup(value) !== undefined;
}

export function minorUnitExponent(code: string): number | null {
  return lookup(code)?.exponent ?? null;
}

export function toMinorUnits(amount: number, code: string): number | null {
  const meta = lookup(code);
  if (!meta) {
    return null;
  }
  const factor = 10 ** meta.exponent;
  return Math.trunc(round(amount * factor, 0));
}

export function fromMinorUnits(minor: number, code: string): number | null {
  const meta = lookup(code);
  if (!meta) {
    return null;
  }
  const factor = 10 ** meta.exponent;
  return minor / factor;
}

function suffixLocale(locale: string): boolean {
  const normalized = locale.toLowerCase();
  return (
    normalized.startsWith("de") ||
    normalized.startsWith("fr") ||
    normalized.startsWith("it") ||
    normalized.startsWith("es")
  );
}

export function formatCurrency(amount: number, code: string, locale: string): string | null {
  const meta = lookup(code);
  if (!meta) {
    return null;
  }
  const formatted = formatNumberLocale(amount, locale, meta.exponent);
  return suffixLocale(locale) ? `${formatted} ${meta.symbol}` : `${meta.symbol}${formatted}`;
}
