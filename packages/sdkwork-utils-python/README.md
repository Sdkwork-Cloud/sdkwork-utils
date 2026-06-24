# sdkwork-utils-python

Domain: shared utilities  
Capability: cross-language common helpers  
Package type: library  
Status: standard  
Version: 0.10.0

Python implementation of the SDKWork utils contract.

## Public API

```python
from sdkwork_utils import is_blank, camel_case, parse_datetime, url_encode, parse_bool

assert is_blank("  ")
assert camel_case("hello_world") == "helloWorld"
assert url_encode("hello world") == "hello%20world"
assert parse_bool("true") is True
```

All exports are listed in `sdkwork_utils/__init__.py`. Names use snake_case per contract.

## Configuration

```bash
pip install -e packages/sdkwork-utils-python
```

Requires Python 3.10+.

## Verification

```bash
python -m pytest packages/sdkwork-utils-python/tests -q
```

Set `PYTHONPATH=packages/sdkwork-utils-python` when running from repo root.

## Related specs

- [`../../specs/utils.contract.json`](../../specs/utils.contract.json)
- [`../../AGENTS.md`](../../AGENTS.md)
