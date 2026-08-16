export type MoneyMode = "symbol" | "narrow_symbol" | "code" | "name" | "decimal" | "accounting" | "compact";
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
export declare function moneySymbol(currency: string): string | null;
export declare function formatMoney(value: MoneyValue, currency: string, locale: string, mode: MoneyMode): string | null;
export declare function formatMoney(value: MoneyValue, options: MoneyFormatOptions): string | null;
export declare function formatMoneyDigits(value: MoneyValue, currency: string, locale: string, mode: MoneyMode, minFraction: number, maxFraction: number): string | null;
export declare function formatMoneyMinorUnits(minor: number, currency: string, locale: string, mode: MoneyMode): string | null;
export declare function formatMoneyOptions(value: MoneyValue, currency: string, locale: string, mode: MoneyMode, minFraction: number, maxFraction: number, sign: MoneySign, useGrouping: boolean): string | null;
//# sourceMappingURL=money.d.ts.map