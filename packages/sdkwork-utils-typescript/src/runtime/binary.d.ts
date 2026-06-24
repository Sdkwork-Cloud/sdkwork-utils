export declare function toUtf8(value: string): Uint8Array;
export declare function readUInt32BE(bytes: Uint8Array, offset: number): number;
export declare function hexEncode(bytes: Uint8Array): string;
export declare function hexDecode(value: string): Uint8Array | null;
export declare function base64Encode(bytes: Uint8Array): string;
export declare function base64Decode(value: string): Uint8Array | null;
export declare function base64UrlEncode(bytes: Uint8Array): string;
export declare function base64UrlDecode(value: string): Uint8Array | null;
