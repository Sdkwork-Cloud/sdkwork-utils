# sdkwork-utils-rust

Domain: shared utilities  
Capability: cross-language common helpers  
Package type: library  
Status: standard  
Version: 0.10.0

Rust implementation of the SDKWork utils contract.

## Public API

Crate: `sdkwork-utils-rust`  
Modules mirror the contract: `string`, `datetime`, `number`, `currency`, `bloom`, `bytes`, `collection`, `validation`, `id`, `encoding`, `path`, `object`, `crypto`, `optional`, `result`, `i18n`, `boolean`, `compare`.

All items are re-exported from the crate root:

```rust
use sdkwork_utils_rust::{is_blank, camel_case, parse_datetime, ResultValue};

assert!(is_blank(None));
assert_eq!(camel_case("hello_world"), "helloWorld");
```

Contract operations use snake_case (see [`../../specs/naming.aliases.json`](../../specs/naming.aliases.json)).

## Configuration

No runtime configuration. Add to your workspace `Cargo.toml`:

```toml
[dependencies]
sdkwork-utils-rust = { path = "../sdkwork-utils/packages/sdkwork-utils-rust" }
```

## Verification

From repository root:

```bash
cargo test --workspace
```

Or:

```bash
pnpm verify
```

Conformance tests: `tests/conformance.rs` (reads shared fixtures).

## Related specs

- [`../../specs/utils.contract.json`](../../specs/utils.contract.json)
- [`../../AGENTS.md`](../../AGENTS.md)
