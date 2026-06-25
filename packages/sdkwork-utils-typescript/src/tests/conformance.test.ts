import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";
import test from "node:test";
import {
  add as addBloom,
  create as createBloom,
  estimateBitCount,
  estimateHashCount,
  mightContain,
} from "../bloom.js";
import { parseBool, isTruthy } from "../boolean.js";
import { deepEqual, deepClone } from "../compare.js";
import {
  formatCurrency,
  fromMinorUnits,
  isCurrencyCode,
  minorUnitExponent,
  toMinorUnits,
} from "../currency.js";
import {
  camelCase,
  capitalize,
  contains,
  endsWith,
  isBlank,
  join,
  kebabCase,
  mask,
  padEnd,
  padStart,
  replaceAll,
  repeat,
  slugify,
  snakeCase,
  split,
  startsWith,
  trim,
  truncate,
  normalizeWhitespace,
  template,
} from "../string.js";
import { formatBytes } from "../bytes.js";
import {
  addDays,
  addHours,
  addMinutes,
  diffMillis,
  endOfDayUtc,
  formatDatetime,
  fromUnixMillis,
  isAfter,
  isBefore,
  isSameInstant,
  now,
  parseDatetime,
  startOfDayUtc,
  toUnixMillis,
} from "../datetime.js";
import { base64Decode, base64Encode, base64UrlDecode, base64UrlEncode, hexDecode, hexEncode, urlDecode, urlEncode } from "../encoding.js";
import { chunk, compact, filter, find, first, flatten, groupBy, keyBy, last, sortBy, unique } from "../collection.js";
import {
  compactObject,
  deepMerge,
  getPath,
  hasPath,
  keys,
  omit,
  pick,
  setPath,
  shallowMerge,
  values,
} from "../object.js";
import { hmacSha256, secureCompare, sha256Hash } from "../crypto.js";
import { coalesce, defaultIfBlank } from "../optional.js";
import { err, isErr, isOk, map, ok, unwrapOr } from "../result.js";
import { formatDatetimeLocaleStr, formatNumberLocale, parseNumberLocale } from "../i18n.js";
import {
  abs,
  clamp,
  formatNumber,
  inRange,
  isInteger,
  parseInt as parseIntValue,
  parseNumber,
  percentFormat,
  round,
} from "../number.js";
import { isEmail, isIpv4, isIpv6, isNumeric, isPhoneE164, isUrl, isUuid } from "../validation.js";
import { randomString, uuid } from "../id.js";
import { joinPath, normalizePath } from "../path.js";

const fixtures = JSON.parse(
  readFileSync(
    path.resolve(path.dirname(fileURLToPath(import.meta.url)), "../../../../specs/conformance/fixtures.json"),
    "utf8",
  ),
);

const UUID_V4_PATTERN =
  /^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;

function decodeText(bytes: Uint8Array): string {
  return new TextDecoder().decode(bytes);
}

test("conformance fixtures", () => {
  for (const item of fixtures.string.is_blank) {
    assert.equal(isBlank(item.input), item.output);
  }
  for (const item of fixtures.string.trim) {
    assert.equal(trim(item.input), item.output);
  }
  for (const item of fixtures.string.truncate) {
    assert.equal(truncate(item.input, item.max_len, item.suffix), item.output);
  }
  for (const item of fixtures.string.capitalize) {
    assert.equal(capitalize(item.input), item.output);
  }
  for (const item of fixtures.string.camel_case) {
    assert.equal(camelCase(item.input), item.output);
  }
  for (const item of fixtures.string.snake_case) {
    assert.equal(snakeCase(item.input), item.output);
  }
  for (const item of fixtures.string.kebab_case) {
    assert.equal(kebabCase(item.input), item.output);
  }
  for (const item of fixtures.string.slugify) {
    assert.equal(slugify(item.input), item.output);
  }
  for (const item of fixtures.string.mask) {
    assert.equal(mask(item.input, item.visible_start, item.visible_end), item.output);
  }
  for (const item of fixtures.string.pad_start) {
    assert.equal(padStart(item.input, item.target_len), item.output);
  }
  for (const item of fixtures.string.pad_end) {
    assert.equal(padEnd(item.input, item.target_len), item.output);
  }
  for (const item of fixtures.string.starts_with) {
    assert.equal(startsWith(item.input, item.prefix), item.output);
  }
  for (const item of fixtures.string.ends_with) {
    assert.equal(endsWith(item.input, item.suffix), item.output);
  }
  for (const item of fixtures.string.contains) {
    assert.equal(contains(item.input, item.substring), item.output);
  }
  for (const item of fixtures.string.replace_all) {
    assert.equal(replaceAll(item.input, item.search, item.replacement), item.output);
  }
  for (const item of fixtures.string.split) {
    assert.deepEqual(split(item.input, item.delimiter, item.trim_parts), item.output);
  }
  for (const item of fixtures.string.join) {
    assert.equal(join(item.parts, item.separator), item.output);
  }
  for (const item of fixtures.string.normalize_whitespace) {
    assert.equal(normalizeWhitespace(item.input), item.output);
  }
  for (const item of fixtures.string.template) {
    assert.equal(template(item.template, item.values), item.output);
  }
  for (const item of fixtures.string.repeat) {
    assert.equal(repeat(item.input, item.count), item.output);
  }

  for (const item of fixtures.datetime.now) {
    assert.equal(item.valid, now() instanceof Date);
  }
  for (const item of fixtures.datetime.format) {
    const parsed = parseDatetime(item.input)!;
    assert.equal(formatDatetime(parsed), item.output);
  }
  for (const item of fixtures.datetime.parse) {
    assert.equal(parseDatetime(item.input) !== null, item.valid);
  }
  const earlier = parseDatetime(fixtures.datetime.diff_millis.earlier)!;
  const later = parseDatetime(fixtures.datetime.diff_millis.later)!;
  assert.equal(diffMillis(earlier, later), fixtures.datetime.diff_millis.output);
  for (const item of fixtures.datetime.add_days) {
    const parsed = parseDatetime(item.input)!;
    assert.equal(formatDatetime(addDays(parsed, item.days)), item.output);
  }
  for (const item of fixtures.datetime.add_hours) {
    const parsed = parseDatetime(item.input)!;
    assert.equal(formatDatetime(addHours(parsed, item.hours)), item.output);
  }
  for (const item of fixtures.datetime.add_minutes) {
    const parsed = parseDatetime(item.input)!;
    assert.equal(formatDatetime(addMinutes(parsed, item.minutes)), item.output);
  }
  for (const item of fixtures.datetime.is_before) {
    const left = parseDatetime(item.left)!;
    const right = parseDatetime(item.right)!;
    assert.equal(isBefore(left, right), item.output);
  }
  for (const item of fixtures.datetime.is_after) {
    const left = parseDatetime(item.left)!;
    const right = parseDatetime(item.right)!;
    assert.equal(isAfter(left, right), item.output);
  }
  for (const item of fixtures.datetime.start_of_day_utc) {
    const parsed = parseDatetime(item.input)!;
    assert.equal(formatDatetime(startOfDayUtc(parsed)), item.output);
  }
  for (const item of fixtures.datetime.end_of_day_utc) {
    const parsed = parseDatetime(item.input)!;
    assert.equal(formatDatetime(endOfDayUtc(parsed)), item.output);
  }
  for (const item of fixtures.datetime.to_unix_millis) {
    const parsed = parseDatetime(item.input)!;
    assert.equal(toUnixMillis(parsed), item.output);
  }
  for (const item of fixtures.datetime.from_unix_millis) {
    const parsed = fromUnixMillis(item.input)!;
    assert.equal(formatDatetime(parsed), item.output);
  }
  for (const item of fixtures.datetime.is_same_instant) {
    const left = parseDatetime(item.left)!;
    const right = parseDatetime(item.right)!;
    assert.equal(isSameInstant(left, right), item.output);
  }

  for (const item of fixtures.encoding.base64_encode) {
    const bytes = new TextEncoder().encode(item.input);
    assert.equal(base64Encode(bytes), item.output);
  }
  for (const item of fixtures.encoding.base64_decode) {
    const decoded = base64Decode(item.input)!;
    assert.equal(decodeText(decoded), item.output);
  }
  for (const item of fixtures.encoding.hex_encode) {
    const bytes = new TextEncoder().encode(item.input);
    assert.equal(hexEncode(bytes), item.output);
  }
  for (const item of fixtures.encoding.hex_decode) {
    const decoded = hexDecode(item.input)!;
    assert.equal(decodeText(decoded), item.output);
  }
  for (const item of fixtures.encoding.url_encode) {
    assert.equal(urlEncode(item.input), item.output);
  }
  for (const item of fixtures.encoding.url_decode) {
    assert.equal(urlDecode(item.input), item.output);
  }
  for (const item of fixtures.encoding.base64url_encode) {
    const bytes = new TextEncoder().encode(item.input);
    assert.equal(base64UrlEncode(bytes), item.output);
  }
  for (const item of fixtures.encoding.base64url_decode) {
    const decoded = base64UrlDecode(item.input)!;
    assert.equal(decodeText(decoded), item.output);
  }

  const merged = deepMerge(fixtures.object.deep_merge.base, fixtures.object.deep_merge.overlay);
  assert.deepEqual(merged, fixtures.object.deep_merge.output);
  const shallow = shallowMerge(fixtures.object.shallow_merge.base, fixtures.object.shallow_merge.overlay);
  assert.deepEqual(shallow, fixtures.object.shallow_merge.output);
  const updated = setPath({}, fixtures.object.set_get_path.path, fixtures.object.set_get_path.value);
  assert.equal(getPath(updated, fixtures.object.set_get_path.path), fixtures.object.set_get_path.output);
  for (const item of fixtures.object.get_path) {
    assert.equal(getPath(item.source, item.path), item.output);
  }
  for (const item of fixtures.object.set_path) {
    const result = setPath({}, item.path, item.value);
    assert.equal(getPath(result, item.path), item.read_back);
  }
  for (const item of fixtures.object.pick) {
    assert.deepEqual(pick(item.source, item.keys), item.output);
  }
  for (const item of fixtures.object.omit) {
    assert.deepEqual(omit(item.source, item.keys), item.output);
  }
  for (const item of fixtures.object.compact) {
    assert.deepEqual(compactObject(item.input), item.output);
  }
  const pathSource = { user: { name: "Ada" } };
  for (const item of fixtures.object.has_path) {
    assert.equal(hasPath(pathSource, item.path), item.exists);
  }
  for (const item of fixtures.object.keys) {
    assert.deepEqual(keys(item.input), item.output);
  }
  for (const item of fixtures.object.values) {
    assert.deepEqual(values(item.input), item.output);
  }

  for (const item of fixtures.crypto.sha256_hash) {
    assert.equal(sha256Hash(item.input), item.output);
  }
  for (const item of fixtures.crypto.hmac_sha256) {
    assert.equal(hmacSha256(item.input, item.secret), item.output);
  }
  for (const item of fixtures.crypto.secure_compare) {
    assert.equal(secureCompare(item.left, item.right), item.output);
  }

  for (const item of fixtures.number.clamp) {
    assert.equal(clamp(item.value, item.min, item.max), item.output);
  }
  for (const item of fixtures.number.round) {
    assert.equal(round(item.value, item.decimals), item.output);
  }
  for (const item of fixtures.number.format_number) {
    assert.equal(formatNumber(item.value, item.decimals), item.output);
  }
  for (const item of fixtures.number.parse_number) {
    assert.equal(parseNumber(item.input), item.output);
  }
  for (const item of fixtures.number.is_integer) {
    assert.equal(isInteger(item.value), item.output);
  }
  for (const item of fixtures.number.parse_int) {
    assert.equal(parseIntValue(item.input), item.output);
  }
  for (const item of fixtures.number.percent_format) {
    assert.equal(percentFormat(item.value, item.decimals), item.output);
  }
  for (const item of fixtures.number.in_range) {
    assert.equal(inRange(item.value, item.min, item.max), item.output);
  }
  for (const item of fixtures.number.abs) {
    assert.equal(abs(item.input), item.output);
  }

  for (const item of fixtures.collection.unique) {
    assert.deepEqual(unique(item.input), item.output);
  }
  for (const item of fixtures.collection.chunk) {
    assert.deepEqual(chunk(item.input, item.size), item.output);
  }
  for (const item of fixtures.collection.flatten) {
    assert.deepEqual(flatten(item.input), item.output);
  }
  for (const item of fixtures.collection.compact) {
    assert.deepEqual(compact(item.input), item.output);
  }
  for (const item of fixtures.collection.group_by) {
    const grouped = groupBy(item.input, (entry: { type: string }) => entry.type);
    assert.deepEqual(grouped, item.output);
  }
  for (const item of fixtures.collection.first) {
    assert.equal(first(item.input), item.output);
  }
  for (const item of fixtures.collection.last) {
    assert.equal(last(item.input), item.output);
  }
  for (const item of fixtures.collection.sort_by) {
    const sorted = sortBy(item.input, (entry: { k: string }) => entry.k);
    assert.deepEqual(sorted, item.output);
  }
  for (const item of fixtures.collection.key_by) {
    const keyed = keyBy(item.input, (entry: { id: string }) => entry.id);
    assert.deepEqual(keyed, item.output);
  }
  for (const item of fixtures.collection.filter) {
    assert.deepEqual(
      filter(item.input, (value: number) => value > item.threshold),
      item.output,
    );
  }
  for (const item of fixtures.collection.find) {
    assert.equal(
      find(item.input, (value: number) => value > item.threshold),
      item.output,
    );
  }

  for (const item of fixtures.validation.is_email) {
    assert.equal(isEmail(item.input), item.output);
  }
  for (const item of fixtures.validation.is_uuid) {
    assert.equal(isUuid(item.input), item.output);
  }
  for (const item of fixtures.validation.is_url) {
    assert.equal(isUrl(item.input), item.output);
  }
  for (const item of fixtures.validation.is_numeric) {
    assert.equal(isNumeric(item.input), item.output);
  }
  for (const item of fixtures.validation.is_ipv4) {
    assert.equal(isIpv4(item.input), item.output);
  }
  for (const item of fixtures.validation.is_ipv6) {
    assert.equal(isIpv6(item.input), item.output);
  }
  for (const item of fixtures.validation.is_phone_e164) {
    assert.equal(isPhoneE164(item.input), item.output);
  }

  for (const item of fixtures.id.uuid) {
    assert.match(uuid(), UUID_V4_PATTERN);
    assert.equal(item.pattern, "uuid-v4");
  }
  for (const item of fixtures.id.random_string) {
    const value = randomString(item.length);
    assert.equal(value.length, item.length);
    assert.match(value, /^[A-Za-z0-9]+$/);
  }

  for (const item of fixtures.path.join_path) {
    assert.equal(joinPath(...item.segments), item.output);
  }
  for (const item of fixtures.path.normalize_path) {
    assert.equal(normalizePath(item.input), item.output);
  }

  for (const item of fixtures.i18n.format_number_locale) {
    assert.equal(formatNumberLocale(item.value, item.locale, item.decimals), item.output);
  }
  for (const item of fixtures.i18n.format_datetime_locale) {
    const formatted = formatDatetimeLocaleStr(item.input, item.locale);
    assert.ok(formatted?.includes(item.contains));
  }
  for (const item of fixtures.i18n.parse_number_locale) {
    assert.equal(parseNumberLocale(item.input, item.locale), item.output);
  }

  for (const item of fixtures.optional.coalesce) {
    assert.equal(coalesce(...item.values), item.output);
  }
  for (const item of fixtures.optional.default_if_blank) {
    assert.equal(defaultIfBlank(item.input, item.default), item.output);
  }

  for (const item of fixtures.result.ok) {
    const result = ok(item.value);
    assert.equal(isOk(result), item.is_ok);
    assert.equal(isErr(result), item.is_err);
  }
  for (const item of fixtures.result.err) {
    const result = err(item.message);
    assert.equal(isOk(result), item.is_ok);
    assert.equal(isErr(result), item.is_err);
  }
  for (const item of fixtures.result.is_ok) {
    const result = item.kind === "ok" ? ok(item.value) : err(item.message);
    assert.equal(isOk(result), item.output);
  }
  for (const item of fixtures.result.is_err) {
    const result = item.kind === "ok" ? ok(item.value) : err(item.message);
    assert.equal(isErr(result), item.output);
  }
  for (const item of fixtures.result.unwrap_or) {
    const result = item.kind === "ok" ? ok(item.value) : err(item.message);
    assert.equal(unwrapOr(result, item.default), item.output);
  }
  for (const item of fixtures.result.map) {
    const mapped = map(ok(item.value), (value: number) => value * 2);
    assert.equal(mapped.ok ? mapped.value : null, item.output);
  }

  for (const item of fixtures.boolean.parse_bool) {
    assert.equal(parseBool(item.input), item.output);
  }
  for (const item of fixtures.boolean.is_truthy) {
    assert.equal(isTruthy(item.input), item.output);
  }

  for (const item of fixtures.compare.deep_equal) {
    assert.equal(deepEqual(item.left, item.right), item.output);
  }
  for (const item of fixtures.compare.deep_clone) {
    const cloned = deepClone(item.input);
    assert.deepEqual(cloned, item.output);
    if (typeof cloned === "object" && cloned !== null && "b" in cloned) {
      const nested = cloned as { b: Array<{ c?: number }> };
      if (Array.isArray(nested.b) && nested.b[1] && typeof nested.b[1] === "object") {
        nested.b[1].c = 99;
        assert.notDeepEqual(item.input, cloned);
      }
    }
  }

  for (const item of fixtures.currency.is_currency_code) {
    assert.equal(isCurrencyCode(item.input), item.output);
  }
  for (const item of fixtures.currency.minor_unit_exponent) {
    assert.equal(minorUnitExponent(item.code), item.output);
  }
  for (const item of fixtures.currency.to_minor_units) {
    assert.equal(toMinorUnits(item.amount, item.code), item.output);
  }
  for (const item of fixtures.currency.from_minor_units) {
    assert.equal(fromMinorUnits(item.minor, item.code), item.output);
  }
  for (const item of fixtures.currency.format_currency) {
    assert.equal(formatCurrency(item.amount, item.code, item.locale), item.output);
  }

  for (const item of fixtures.bloom.create) {
    const filter = createBloom(item.expected_items, item.false_positive_rate);
    assert.equal(filter.bitCount, item.bit_count);
    assert.equal(filter.hashCount, item.hash_count);
  }
  for (const item of fixtures.bloom.estimate_bit_count) {
    assert.equal(estimateBitCount(item.expected_items, item.false_positive_rate), item.output);
  }
  for (const item of fixtures.bloom.estimate_hash_count) {
    assert.equal(estimateHashCount(item.expected_items, item.bit_count), item.output);
  }
  for (const item of fixtures.bloom.might_contain) {
    const filter = createBloom(128, 0.01);
    for (const value of item.added) {
      addBloom(filter, value);
    }
    assert.equal(mightContain(filter, item.present), item.present_output);
    assert.equal(mightContain(filter, item.absent), item.absent_output);
  }

  for (const item of fixtures.bytes.format_bytes) {
    assert.equal(formatBytes(item.value, item.decimals), item.output);
  }
});
