import assert from "node:assert/strict";
import test from "node:test";
import {
  camelCase,
  isBlank,
  slugify,
  truncate,
} from "../string.js";
import { addHours, diffMillis, parseDatetime } from "../datetime.js";
import { base64Encode, hexEncode } from "../encoding.js";
import { deepMerge, getPath, setPath } from "../object.js";
import { hmacSha256, sha256Hash } from "../crypto.js";
import { formatNumberLocale, formatDatetimeLocaleStr } from "../i18n.js";
import { coalesce, defaultIfBlank } from "../optional.js";
import { ok, unwrapOr } from "../result.js";

test("string helpers", () => {
  assert.equal(isBlank("  "), true);
  assert.equal(camelCase("hello_world"), "helloWorld");
  assert.equal(slugify("Hello, SDKWork!"), "hello-sdk-work");
  assert.equal(truncate("abcdef", 5), "ab...");
});

test("datetime helpers", () => {
  const first = parseDatetime("2024-01-01T00:00:00.000Z");
  assert.ok(first);
  const second = addHours(first!, 2);
  assert.equal(diffMillis(first!, second), 7_200_000);
});

test("encoding helpers", () => {
  const bytes = new TextEncoder().encode("hello");
  assert.equal(base64Encode(bytes), "aGVsbG8=");
  assert.equal(hexEncode(bytes), "68656c6c6f");
});

test("object and crypto helpers", () => {
  const merged = deepMerge({ a: 1, nested: { x: 1 } }, { b: 2, nested: { y: 2 } }) as Record<
    string,
    unknown
  >;
  assert.deepEqual(merged, { a: 1, b: 2, nested: { x: 1, y: 2 } });
  const updated = setPath({}, "user.city", "Paris");
  assert.equal(getPath(updated, "user.city"), "Paris");
  assert.equal(
    sha256Hash("hello"),
    "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824",
  );
  assert.equal(
    hmacSha256("payload", "secret"),
    "b82fcb791acec57859b989b430a826488ce2e479fdf92326bd0a2e8375a42ba4",
  );
});

test("optional result and i18n helpers", () => {
  assert.equal(coalesce(null, "", "  ", "ok"), "ok");
  assert.equal(defaultIfBlank("  ", "fallback"), "fallback");
  assert.equal(unwrapOr(ok(42), 0), 42);
  assert.equal(unwrapOr({ ok: false, error: "fail" }, 0), 0);
  assert.equal(formatNumberLocale(1234.5, "en-US", 2), "1,234.50");
  assert.equal(formatNumberLocale(1234.5, "de-DE", 2), "1.234,50");
  assert.ok(formatDatetimeLocaleStr("2024-06-15T14:30:00.000Z", "en-US")?.includes("2024"));
});
