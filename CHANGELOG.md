# Changelog

## 0.10.0

- Conformance quality release: Java, Kotlin, and C# conformance tests now iterate all 112 contract operations
- Go conformance expanded to 87% fixture coverage (threshold raised to 70%)
- Coverage audit script recognizes `GetProperty` / `getJSONArray` fixture references and multi-file Go tests

## 0.9.0

- Add `i18n.parse_number_locale` (symmetry with `format_number_locale` for en-US and de-DE)
- Add `validation.is_ipv6` and `validation.is_phone_e164`
- Add `encoding.base64url_encode` and `encoding.base64url_decode`
- Add `compare.deep_clone` for independent JSON-like tree copies
- 18 modules, 112 contract operations

## 0.8.0

- Add `string.template` for `{key}` placeholder substitution from string maps
- Add **bytes** module with `format_bytes` (1024-based B/KB/MB/GB/TB/PB)
- 18 modules, 106 contract operations

## 0.7.0

- Quality release: conformance coverage audit gate in `pnpm verify`
- TypeScript, Python, and PHP conformance tests now iterate all 104 contract operations
- Kotlin, C#, and Go conformance expanded for `currency` and `bloom`
- Add [`specs/conformance/coverage-thresholds.json`](specs/conformance/coverage-thresholds.json) with phased per-language minimums
- Sync package README versions and repository docs to 0.7.0

## 0.6.0

- Add **bloom** module: `create`, `add`, `might_contain`, `estimate_bit_count`, `estimate_hash_count`
- SHA-256 Kirsch-Mitzenmacher hashing with shared sizing formulas across all 8 languages
- 17 modules, 104 contract operations with full fixture coverage

## 0.5.0

- Add **currency** module: ISO code validation, minor units, locale currency formatting
- Add `string.normalize_whitespace`, `number.abs`, `collection.filter`, `collection.find`
- Add [`specs/currency.metadata.json`](specs/currency.metadata.json) and [`specs/GENERIC_UTILS_SCOPE.md`](specs/GENERIC_UTILS_SCOPE.md)
- 16 modules, 99 contract operations with full fixture coverage

## 0.4.0

- Add **compare** module with `deep_equal`
- Add `string.repeat`, `datetime.is_same_instant`, `number.in_range`
- Add `collection.sort_by`, `collection.key_by`, `object.keys`, `object.values`
- Require fixture key for **every** contract operation (90 operations, 15 modules)
- Expand conformance fixtures for previously untested operations
- Add GEMINI.md and CODEX.md compatibility shims
- Bump all packages to 0.4.0

## 0.3.0

- Add **boolean** module (`parse_bool`, `is_truthy`)
- Extend string, datetime, number, collection, validation, encoding, object, crypto modules
- Expand conformance fixtures for broader cross-language coverage
- Add `specs/naming.aliases.json` and contract semantics section
- Package README for every language; AGENTS.md aligned with SDKWork spec
- Fix PHP Unicode string length; normalize mask/pad defaults across languages

## 0.2.0

- Add 8 language packages: Rust, TypeScript, Python, Go, Java, Kotlin, C#, PHP
- Add 13 utility modules aligned by `specs/utils.contract.json`
- Add cross-language conformance fixtures and conformance tests
- Add `pnpm verify` orchestration and GitHub Actions CI
- Add optional, result, i18n, object, and crypto modules

## 0.1.0

- Initial scaffold with core string/datetime/number modules
