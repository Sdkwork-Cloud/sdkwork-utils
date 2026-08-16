import { Sha256Hasher } from "./runtime/sha256.js";
export declare function sha256Hash(value: string | Uint8Array): string;
export { Sha256Hasher };
export declare function hmacSha256(value: string | Uint8Array, secret: string | Uint8Array): string;
export declare function secureCompare(left: string, right: string): boolean;
//# sourceMappingURL=crypto.d.ts.map