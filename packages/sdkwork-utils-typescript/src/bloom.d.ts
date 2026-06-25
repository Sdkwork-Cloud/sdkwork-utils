export declare class BloomFilter {
    readonly bitCount: number;
    readonly hashCount: number;
    readonly bits: Uint8Array;
    constructor(bitCount: number, hashCount: number, bits: Uint8Array);
}
export declare function estimateBitCount(expectedItems: number, falsePositiveRate: number): number;
export declare function estimateHashCount(expectedItems: number, bitCount: number): number;
export declare function create(expectedItems: number, falsePositiveRate: number): BloomFilter;
export declare function add(filter: BloomFilter, value: string): void;
export declare function mightContain(filter: BloomFilter, value: string): boolean;
