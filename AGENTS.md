# Repository Guidelines

## SDKWORK Soul

Read `../sdkwork-specs/SOUL.md` before executing tasks in this repository.

## SDKWORK Standards

Canonical specs path from this repository:

- `../sdkwork-specs/README.md`
- `../sdkwork-specs/SOUL.md`
- `../sdkwork-specs/AGENTS_SPEC.md`
- `../sdkwork-specs/CODE_STYLE_SPEC.md`
- `../sdkwork-specs/NAMING_SPEC.md`
- `../sdkwork-specs/DOCUMENTATION_SPEC.md`
- `../sdkwork-specs/TEST_SPEC.md`

Do not copy root standard bodies into this file.

## Application Identity

This repository is a shared multi-language utility library, not an SDKWork application root. There is no `sdkwork.app.config.json`.

## Local Dictionary Structure

- `AGENTS.md`: agent execution entrypoint for this repository.
- `specs/utils.contract.json`: authoritative cross-language API contract (v0.7, 17 modules, 104 operations).
- `specs/conformance/coverage-thresholds.json`: per-language conformance test coverage minimums.
- `specs/naming.aliases.json`: idiomatic export name aliases per language.
- `specs/conformance/fixtures.json`: shared behavioral test vectors.
- `packages/sdkwork-utils-<language>/`: per-language packages (`rust`, `typescript`, `python`, `go`, `java`, `kotlin`, `csharp`, `php`).
- TypeScript npm package name: `@sdkwork/utils` in `packages/sdkwork-utils-typescript` (see `NAMING_SPEC.md` §4.2).
- `scripts/`: contract verification, module parity, conformance sync, and orchestrated test runner.

## Documentation Canon

- [docs/README.md](docs/README.md)
- [docs/product/prd/PRD.md](docs/product/prd/PRD.md)
- [docs/architecture/tech/TECH_ARCHITECTURE.md](docs/architecture/tech/TECH_ARCHITECTURE.md)

## Spec Resolution Order

1. Read this `AGENTS.md`.
2. Read `specs/utils.contract.json`, `specs/naming.aliases.json`, and `specs/conformance/coverage-thresholds.json` for API parity, naming, and test coverage gates (v0.10, 18 modules, 112 operations).
3. Read `specs/conformance/fixtures.json` when changing behavior or tests.
4. Read the nearest package README for language-specific install and verification.
5. Read task-specific files from `../sdkwork-specs/` (language specs on demand only).
6. Inspect implementation files last.

## Required Specs By Task Type

| Task | Required specs |
| --- | --- |
| Agent/workflow rules | `../sdkwork-specs/SOUL.md`, `../sdkwork-specs/AGENTS_SPEC.md` |
| Contract or API changes | `specs/utils.contract.json`, `../sdkwork-specs/NAMING_SPEC.md`, `../sdkwork-specs/TEST_SPEC.md` |
| Rust code | `../sdkwork-specs/RUST_CODE_SPEC.md`, `../sdkwork-specs/CODE_STYLE_SPEC.md` |
| TypeScript code | `../sdkwork-specs/TYPESCRIPT_CODE_SPEC.md`, `../sdkwork-specs/CODE_STYLE_SPEC.md` |
| Python code | `../sdkwork-specs/CODE_STYLE_SPEC.md`, `../sdkwork-specs/NAMING_SPEC.md` |
| Java code | `../sdkwork-specs/JAVA_CODE_SPEC.md`, `../sdkwork-specs/CODE_STYLE_SPEC.md` |
| Kotlin code | `../sdkwork-specs/JAVA_CODE_SPEC.md`, `../sdkwork-specs/CODE_STYLE_SPEC.md` |
| Go code | `../sdkwork-specs/CODE_STYLE_SPEC.md`, `../sdkwork-specs/NAMING_SPEC.md` |
| C# code | `../sdkwork-specs/CODE_STYLE_SPEC.md`, `../sdkwork-specs/NAMING_SPEC.md` |
| PHP code | `../sdkwork-specs/CODE_STYLE_SPEC.md`, `../sdkwork-specs/NAMING_SPEC.md` |
| Documentation | `../sdkwork-specs/DOCUMENTATION_SPEC.md` |

Language specs are on-demand. Do not load every language spec for a single-language change.

## Code Style Rules

Run commands from the target package directory or repository root as documented in README files. Match existing module layout and naming in each language package. Contract operations use snake_case in `utils.contract.json`; map to idiomatic exports documented in `specs/naming.aliases.json`.

Build scripts, dev runners, and `pnpm clean` must follow `CODE_STYLE_SPEC.md` §7 (Build Source Integrity And Self-Healing). Git-tracked build-critical source files must be verified before builds and self-healed from git when missing; `clean` must not delete them.

## Build, Test, and Verification

From repository root:

```bash
pnpm verify
```

This runs contract validation, conformance fixture sync, module parity check, conformance coverage audit, and all language tests available on the current machine.

Individual commands:

| Language | Command |
| --- | --- |
| Rust | `cargo test --workspace` |
| TypeScript | `pnpm --dir packages/sdkwork-utils-typescript test` |
| Python | `python -m pytest packages/sdkwork-utils-python/tests` |
| Go | `go test ./...` in `packages/sdkwork-utils-go` |
| Java | `mvn test` in `packages/sdkwork-utils-java` |
| Kotlin | `./gradlew test` in `packages/sdkwork-utils-kotlin` |
| C# | `dotnet test` in `packages/sdkwork-utils-csharp` |
| PHP | `composer test` in `packages/sdkwork-utils-php` |

## Agent Execution Rules

- Keep behavioral parity with `specs/utils.contract.json` across all language packages.
- Do not add public operations without updating the contract, fixtures, and every language package.
- Prefer idiomatic implementations per language while preserving shared semantics in the contract `semantics` section.
- Only add utilities that are generic, stable, and reusable across SDKWork applications; avoid app-specific or framework-coupled helpers.
- Do not hand-edit generated SDK output in consumer applications; this repo owns utility source only.
- Stop and report ambiguity when contract semantics, fixture expectations, or language export naming is unclear.

## HTTP API Response Envelope

All L2+ `app-api`, `backend-api`, and SDKWork-owned business `open-api` HTTP contracts `MUST` follow `API_SPEC.md` section 4.5, section 14, and section 15:

- **Input:** typed request bodies, section 14.1 list/search/command input, `SdkWorkListQuery`, and `q` for free-text search.
- **Success output:** `SdkWorkApiResponse` with `{ "code": 0, "data": <payload>, "traceId": "<server-uuid>" }`.
- **Error output:** HTTP 4xx/5xx `application/problem+json` (`ProblemDetail`) with numeric `code` and `traceId`.
- Success `code` is numeric `int32`; HTTP 2xx JSON bodies `MUST` use `0` only. REST semantics remain on HTTP status (`201`, `202`, etc.).
- Platform error codes are numeric non-zero values per section 15.3 (`40001`, `40101`, `40401`, …).
- Single resource: `data.item`
- Lists: `data.items` + `data.pageInfo` (`PageInfo.mode` is `offset` or `cursor`)
- Commands: `data.accepted` plus optional `resourceId` / `status`
- Async accept (`202`): `data.operationId`, `data.status`, optional `pollUrl`

Vendor compatibility `open-api` routes that mirror upstream tool or provider wire (for example OpenAI `/v1/*`, Claude Code, Codex) `MAY` opt out only when every exempt operation declares `x-sdkwork-wire-protocol: external` and `x-sdkwork-external-protocol-id` per `API_SPEC.md` section 4.5.2. SDKWork-owned business `open-api` operations `MUST NOT` opt out.

Errors `MUST` use HTTP 4xx/5xx with `application/problem+json` (`ProblemDetail`) including required numeric `code` and `traceId`. Business failures `MUST NOT` use HTTP 2xx with non-zero `code`, string wire codes, `success`, or human `message`.

Forbidden legacy envelopes and fields: `PlusApiResult`, `AppbaseApiResult`, `StoreApiResult`, `SdkWorkResponse`, per-domain `*ApiResult`, wire field `requestId`, bare domain DTOs at the HTTP root, and top-level `{ items, pageInfo, traceId }` without `data`.

Handlers `MUST` serialize success and map errors through `sdkwork-web-framework` response mapping. Generated HTTP SDKs (`--standard-profile sdkwork-v3`) unwrap `data` by default and expose typed numeric `ProblemDetail.code` / `traceId` on errors; use `.raw` when the full envelope is required.

Before completing API contract, SDK generation, or frontend service work, run:

```bash
node <sdkwork-specs>/tools/check-api-response-envelope.mjs --workspace <workspace-root>
```

Authority: `sdkwork-specs/API_SPEC.md` section 4.5 and sections 14–16, `SDK_SPEC.md` section 4.2, `FRONTEND_SPEC.md`, `MIGRATION_SPEC.md` section 4.2.

## Human Review Rules

Human review is required for:

- Breaking contract changes or major version bumps.
- Semantic changes to shared fixtures (behavior visible to all languages).
- New language packages or removal of a supported language.
- Security-sensitive crypto utilities and validation rule changes.
