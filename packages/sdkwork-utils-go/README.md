# sdkwork-utils-go

Domain: shared utilities  
Capability: cross-language common helpers  
Package type: library  
Status: standard  
Version: 0.10.0

Go implementation of the SDKWork utils contract.

## Public API

Package name: `utils`

```go
import "path/to/sdkwork-utils/packages/sdkwork-utils-go/utils"

utils.IsBlank(nil) // true
utils.CamelCase("hello_world") // "helloWorld"
utils.UrlEncode("hello world") // "hello%20world"
```

Exports use PascalCase. See [`../../specs/naming.aliases.json`](../../specs/naming.aliases.json).

## Configuration

Add a module replace or copy the package into your module. Go module path should match your workspace layout.

## Verification

```bash
cd packages/sdkwork-utils-go
go test ./...
```

Conformance: `conformance_test.go`.

## Related specs

- [`../../specs/utils.contract.json`](../../specs/utils.contract.json)
- [`../../AGENTS.md`](../../AGENTS.md)
