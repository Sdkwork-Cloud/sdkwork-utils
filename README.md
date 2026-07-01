# sdkwork-utils
repository-kind: shared-package-family

Cross-language common utility libraries for SDKWork applications.

**Version:** 0.11.0  
**Contract:** [`specs/utils.contract.json`](specs/utils.contract.json)  
**Fixtures:** [`specs/conformance/fixtures.json`](specs/conformance/fixtures.json)  
**Agent entry:** [`AGENTS.md`](AGENTS.md)

## Purpose

`sdkwork-utils` provides **generic, stable helpers** (string, datetime, number, validation, encoding, and more) with **equivalent behavior across languages**. Use these packages to simplify application code and keep utility usage consistent across Rust, TypeScript, Python, Go, Java, Kotlin, C#, and PHP codebases.

Only cross-cutting utilities belong here. App-specific or framework-coupled logic should stay in application modules.

## Packages

| Language | Directory | Install / import |
| --- | --- | --- |
| Rust | [`packages/sdkwork-utils-rust`](packages/sdkwork-utils-rust) | `sdkwork-utils-rust` crate |
| TypeScript | [`packages/sdkwork-utils-typescript`](packages/sdkwork-utils-typescript) | `@sdkwork/utils` |
| Python | [`packages/sdkwork-utils-python`](packages/sdkwork-utils-python) | `sdkwork-utils-python` |
| Go | [`packages/sdkwork-utils-go`](packages/sdkwork-utils-go) | `utils` package (module path in README) |
| Java | [`packages/sdkwork-utils-java`](packages/sdkwork-utils-java) | Maven artifact `com.sdkwork:sdkwork-utils-java` |
| Kotlin | [`packages/sdkwork-utils-kotlin`](packages/sdkwork-utils-kotlin) | Gradle `com.sdkwork:utils` |
| C# | [`packages/sdkwork-utils-csharp`](packages/sdkwork-utils-csharp) | NuGet `Sdkwork.Utils` |
| PHP | [`packages/sdkwork-utils-php`](packages/sdkwork-utils-php) | Composer `sdkwork/utils-php` |

Each package has its own README with language-specific examples and verification commands.

## Modules (v0.8)

| Module | Responsibility |
| --- | --- |
| `string` | Blank checks, trim, case conversion, slugify, mask, pad, split/join, repeat, normalize whitespace, **template** |
| `datetime` | UTC instants, ISO-8601 parse/format, arithmetic, unix millis, same-instant check |
| `number` | Clamp, round, parse, percent format, in-range check, abs |
| `currency` | ISO 4217 codes, minor units, locale currency format |
| `bloom` | Probabilistic set membership (create, add, might_contain, sizing helpers) |
| `bytes` | Human-readable byte sizes (`format_bytes`, 1024-based) |
| `collection` | unique, chunk, group_by, flatten, compact, first, last, sort_by, key_by, filter, find |
| `validation` | email, uuid, url, numeric, ipv4 |
| `id` | UUID v4, random alphanumeric strings |
| `encoding` | base64, hex, url encode/decode |
| `path` | Logical path join and normalize |
| `object` | pick, omit, dot-path get/set/has, deep/shallow merge, compact, keys, values |
| `crypto` | SHA-256, HMAC-SHA256, secure compare |
| `optional` | coalesce, default_if_blank |
| `result` | Lightweight ok/err result type |
| `i18n` | Locale number and datetime formatting |
| `boolean` | parse_bool, is_truthy |
| `compare` | deep_equal for JSON-like structures |

See [`specs/naming.aliases.json`](specs/naming.aliases.json) for idiomatic export names per language.

## Repository layout

```
sdkwork-utils/
├── AGENTS.md                 # Agent execution rules (SDKWork standard)
├── specs/
│   ├── utils.contract.json   # Authoritative API contract
│   ├── naming.aliases.json   # Per-language export naming
│   └── conformance/
│       └── fixtures.json     # Shared test vectors
├── scripts/
│   ├── verify.mjs            # Run all language tests
│   ├── verify-contract.mjs   # Contract vs fixtures check
│   ├── audit-conformance-coverage.mjs  # Per-language conformance coverage gate
│   ├── check-modules.mjs     # 18 modules × 8 languages parity
│   └── sync-conformance-fixtures.mjs
├── packages/sdkwork-utils-*/
└── .github/workflows/verify.yml
```

## Verification

```bash
pnpm verify
```

Runs contract validation (every operation must have a fixture key), fixture sync, module parity check, conformance coverage audit, and every language test available on the current machine. CI runs the same workflow on push and pull requests.

Coverage thresholds live in [`specs/conformance/coverage-thresholds.json`](specs/conformance/coverage-thresholds.json). TypeScript, Python, and PHP require 100% fixture iteration; other languages have phased minimums that rise over time.

## Contributing

1. Update `specs/utils.contract.json` and `specs/conformance/fixtures.json` first.
2. Implement the change in **all eight** language packages.
3. Run `pnpm verify` and fix any parity or conformance failures.
4. Update package READMEs when public usage changes.

Breaking contract changes require a major version bump and human review (see `AGENTS.md`).

## License

MIT — see [`LICENSE`](LICENSE).

## Documentation Canon

- [docs/README.md](docs/README.md)
- [docs/product/prd/PRD.md](docs/product/prd/PRD.md)
- [docs/architecture/tech/TECH_ARCHITECTURE.md](docs/architecture/tech/TECH_ARCHITECTURE.md)

