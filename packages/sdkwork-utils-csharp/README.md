# Sdkwork.Utils (C#)

Domain: shared utilities  
Capability: cross-language common helpers  
Package type: library  
Status: standard  
Version: 0.10.0

.NET 8 implementation of the SDKWork utils contract.

## Public API

```csharp
using Sdkwork.Utils;

StringUtils.IsBlank("  "); // true
StringUtils.CamelCase("hello_world"); // "helloWorld"
BooleanUtils.ParseBool("true"); // true
EncodingUtils.UrlEncode("hello world"); // "hello%20world"
```

Namespace: `Sdkwork.Utils`. Result type uses `ResultValue.Success` / `ResultValue.Failure`.

## Configuration

```bash
dotnet add package Sdkwork.Utils
```

Or project reference to `Sdkwork.Utils.csproj`.

## Verification

```bash
cd packages/sdkwork-utils-csharp
dotnet test Sdkwork.Utils.Tests/Sdkwork.Utils.Tests.csproj
```

## Related specs

- [`../../specs/utils.contract.json`](../../specs/utils.contract.json)
- [`../../AGENTS.md`](../../AGENTS.md)
