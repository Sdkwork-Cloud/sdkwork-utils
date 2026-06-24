# sdkwork-utils specs

Authoritative contracts for the cross-language utility library.

| File | Purpose |
| --- | --- |
| [`utils.contract.json`](utils.contract.json) | Module and operation definitions (v0.4, 90 operations) |
| [`naming.aliases.json`](naming.aliases.json) | Idiomatic export names per language |
| [`conformance/fixtures.json`](conformance/fixtures.json) | Shared behavioral test vectors |

When changing public behavior:

1. Update `utils.contract.json` and bump version if breaking.
2. Add or update fixtures in `conformance/fixtures.json`.
3. Implement in all eight language packages under `packages/`.
4. Run `pnpm verify` from repository root.

See [`../AGENTS.md`](../AGENTS.md) for agent execution rules.
