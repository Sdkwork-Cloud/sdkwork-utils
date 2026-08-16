export declare function sha256Digest(value: Uint8Array): Uint8Array;
export declare class Sha256Hasher {
    private readonly state;
    private readonly buffer;
    private bufferLength;
    private totalLength;
    update(chunk: Uint8Array): void;
    digest(): Uint8Array;
}
export declare function hmacSha256Digest(value: Uint8Array, secret: Uint8Array): Uint8Array;
export declare function sha256Hex(value: string | Uint8Array): string;
export declare function hmacSha256Hex(value: string | Uint8Array, secret: string | Uint8Array): string;
//# sourceMappingURL=sha256.d.ts.map