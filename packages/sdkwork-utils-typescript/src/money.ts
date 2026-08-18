import { minorUnitExponent } from "./currency.js";

export type MoneyMode =
  | "symbol"
  | "narrow_symbol"
  | "code"
  | "name"
  | "decimal"
  | "accounting"
  | "compact";

export type MoneySign = "auto" | "always" | "never" | "except_zero";

export type MoneyValue = number | string | null | undefined;

export interface MoneyFormatOptions {
  currency: string;
  locale?: string;
  mode?: MoneyMode;
  minFractionDigits?: number;
  maxFractionDigits?: number;
  sign?: MoneySign;
  useGrouping?: boolean;
}

const MONEY_MODES: readonly MoneyMode[] = [
  "symbol",
  "narrow_symbol",
  "code",
  "name",
  "decimal",
  "accounting",
  "compact",
];

const MONEY_SIGNS: readonly MoneySign[] = ["auto", "always", "never", "except_zero"];

const CURRENCY_SYMBOLS: Record<string, string> = {
  USD: "$",
  EUR: "\u20AC",
  GBP: "\u00A3",
  CNY: "\u00A5",
  JPY: "\u00A5",
  KRW: "\u20A9",
  HKD: "HK$",
  TWD: "NT$",
  CHF: "CHF",
  CAD: "CA$",
  AUD: "A$",
  INR: "\u20B9",
  BHD: "BHD",
  KWD: "KWD",
};

interface CompactUnit {
  exponent: number;
  unit: string;
}

interface LocaleRules {
  prefix: boolean;
  decimal: string;
  grouping: string;
  nameSpace: boolean;
  compact: readonly CompactUnit[];
}

const DEFAULT_LOCALE_RULES: LocaleRules = {
  prefix: true,
  decimal: ".",
  grouping: ",",
  nameSpace: true,
  compact: [
    { exponent: 12, unit: "T" },
    { exponent: 9, unit: "B" },
    { exponent: 6, unit: "M" },
    { exponent: 3, unit: "K" },
  ],
};

const LOCALE_RULES: Record<string, LocaleRules> = {
  "en-us": DEFAULT_LOCALE_RULES,
  "zh-cn": {
    prefix: true,
    decimal: ".",
    grouping: ",",
    nameSpace: false,
    compact: [
      { exponent: 12, unit: "\u5146" },
      { exponent: 8, unit: "\u4EBF" },
      { exponent: 4, unit: "\u4E07" },
    ],
  },
  "ja-jp": {
    prefix: true,
    decimal: ".",
    grouping: ",",
    nameSpace: false,
    compact: [
      { exponent: 12, unit: "\u5146" },
      { exponent: 8, unit: "\u5104" },
      { exponent: 4, unit: "\u4E07" },
    ],
  },
  "ko-kr": {
    prefix: true,
    decimal: ".",
    grouping: ",",
    nameSpace: false,
    compact: [
      { exponent: 12, unit: "\uC870" },
      { exponent: 8, unit: "\uC5B5" },
      { exponent: 4, unit: "\uB9CC" },
    ],
  },
  "de-de": {
    prefix: false,
    decimal: ",",
    grouping: ".",
    nameSpace: true,
    compact: [
      { exponent: 12, unit: "Bio." },
      { exponent: 9, unit: "Mrd." },
      { exponent: 6, unit: "Mio." },
      { exponent: 3, unit: "Tsd." },
    ],
  },
  "fr-fr": {
    prefix: false,
    decimal: ",",
    grouping: " ",
    nameSpace: true,
    compact: [
      { exponent: 12, unit: "B" },
      { exponent: 9, unit: "Md" },
      { exponent: 6, unit: "M" },
      { exponent: 3, unit: "k" },
    ],
  },
  "it-it": {
    prefix: false,
    decimal: ",",
    grouping: ".",
    nameSpace: true,
    compact: [
      { exponent: 12, unit: "Bio." },
      { exponent: 9, unit: "Mrd." },
      { exponent: 6, unit: "M" },
      { exponent: 3, unit: "k" },
    ],
  },
  "es-es": {
    prefix: false,
    decimal: ",",
    grouping: ".",
    nameSpace: true,
    compact: [
      { exponent: 12, unit: "T" },
      { exponent: 9, unit: "B" },
      { exponent: 6, unit: "M" },
      { exponent: 3, unit: "k" },
    ],
  },
  "ru-ru": {
    prefix: false,
    decimal: ",",
    grouping: " ",
    nameSpace: true,
    compact: [
      { exponent: 12, unit: "\u0442\u0440\u043B\u043D" },
      { exponent: 9, unit: "\u043C\u043B\u0440\u0434" },
      { exponent: 6, unit: "\u043C\u043B\u043D" },
      { exponent: 3, unit: "\u0442\u044B\u0441." },
    ],
  },
};

const DEFAULT_CURRENCY_NAMES: Record<string, string> = {
  USD: "US dollars",
  EUR: "euros",
  GBP: "British pounds",
  CNY: "Chinese yuan",
  JPY: "Japanese yen",
  KRW: "South Korean won",
  HKD: "Hong Kong dollars",
  TWD: "New Taiwan dollars",
  CHF: "Swiss francs",
  CAD: "Canadian dollars",
  AUD: "Australian dollars",
  INR: "Indian rupees",
  BHD: "Bahraini dinars",
  KWD: "Kuwaiti dinars",
};

const CURRENCY_NAMES: Record<string, Record<string, string>> = {
  "en-us": DEFAULT_CURRENCY_NAMES,
  "zh-cn": {
    USD: "\u7F8E\u5143",
    EUR: "\u6B27\u5143",
    GBP: "\u82F1\u9563",
    CNY: "\u4EBA\u6C11\u5E01",
    JPY: "\u65E5\u5143",
    KRW: "\u97E9\u5143",
    HKD: "\u6E2F\u5E01",
    TWD: "\u65B0\u53F0\u5E01",
    CHF: "\u745E\u58EB\u6CD5\u90CE",
    CAD: "\u52A0\u62FF\u5927\u5143",
    AUD: "\u6FB3\u5927\u5229\u4E9A\u5143",
    INR: "\u5370\u5EA6\u5362\u6BD4",
    BHD: "\u5DF4\u6797\u7B2C\u7EB3\u5C14",
    KWD: "\u79D1\u5A01\u7279\u7B2C\u7EB3\u5C14",
  },
  "de-de": {
    USD: "US-Dollar",
    EUR: "Euro",
    GBP: "Britisches Pfund",
    CNY: "Chinesischer Yuan",
    JPY: "Japanischer Yen",
    KRW: "S\u00FCdkoreanischer Won",
    HKD: "Hongkong-Dollar",
    TWD: "Neuer Taiwan-Dollar",
    CHF: "Schweizer Franken",
    CAD: "Kanadischer Dollar",
    AUD: "Australischer Dollar",
    INR: "Indische Rupie",
    BHD: "Bahrainischer Dinar",
    KWD: "Kuwaitischer Dinar",
  },
  "fr-fr": {
    USD: "dollar am\u00E9ricain",
    EUR: "euro",
    GBP: "livre sterling",
    CNY: "yuan chinois",
    JPY: "yen japonais",
    KRW: "won sud-cor\u00E9en",
    HKD: "dollar de Hong Kong",
    TWD: "nouveau dollar de Ta\u00EFwan",
    CHF: "franc suisse",
    CAD: "dollar canadien",
    AUD: "dollar australien",
    INR: "roupie indienne",
    BHD: "dinar bahre\u00EFni",
    KWD: "dinar kowe\u00EFtien",
  },
  "it-it": {
    USD: "dollaro statunitense",
    EUR: "euro",
    GBP: "sterlina britannica",
    CNY: "yuan cinese",
    JPY: "yen giapponese",
    KRW: "won sudcoreano",
    HKD: "dollaro di Hong Kong",
    TWD: "nuovo dollaro taiwanese",
    CHF: "franco svizzero",
    CAD: "dollaro canadese",
    AUD: "dollaro australiano",
    INR: "rupia indiana",
    BHD: "dinaro bahreinita",
    KWD: "dinaro kuwaitiano",
  },
  "es-es": {
    USD: "d\u00F3lar estadounidense",
    EUR: "euro",
    GBP: "libra esterlina",
    CNY: "yuan chino",
    JPY: "yen japon\u00E9s",
    KRW: "won surcoreano",
    HKD: "d\u00F3lar de Hong Kong",
    TWD: "nuevo d\u00F3lar taiwan\u00E9s",
    CHF: "franco suizo",
    CAD: "d\u00F3lar canadiense",
    AUD: "d\u00F3lar australiano",
    INR: "rupia india",
    BHD: "dinar bahrein\u00ED",
    KWD: "dinar kuwait\u00ED",
  },
  "ja-jp": {
    USD: "\u7C73\u30C9\u30EB",
    EUR: "\u30E6\u30FC\u30ED",
    GBP: "\u82F1\u30DD\u30F3\u30C9",
    CNY: "\u4E2D\u56FD\u4EBA\u6C11\u5143",
    JPY: "\u65E5\u672C\u5186",
    KRW: "\u97D3\u56FD\u30A6\u30A9\u30F3",
    HKD: "\u9999\u6E2F\u30C9\u30EB",
    TWD: "\u53F0\u6E7E\u30C9\u30EB",
    CHF: "\u30B9\u30A4\u30B9\u30D5\u30E9\u30F3",
    CAD: "\u30AB\u30CA\u30C0\u30C9\u30EB",
    AUD: "\u30AA\u30FC\u30B9\u30C8\u30E9\u30EA\u30A2\u30C9\u30EB",
    INR: "\u30A4\u30F3\u30C9\u30EB\u30D4\u30FC",
    BHD: "\u30D0\u30FC\u30EC\u30FC\u30F3\u30C7\u30A3\u30FC\u30CA\u30FC\u30EB",
    KWD: "\u30AF\u30A6\u30A7\u30FC\u30C8\u30C7\u30A3\u30CA\u30FC\u30EB",
  },
  "ko-kr": {
    USD: "\uBBF8\uAD6D \uB2EC\uB7EC",
    EUR: "\uC720\uB85C",
    GBP: "\uC601\uAD6D \uD30C\uC6B4\uB4DC",
    CNY: "\uC911\uAD6D \uC704\uC548",
    JPY: "\uC77C\uBCF8 \uC5D4",
    KRW: "\uB300\uD55C\uBBFC\uAD6D \uC6D0",
    HKD: "\uD64D\uCF69 \uB2EC\uB7EC",
    TWD: "\uC2E0 \uB300\uB9CC \uB2EC\uB7EC",
    CHF: "\uC2A4\uC704\uC2A4 \uD504\uB791",
    CAD: "\uCE90\uB098\uB2E4 \uB2EC\uB7EC",
    AUD: "\uD638\uC8FC \uB2EC\uB7EC",
    INR: "\uC778\uB3C4 \uB8E8\uD53C",
    BHD: "\uBC14\uB808\uC778 \uB514\uB098\uB974",
    KWD: "\uCFE0\uC6E8\uC774\uD2B8 \uB514\uB098\uB974",
  },
  "ru-ru": {
    USD: "\u0434\u043E\u043B\u043B\u0430\u0440 \u0421\u0428\u0410",
    EUR: "\u0435\u0432\u0440\u043E",
    GBP: "\u0431\u0440\u0438\u0442\u0430\u043D\u0441\u043A\u0438\u0439 \u0444\u0443\u043D\u0442",
    CNY: "\u043A\u0438\u0442\u0430\u0439\u0441\u043A\u0438\u0439 \u044E\u0430\u043D\u044C",
    JPY: "\u044F\u043F\u043E\u043D\u0441\u043A\u0430\u044F \u0438\u0435\u043D\u0430",
    KRW: "\u044E\u0436\u043D\u043E\u043A\u043E\u0440\u0435\u0439\u0441\u043A\u0430\u044F \u0432\u043E\u043D\u0430",
    HKD: "\u0433\u043E\u043D\u043A\u043E\u043D\u0433\u0441\u043A\u0438\u0439 \u0434\u043E\u043B\u043B\u0430\u0440",
    TWD: "\u043D\u043E\u0432\u044B\u0439 \u0442\u0430\u0439\u0432\u0430\u043D\u044C\u0441\u043A\u0438\u0439 \u0434\u043E\u043B\u043B\u0430\u0440",
    CHF: "\u0448\u0432\u0435\u0439\u0446\u0430\u0440\u0441\u043A\u0438\u0439 \u0444\u0440\u0430\u043D\u043A",
    CAD: "\u043A\u0430\u043D\u0430\u0434\u0441\u043A\u0438\u0439 \u0434\u043E\u043B\u043B\u0430\u0440",
    AUD: "\u0430\u0432\u0441\u0442\u0440\u0430\u043B\u0438\u0439\u0441\u043A\u0438\u0439 \u0434\u043E\u043B\u043B\u0430\u0440",
    INR: "\u0438\u043D\u0434\u0438\u0439\u0441\u043A\u0430\u044F \u0440\u0443\u043F\u0438\u044F",
    BHD: "\u0431\u0430\u0445\u0440\u0435\u0439\u043D\u0441\u043A\u0438\u0439 \u0434\u0438\u043D\u0430\u0440",
    KWD: "\u043A\u0443\u0432\u0435\u0439\u0442\u0441\u043A\u0438\u0439 \u0434\u0438\u043D\u0430\u0440",
  },
};

function lookupCurrency(currency: string | null | undefined): string | null {
  if (typeof currency !== "string") {
    return null;
  }
  const trimmed = currency.trim();
  if (!/^[A-Z]{3}$/.test(trimmed)) {
    return null;
  }
  return trimmed in CURRENCY_SYMBOLS ? trimmed : null;
}

function resolveLocaleRules(locale: string | null | undefined): LocaleRules {
  if (typeof locale !== "string") {
    return DEFAULT_LOCALE_RULES;
  }
  const normalized = locale.trim().toLowerCase();
  const exact = LOCALE_RULES[normalized];
  if (exact) {
    return exact;
  }
  const [language = ""] = normalized.split("-");
  for (const [key, rules] of Object.entries(LOCALE_RULES)) {
    if (key.split("-")[0] === language) {
      return rules;
    }
  }
  return DEFAULT_LOCALE_RULES;
}

function resolveNames(locale: string | null | undefined): Record<string, string> {
  if (typeof locale !== "string") {
    return DEFAULT_CURRENCY_NAMES;
  }
  const normalized = locale.trim().toLowerCase();
  const exact = CURRENCY_NAMES[normalized];
  if (exact) {
    return exact;
  }
  const [language = ""] = normalized.split("-");
  for (const [key, names] of Object.entries(CURRENCY_NAMES)) {
    if (key.split("-")[0] === language) {
      return names;
    }
  }
  return DEFAULT_CURRENCY_NAMES;
}

function expandExponent(value: string): string {
  const exponentIndex = value.indexOf("e");
  if (exponentIndex < 0) {
    return value;
  }
  const mantissa = value.slice(0, exponentIndex);
  const exponent = Number.parseInt(value.slice(exponentIndex + 1), 10);
  const [intPart = "", fracPart = ""] = mantissa.split(".");
  const digits = intPart + fracPart;
  const pointIndex = intPart.length + exponent;
  if (pointIndex <= 0) {
    return `0.${"0".repeat(-pointIndex)}${digits}`;
  }
  if (pointIndex >= digits.length) {
    return `${digits}${"0".repeat(pointIndex - digits.length)}`;
  }
  return `${digits.slice(0, pointIndex)}.${digits.slice(pointIndex)}`;
}

interface ParsedValue {
  negative: boolean;
  isZero: boolean;
  absDecimal: string;
}

function parseValue(value: MoneyValue | null): ParsedValue | null {
  if (value === null || value === undefined) {
    return null;
  }
  if (typeof value === "number") {
    if (!Number.isFinite(value)) {
      return null;
    }
    const negative = value < 0;
    const abs = Math.abs(value);
    if (abs === 0) {
      return { negative: false, isZero: true, absDecimal: "0" };
    }
    return { negative, isZero: false, absDecimal: expandExponent(abs.toString()) };
  }
  if (typeof value !== "string") {
    return null;
  }
  const trimmed = value.trim();
  const match = /^([+-]?)(\d+)(?:\.(\d+))?$/.exec(trimmed);
  if (!match) {
    return null;
  }
  const [, sign = "", integer = "", fracPart = ""] = match;
  const negative = sign === "-";
  const intPart = integer.replace(/^0+(?=\d)/, "");
  const isZero = /^0*$/.test(intPart + fracPart);
  const absDecimal = `${intPart}${fracPart ? `.${fracPart}` : ""}`;
  return { negative, isZero, absDecimal };
}

function roundDecimal(
  absDecimal: string,
  maxFraction: number,
): { intPart: string; fracPart: string } {
  const dotIndex = absDecimal.indexOf(".");
  const intPart = dotIndex < 0 ? absDecimal : absDecimal.slice(0, dotIndex);
  const fracPart = dotIndex < 0 ? "" : absDecimal.slice(dotIndex + 1);
  if (fracPart.length <= maxFraction) {
    return { intPart, fracPart: fracPart.padEnd(maxFraction, "0") };
  }
  const keep = fracPart.slice(0, maxFraction);
  if (fracPart.charAt(maxFraction) >= "5") {
    return incrementDecimal(intPart, keep);
  }
  return { intPart, fracPart: keep };
}

function incrementDecimal(
  intPart: string,
  fracPart: string,
): { intPart: string; fracPart: string } {
  const digits = (intPart + fracPart).split("");
  let index = digits.length - 1;
  while (index >= 0 && digits[index] === "9") {
    digits[index] = "0";
    index -= 1;
  }
  if (index >= 0) {
    const digit = digits[index];
    if (digit === undefined) {
      throw new RangeError("Decimal increment index is out of range.");
    }
    digits[index] = String(Number(digit) + 1);
  } else {
    digits.unshift("1");
  }
  const cut = digits.length - fracPart.length;
  return {
    intPart: digits.slice(0, cut).join(""),
    fracPart: digits.slice(cut).join(""),
  };
}

function trimFraction(fracPart: string, minFraction: number): string {
  let end = fracPart.length;
  while (end > minFraction && fracPart[end - 1] === "0") {
    end -= 1;
  }
  return fracPart.slice(0, end);
}

function groupInteger(intPart: string, grouping: string, useGrouping: boolean): string {
  if (!useGrouping) {
    return intPart;
  }
  return intPart.replace(/\B(?=(\d{3})+(?!\d))/g, grouping);
}

function shiftDecimalPoint(absDecimal: string, exponent: number): string {
  const dotIndex = absDecimal.indexOf(".");
  const intPart = dotIndex < 0 ? absDecimal : absDecimal.slice(0, dotIndex);
  const fracPart = dotIndex < 0 ? "" : absDecimal.slice(dotIndex + 1);
  const digits = intPart + fracPart;
  const pointIndex = intPart.length - exponent;
  if (pointIndex <= 0) {
    return `0.${"0".repeat(-pointIndex)}${digits}`;
  }
  if (pointIndex >= digits.length) {
    return `${digits}${"0".repeat(pointIndex - digits.length)}`;
  }
  return `${digits.slice(0, pointIndex)}.${digits.slice(pointIndex)}`;
}

function formatCompactBody(parsed: ParsedValue, rules: LocaleRules): string {
  if (parsed.isZero) {
    return "0";
  }
  const [integerPart = ""] = parsed.absDecimal.split(".");
  const intLength = integerPart.length;
  let unitIndex = -1;
  let unit: CompactUnit | undefined;
  for (const [index, candidate] of rules.compact.entries()) {
    if (intLength > candidate.exponent) {
      unitIndex = index;
      unit = candidate;
      break;
    }
  }
  if (unitIndex < 0 || unit === undefined) {
    const rounded = roundDecimal(parsed.absDecimal, 1);
    const trimmed = trimFraction(rounded.fracPart, 0);
    return trimmed.length > 0 ? `${rounded.intPart}.${trimmed}` : rounded.intPart;
  }
  let scaled = roundDecimal(shiftDecimalPoint(parsed.absDecimal, unit.exponent), 1);
  if (scaled.intPart.length > 1 && unitIndex + 1 < rules.compact.length) {
    const nextUnit = rules.compact[unitIndex + 1];
    if (nextUnit !== undefined) {
      scaled = roundDecimal(shiftDecimalPoint(parsed.absDecimal, nextUnit.exponent), 1);
      unit = nextUnit;
    }
  }
  const trimmed = trimFraction(scaled.fracPart, 0);
  return `${trimmed.length > 0 ? `${scaled.intPart}.${trimmed}` : scaled.intPart}${unit.unit}`;
}

function signPrefix(negative: boolean, isZero: boolean, sign: MoneySign): string {
  switch (sign) {
    case "always":
      return negative ? "-" : "+";
    case "never":
      return "";
    case "except_zero":
      return negative ? "-" : isZero ? "" : "+";
    default:
      return negative ? "-" : "";
  }
}

function modeDefaultFraction(mode: MoneyMode): { minFraction: number; maxFraction: number } {
  if (mode === "compact") {
    return { minFraction: 0, maxFraction: 1 };
  }
  if (mode === "decimal") {
    return { minFraction: 0, maxFraction: 2 };
  }
  return { minFraction: 2, maxFraction: 2 };
}

interface FormatArguments {
  value: MoneyValue | null;
  currency: string | null;
  locale: string | null;
  mode: MoneyMode | null;
  minFraction: number | null;
  maxFraction: number | null;
  sign: MoneySign | null;
  useGrouping: boolean | null;
}

function formatMoneyInternal(args: FormatArguments): string | null {
  const code = lookupCurrency(args.currency);
  if (!code) {
    return null;
  }
  if (!args.mode || !MONEY_MODES.includes(args.mode)) {
    return null;
  }
  if (args.sign && !MONEY_SIGNS.includes(args.sign)) {
    return null;
  }
  const parsed = parseValue(args.value);
  if (!parsed) {
    return null;
  }

  let minFraction: number;
  let maxFraction: number;
  if (args.minFraction === null && args.maxFraction === null) {
    ({ minFraction, maxFraction } = modeDefaultFraction(args.mode));
  } else {
    if (!Number.isInteger(args.minFraction) || !Number.isInteger(args.maxFraction)) {
      return null;
    }
    minFraction = args.minFraction as number;
    maxFraction = args.maxFraction as number;
    if (minFraction < 0 || maxFraction > 18 || minFraction > maxFraction) {
      return null;
    }
    if (args.mode === "compact") {
      minFraction = 0;
      maxFraction = 1;
    }
  }

  const useGrouping = args.useGrouping === null ? true : args.useGrouping;
  const sign = args.sign ?? "auto";
  const rules = resolveLocaleRules(args.locale);
  const negative = parsed.negative;
  const isZero = parsed.isZero;
  const symbol = CURRENCY_SYMBOLS[code];
  if (symbol === undefined) {
    return null;
  }

  if (args.mode === "compact") {
    const body = formatCompactBody(parsed, rules);
    const signText = signPrefix(negative, isZero, sign);
    return rules.prefix ? `${signText}${symbol}${body}` : `${signText}${body} ${symbol}`;
  }

  const rounded = roundDecimal(parsed.absDecimal, maxFraction);
  const fracPart = trimFraction(rounded.fracPart, minFraction);
  const grouped = groupInteger(rounded.intPart, rules.grouping, useGrouping);
  const body = fracPart.length > 0 ? `${grouped}${rules.decimal}${fracPart}` : grouped;
  const signText = signPrefix(negative, isZero, sign);

  if (args.mode === "decimal") {
    return `${signText}${body}`;
  }

  if (args.mode === "accounting") {
    if (negative && rules.prefix) {
      return `(${symbol}${body})`;
    }
    if (negative && !rules.prefix) {
      return `-${body} ${symbol}`;
    }
    return rules.prefix ? `${symbol}${body}` : `${body} ${symbol}`;
  }

  if (args.mode === "code") {
    return rules.prefix ? `${signText}${code} ${body}` : `${signText}${body} ${code}`;
  }

  if (args.mode === "name") {
    const names = resolveNames(args.locale);
    const name = names[code] ?? names.USD ?? code;
    const separator = rules.nameSpace ? " " : "";
    return `${signText}${body}${separator}${name}`;
  }

  return rules.prefix ? `${signText}${symbol}${body}` : `${signText}${body} ${symbol}`;
}

export function moneySymbol(currency: string): string | null {
  const code = lookupCurrency(currency);
  return code ? CURRENCY_SYMBOLS[code] ?? null : null;
}

export function formatMoney(
  value: MoneyValue,
  currency: string,
  locale: string,
  mode: MoneyMode,
): string | null;
export function formatMoney(value: MoneyValue, options: MoneyFormatOptions): string | null;
export function formatMoney(
  value: MoneyValue,
  currencyOrOptions: string | MoneyFormatOptions,
  locale?: string,
  mode?: MoneyMode,
): string | null {
  if (typeof currencyOrOptions === "string") {
    return formatMoneyInternal({
      value,
      currency: currencyOrOptions,
      locale: locale ?? null,
      mode: mode ?? null,
      minFraction: null,
      maxFraction: null,
      sign: null,
      useGrouping: null,
    });
  }
  const options = currencyOrOptions;
  const resolvedMode = options.mode ?? "symbol";
  const defaults = modeDefaultFraction(resolvedMode);
  return formatMoneyInternal({
    value,
    currency: options.currency,
    locale: options.locale ?? null,
    mode: resolvedMode,
    minFraction: options.minFractionDigits ?? defaults.minFraction,
    maxFraction: options.maxFractionDigits ?? defaults.maxFraction,
    sign: options.sign ?? null,
    useGrouping: options.useGrouping ?? null,
  });
}

export function formatMoneyDigits(
  value: MoneyValue,
  currency: string,
  locale: string,
  mode: MoneyMode,
  minFraction: number,
  maxFraction: number,
): string | null {
  return formatMoneyInternal({
    value,
    currency,
    locale,
    mode,
    minFraction,
    maxFraction,
    sign: null,
    useGrouping: null,
  });
}

export function formatMoneyMinorUnits(
  minor: number,
  currency: string,
  locale: string,
  mode: MoneyMode,
): string | null {
  if (typeof minor !== "number" || !Number.isInteger(minor)) {
    return null;
  }
  if (mode === "compact" || !MONEY_MODES.includes(mode)) {
    return null;
  }
  const exponent = minorUnitExponent(currency);
  if (exponent === null) {
    return null;
  }
  return formatMoneyInternal({
    value: minor / 10 ** exponent,
    currency,
    locale,
    mode,
    minFraction: exponent,
    maxFraction: exponent,
    sign: null,
    useGrouping: null,
  });
}

export function formatMoneyOptions(
  value: MoneyValue,
  currency: string,
  locale: string,
  mode: MoneyMode,
  minFraction: number,
  maxFraction: number,
  sign: MoneySign,
  useGrouping: boolean,
): string | null {
  return formatMoneyInternal({
    value,
    currency,
    locale,
    mode,
    minFraction,
    maxFraction,
    sign,
    useGrouping,
  });
}
