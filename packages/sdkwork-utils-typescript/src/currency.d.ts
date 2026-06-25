export declare function isCurrencyCode(value: string): boolean;
export declare function minorUnitExponent(code: string): number | null;
export declare function toMinorUnits(amount: number, code: string): number | null;
export declare function fromMinorUnits(minor: number, code: string): number | null;
export declare function formatCurrency(amount: number, code: string, locale: string): string | null;
