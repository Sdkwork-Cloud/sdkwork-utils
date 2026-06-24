import { round } from "./number.js";
import { parseDatetime } from "./datetime.js";

function separators(locale: string): { decimal: string; grouping: string } {
  if (locale.toLowerCase() === "de-de") {
    return { decimal: ",", grouping: "." };
  }
  return { decimal: ".", grouping: "," };
}

export function formatNumberLocale(value: number, locale: string, decimals = 0): string {
  const rounded = round(Math.abs(value), decimals);
  const { decimal, grouping } = separators(locale);
  const [integerPart, fractionPart = ""] = rounded.toFixed(decimals).split(".");
  const grouped = integerPart.replace(/\B(?=(\d{3})+(?!\d))/g, grouping);
  const formatted =
    decimals > 0 ? `${grouped}${decimal}${fractionPart.padEnd(decimals, "0")}` : grouped;
  return value < 0 ? `-${formatted}` : formatted;
}

export function formatDatetimeLocale(value: Date, locale: string): string {
  const normalized = locale.toLowerCase();
  const year = value.getUTCFullYear();
  const month = String(value.getUTCMonth() + 1).padStart(2, "0");
  const day = String(value.getUTCDate()).padStart(2, "0");
  const hour = String(value.getUTCHours()).padStart(2, "0");
  const minute = String(value.getUTCMinutes()).padStart(2, "0");
  if (normalized.startsWith("de")) {
    return `${day}.${month}.${year} ${hour}:${minute}`;
  }
  if (normalized.startsWith("zh")) {
    return `${year}-${month}-${day} ${hour}:${minute}`;
  }
  return `${month}/${day}/${year} ${hour}:${minute}`;
}

export function formatDatetimeLocaleStr(value: string, locale: string): string | null {
  const parsed = parseDatetime(value);
  return parsed ? formatDatetimeLocale(parsed, locale) : null;
}

export function parseNumberLocale(input: string, locale: string): number | null {
  const trimmed = input.trim();
  if (!trimmed) {
    return null;
  }
  const { decimal, grouping } = separators(locale);
  const normalized = trimmed.replaceAll(grouping, "").replace(decimal, ".");
  const parsed = Number.parseFloat(normalized);
  return Number.isFinite(parsed) ? parsed : null;
}
