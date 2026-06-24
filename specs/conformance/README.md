# Conformance Fixtures

`fixtures.json` is the single source of truth for cross-language behavior.

Each language package should include a conformance test that loads this file and asserts expected outputs.

Run contract validation:

```bash
pnpm verify:contract
```

Run full verification:

```bash
pnpm verify
```
