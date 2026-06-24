export type ResultValue<T> = {
    ok: true;
    value: T;
} | {
    ok: false;
    error: string;
};
export declare function ok<T>(value: T): ResultValue<T>;
export declare function err<T = never>(message: string): ResultValue<T>;
export declare function isOk<T>(result: ResultValue<T>): result is {
    ok: true;
    value: T;
};
export declare function isErr<T>(result: ResultValue<T>): result is {
    ok: false;
    error: string;
};
export declare function unwrapOr<T>(result: ResultValue<T>, defaultValue: T): T;
export declare function map<T, U>(result: ResultValue<T>, mapper: (value: T) => U): ResultValue<U>;
