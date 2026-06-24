# sdkwork-utils-kotlin

Domain: shared utilities  
Capability: cross-language common helpers  
Package type: library  
Status: standard  
Version: 0.10.0

Kotlin/JVM implementation of the SDKWork utils contract.

## Public API

```kotlin
import com.sdkwork.utils.StringUtils
import com.sdkwork.utils.BooleanUtils

StringUtils.isBlank("  ") // true
StringUtils.camelCase("hello_world") // helloWorld
BooleanUtils.parseBool("true") // true
```

Utility `object` classes with camelCase methods.

## Configuration

Gradle (Kotlin DSL):

```kotlin
dependencies {
    implementation("com.sdkwork:utils:0.10.0")
}
```

Or use the local project path in a composite build.

## Verification

```bash
cd packages/sdkwork-utils-kotlin
./gradlew test
```

## Related specs

- [`../../specs/utils.contract.json`](../../specs/utils.contract.json)
- [`../../AGENTS.md`](../../AGENTS.md)
