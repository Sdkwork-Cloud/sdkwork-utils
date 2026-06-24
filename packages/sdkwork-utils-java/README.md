# sdkwork-utils-java

Domain: shared utilities  
Capability: cross-language common helpers  
Package type: library  
Status: standard  
Version: 0.10.0

Java 21 implementation of the SDKWork utils contract.

## Public API

```java
import com.sdkwork.utils.StringUtils;
import com.sdkwork.utils.DateTimeUtils;
import com.sdkwork.utils.BooleanUtils;

StringUtils.isBlank("  "); // true
StringUtils.camelCase("hello_world"); // "helloWorld"
BooleanUtils.parseBool("true"); // Boolean.TRUE
```

Static methods on `*Utils` classes. See [`../../specs/naming.aliases.json`](../../specs/naming.aliases.json).

## Configuration

Maven coordinates: `com.sdkwork:sdkwork-utils-java:0.10.0`

```xml
<dependency>
  <groupId>com.sdkwork</groupId>
  <artifactId>sdkwork-utils-java</artifactId>
  <version>0.10.0</version>
</dependency>
```

## Verification

```bash
cd packages/sdkwork-utils-java
mvn test
```

Conformance: `ConformanceTest.java` (fixtures synced from repo root).

## Related specs

- [`../../specs/utils.contract.json`](../../specs/utils.contract.json)
- [`../../AGENTS.md`](../../AGENTS.md)
