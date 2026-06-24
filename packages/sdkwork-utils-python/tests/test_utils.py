from sdkwork_utils.crypto import hmac_sha256, sha256_hash
from sdkwork_utils.datetime import add_hours, diff_millis, parse_datetime
from sdkwork_utils.encoding import base64_encode, hex_encode
from sdkwork_utils.i18n import format_datetime_locale_str, format_number_locale
from sdkwork_utils.object import deep_merge, get_path, set_path
from sdkwork_utils.optional import coalesce, default_if_blank
from sdkwork_utils.result import ResultValue
from sdkwork_utils.string import camel_case, is_blank, slugify, truncate


def test_string_helpers() -> None:
    assert is_blank("  ")
    assert camel_case("hello_world") == "helloWorld"
    assert slugify("Hello, SDKWork!") == "hello-sdk-work"
    assert truncate("abcdef", 5) == "ab..."


def test_datetime_helpers() -> None:
    first = parse_datetime("2024-01-01T00:00:00.000Z")
    assert first is not None
    second = add_hours(first, 2)
    assert diff_millis(first, second) == 7_200_000


def test_encoding_helpers() -> None:
    data = b"hello"
    assert base64_encode(data) == "aGVsbG8="
    assert hex_encode(data) == "68656c6c6f"


def test_object_and_crypto_helpers() -> None:
    merged = deep_merge({"a": 1, "nested": {"x": 1}}, {"b": 2, "nested": {"y": 2}})
    assert merged == {"a": 1, "b": 2, "nested": {"x": 1, "y": 2}}
    target: dict = {}
    set_path(target, "user.city", "Paris")
    assert get_path(target, "user.city") == "Paris"
    assert (
        sha256_hash("hello")
        == "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824"
    )
    assert (
        hmac_sha256("payload", "secret")
        == "b82fcb791acec57859b989b430a826488ce2e479fdf92326bd0a2e8375a42ba4"
    )


def test_optional_result_and_i18n_helpers() -> None:
    assert coalesce(None, "", "  ", "ok") == "ok"
    assert default_if_blank("  ", "fallback") == "fallback"
    assert ResultValue.ok(42).unwrap_or(0) == 42
    assert ResultValue.err("fail").unwrap_or(0) == 0
    assert format_number_locale(1234.5, "en-US", 2) == "1,234.50"
    assert format_number_locale(1234.5, "de-DE", 2) == "1.234,50"
    formatted = format_datetime_locale_str("2024-06-15T14:30:00.000Z", "en-US")
    assert formatted is not None and "2024" in formatted
