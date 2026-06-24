# Generic utils scope (sdkwork-utils)

This document records what belongs in **sdkwork-utils** versus application code.

## In scope (implemented or planned here)

| Area | Examples | Module |
| --- | --- | --- |
| Text | blank, trim, case, slug, mask, pad, split, repeat, normalize whitespace, template | `string` |
| Bytes | Human-readable byte sizes for logs/UI | `bytes` |
| Time | UTC parse/format, arithmetic, unix millis | `datetime` |
| Numbers | clamp, round, parse, percent, range, abs | `number` |
| Money | ISO code check, minor units, locale currency format | `currency` |
| Probabilistic sets | Bloom filter membership with false-positive control | `bloom` |
| Lists | unique, chunk, group, flatten, compact, first/last, sort, key, filter, find | `collection` |
| Objects | pick, omit, path get/set, merge, keys, values | `object` |
| Validation | email, uuid, url, numeric, ipv4, ipv6, e164 phone | `validation` |
| Encoding | base64, base64url, hex, url | `encoding` |
| Crypto | sha256, hmac, secure compare | `crypto` |
| i18n display | locale number/datetime formatting and parsing | `i18n` |
| Optional/Result | coalesce, lightweight ok/err | `optional`, `result` |
| Compare | deep equal and deep clone JSON-like structures | `compare` |

## v0.10 conformance expansion

- Java, Kotlin, and C# conformance tests iterate all 112 contract operations
- Go conformance expanded via `conformance_extra_test.go` (87% audited coverage; 70% threshold)
- Audit script supports C#/Kotlin `GetProperty("operation")` and multi-file Go test paths

## v0.9 symmetry and closure

- `i18n.parse_number_locale` mirrors `format_number_locale` for en-US and de-DE separators
- `validation.is_ipv6` accepts full or `::`-compressed colon-hex forms (not IPv4 dotted)
- `validation.is_phone_e164` checks `+` followed by 2–15 digits with leading digit 1–9
- `encoding.base64url_encode` / `base64url_decode` use URL-safe unpadded base64
- `compare.deep_clone` returns independent JSON-like trees (scalars, arrays, objects)

## v0.7 conformance quality

- `pnpm verify` runs `scripts/audit-conformance-coverage.mjs` against [`coverage-thresholds.json`](conformance/coverage-thresholds.json)
- TypeScript, Python, and PHP conformance tests iterate all 112 contract operations
- Java, Kotlin, C#, Go, and Rust use phased minimum coverage thresholds (raised over time)

## v0.6 bloom module

- `create(expected_items, false_positive_rate)` sizes bit array and hash count from standard formulas
- `add` / `might_contain` use SHA-256 double-hash positions (Kirsch-Mitzenmacher)
- No false negatives for inserted items; false-positive rate is approximate

## v0.5 currency module

Operations align with [`currency.metadata.json`](currency.metadata.json):

- Supported codes: USD, EUR, GBP, CNY, JPY, KRW, HKD, TWD, CHF, CAD, AUD, INR, BHD, KWD
- `to_minor_units` / `from_minor_units` use ISO exponent (e.g. USD×100, JPY×1)
- `format_currency` reuses locale separators from `i18n.format_number_locale`

## Candidates for future versions (generic only)

| Candidate | Rationale | Notes |
| --- | --- | --- |
| `collection.map` / `reduce` | Functional helpers | Often native in each language |
| `validation.is_iso3166_alpha2` | Country codes | Static list maintenance |
| `retry` / `backoff` | Generic retry delays | May belong in runtime libs |

## Out of scope (do not add)

- Payment gateway APIs, tax calculation, FX rates
- Framework-specific helpers (React hooks, Spring beans)
- App/domain DTOs or SDK HTTP clients
- Database, cache, or file I/O utilities
- Business validation (SKU rules, tenant policies)

When proposing a new operation, update `utils.contract.json`, `fixtures.json`, all 8 language packages, and this document if scope changes.
