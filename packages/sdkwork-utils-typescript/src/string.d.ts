export declare function isBlank(value: string | null | undefined): boolean;
export declare function trim(value: string): string;
export declare function truncate(value: string, maxLen: number, suffix?: string): string;
export declare function capitalize(value: string): string;
export declare function camelCase(value: string): string;
export declare function snakeCase(value: string): string;
export declare function kebabCase(value: string): string;
export declare function slugify(value: string): string;
export declare function mask(value: string, visibleStart: number, visibleEnd: number, maskChar?: string): string;
export declare function padStart(value: string, targetLen: number, padChar?: string): string;
export declare function padEnd(value: string, targetLen: number, padChar?: string): string;
export declare function startsWith(value: string, prefix: string): boolean;
export declare function endsWith(value: string, suffix: string): boolean;
export declare function contains(value: string, substring: string): boolean;
export declare function replaceAll(value: string, search: string, replacement: string): string;
export declare function split(value: string, delimiter: string, trimParts?: boolean): string[];
export declare function join(parts: string[], separator: string): string;
export declare function repeat(value: string, count: number): string;
export declare function normalizeWhitespace(value: string): string;
export declare function template(pattern: string, values: Record<string, string>): string;
export interface SplitDisplayFileName {
    stem: string;
    extension: string | null;
}
export declare function splitDisplayFileName(fileName: string): SplitDisplayFileName;
export declare function formatNumberedFilenameVariant(stem: string, index: number, extension: string | null): string;
export declare function hasSiblingNameConflict(candidateName: string, siblingNames: Iterable<string>, excludeName?: string): boolean;
export declare function allocateUniqueSiblingName(baseName: string, siblingNames: Iterable<string>, excludeName?: string): string;
/** @deprecated Use allocateUniqueSiblingName */
export declare const resolveUniqueSiblingName: typeof allocateUniqueSiblingName;
//# sourceMappingURL=string.d.ts.map