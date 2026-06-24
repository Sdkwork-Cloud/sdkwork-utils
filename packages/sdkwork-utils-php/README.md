# sdkwork/utils-php

Domain: shared utilities  
Capability: cross-language common helpers  
Package type: library  
Status: standard  
Version: 0.10.0

PHP 8.2+ implementation of the SDKWork utils contract.

## Public API

```php
use Sdkwork\Utils\StringUtils;
use Sdkwork\Utils\BooleanUtils;
use Sdkwork\Utils\EncodingUtils;

StringUtils::isBlank('  '); // true
StringUtils::camelCase('hello_world'); // helloWorld
EncodingUtils::urlEncode('hello world'); // hello%20world
BooleanUtils::parseBool('true'); // true
```

PSR-4 namespace: `Sdkwork\Utils\`.

## Configuration

```bash
composer require sdkwork/utils-php
```

Local development:

```bash
cd packages/sdkwork-utils-php
composer install
```

## Verification

```bash
composer test
```

## Related specs

- [`../../specs/utils.contract.json`](../../specs/utils.contract.json)
- [`../../AGENTS.md`](../../AGENTS.md)
