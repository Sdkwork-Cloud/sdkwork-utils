import json
import re
from pathlib import Path

from sdkwork_utils.boolean import is_truthy, parse_bool
from sdkwork_utils.collection import (
    chunk,
    compact,
    filter as filter_items,
    find,
    first,
    flatten,
    group_by,
    key_by,
    last,
    sort_by,
    unique,
)
from sdkwork_utils.compare import deep_clone, deep_equal
from sdkwork_utils.bloom import add, create, estimate_bit_count, estimate_hash_count, might_contain
from sdkwork_utils.currency import (
    format_currency,
    from_minor_units,
    is_currency_code,
    minor_unit_exponent,
    to_minor_units,
)
from sdkwork_utils.crypto import hmac_sha256, secure_compare, sha256_hash
from sdkwork_utils.datetime import (
    add_days,
    add_hours,
    add_minutes,
    diff_millis,
    end_of_day_utc,
    format_datetime,
    from_unix_millis,
    is_after,
    is_before,
    is_same_instant,
    now,
    parse_datetime,
    start_of_day_utc,
    to_unix_millis,
)
from sdkwork_utils.encoding import (
    base64_decode,
    base64_encode,
    base64url_decode,
    base64url_encode,
    hex_decode,
    hex_encode,
    url_decode,
    url_encode,
)
from sdkwork_utils.i18n import format_datetime_locale_str, format_number_locale, parse_number_locale
from sdkwork_utils.id import random_string, uuid
from sdkwork_utils.number import (
    abs,
    clamp,
    format_number,
    in_range,
    is_integer,
    parse_int,
    parse_number,
    percent_format,
    round_number,
)
from sdkwork_utils.object import (
    compact_map,
    deep_merge,
    get_path,
    has_path,
    keys,
    omit,
    pick,
    set_path,
    shallow_merge,
    values,
)
from sdkwork_utils.optional import coalesce, default_if_blank
from sdkwork_utils.path import join_path, normalize_path
from sdkwork_utils.result import ResultValue
from sdkwork_utils.string import (
    camel_case,
    capitalize,
    contains,
    ends_with,
    is_blank,
    join,
    kebab_case,
    mask,
    pad_end,
    pad_start,
    replace_all,
    repeat,
    slugify,
    snake_case,
    split,
    starts_with,
    trim,
    truncate,
    normalize_whitespace,
    template,
)
from sdkwork_utils.bytes import format_bytes
from sdkwork_utils.validation import is_email, is_ipv4, is_ipv6, is_numeric, is_phone_e164, is_url, is_uuid

FIXTURES = json.loads(
    Path(__file__).resolve().parents[3].joinpath("specs", "conformance", "fixtures.json").read_text(encoding="utf-8")
)
UUID_V4_PATTERN = re.compile(
    r"^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$",
    re.IGNORECASE,
)


def test_conformance_fixtures() -> None:
    for item in FIXTURES["string"]["is_blank"]:
        assert is_blank(item["input"]) is item["output"]

    for item in FIXTURES["string"]["trim"]:
        assert trim(item["input"]) == item["output"]

    for item in FIXTURES["string"]["truncate"]:
        assert truncate(item["input"], item["max_len"], item["suffix"]) == item["output"]

    for item in FIXTURES["string"]["capitalize"]:
        assert capitalize(item["input"]) == item["output"]

    for item in FIXTURES["string"]["camel_case"]:
        assert camel_case(item["input"]) == item["output"]

    for item in FIXTURES["string"]["snake_case"]:
        assert snake_case(item["input"]) == item["output"]

    for item in FIXTURES["string"]["kebab_case"]:
        assert kebab_case(item["input"]) == item["output"]

    for item in FIXTURES["string"]["slugify"]:
        assert slugify(item["input"]) == item["output"]

    for item in FIXTURES["string"]["mask"]:
        assert mask(item["input"], item["visible_start"], item["visible_end"]) == item["output"]

    for item in FIXTURES["string"]["pad_start"]:
        assert pad_start(item["input"], item["target_len"]) == item["output"]

    for item in FIXTURES["string"]["pad_end"]:
        assert pad_end(item["input"], item["target_len"]) == item["output"]

    for item in FIXTURES["string"]["starts_with"]:
        assert starts_with(item["input"], item["prefix"]) is item["output"]

    for item in FIXTURES["string"]["ends_with"]:
        assert ends_with(item["input"], item["suffix"]) is item["output"]

    for item in FIXTURES["string"]["contains"]:
        assert contains(item["input"], item["substring"]) is item["output"]

    for item in FIXTURES["string"]["replace_all"]:
        assert replace_all(item["input"], item["search"], item["replacement"]) == item["output"]

    for item in FIXTURES["string"]["split"]:
        assert split(item["input"], item["delimiter"], item["trim_parts"]) == item["output"]

    for item in FIXTURES["string"]["join"]:
        assert join(item["parts"], item["separator"]) == item["output"]

    for item in FIXTURES["string"]["repeat"]:
        assert repeat(item["input"], item["count"]) == item["output"]

    for item in FIXTURES["string"]["normalize_whitespace"]:
        assert normalize_whitespace(item["input"]) == item["output"]

    for item in FIXTURES["string"]["template"]:
        assert template(item["template"], item["values"]) == item["output"]

    for item in FIXTURES["datetime"]["now"]:
        assert isinstance(now(), object) is item["valid"]

    for item in FIXTURES["datetime"]["format"]:
        parsed = parse_datetime(item["input"])
        assert parsed is not None
        assert format_datetime(parsed) == item["output"]

    for item in FIXTURES["datetime"]["parse"]:
        assert (parse_datetime(item["input"]) is not None) is item["valid"]

    diff = FIXTURES["datetime"]["diff_millis"]
    earlier = parse_datetime(diff["earlier"])
    later = parse_datetime(diff["later"])
    assert earlier is not None and later is not None
    assert diff_millis(earlier, later) == diff["output"]

    for item in FIXTURES["datetime"]["add_days"]:
        parsed = parse_datetime(item["input"])
        assert parsed is not None
        assert format_datetime(add_days(parsed, item["days"])) == item["output"]

    for item in FIXTURES["datetime"]["add_hours"]:
        parsed = parse_datetime(item["input"])
        assert parsed is not None
        assert format_datetime(add_hours(parsed, item["hours"])) == item["output"]

    for item in FIXTURES["datetime"]["add_minutes"]:
        parsed = parse_datetime(item["input"])
        assert parsed is not None
        assert format_datetime(add_minutes(parsed, item["minutes"])) == item["output"]

    for item in FIXTURES["datetime"]["is_before"]:
        left = parse_datetime(item["left"])
        right = parse_datetime(item["right"])
        assert left is not None and right is not None
        assert is_before(left, right) is item["output"]

    for item in FIXTURES["datetime"]["is_after"]:
        left = parse_datetime(item["left"])
        right = parse_datetime(item["right"])
        assert left is not None and right is not None
        assert is_after(left, right) is item["output"]

    for item in FIXTURES["datetime"]["start_of_day_utc"]:
        parsed = parse_datetime(item["input"])
        assert parsed is not None
        assert format_datetime(start_of_day_utc(parsed)) == item["output"]

    for item in FIXTURES["datetime"]["end_of_day_utc"]:
        parsed = parse_datetime(item["input"])
        assert parsed is not None
        assert format_datetime(end_of_day_utc(parsed)) == item["output"]

    for item in FIXTURES["datetime"]["to_unix_millis"]:
        parsed = parse_datetime(item["input"])
        assert parsed is not None
        assert to_unix_millis(parsed) == item["output"]

    for item in FIXTURES["datetime"]["from_unix_millis"]:
        parsed = from_unix_millis(item["input"])
        assert parsed is not None
        assert format_datetime(parsed) == item["output"]

    for item in FIXTURES["datetime"]["is_same_instant"]:
        left = parse_datetime(item["left"])
        right = parse_datetime(item["right"])
        assert left is not None and right is not None
        assert is_same_instant(left, right) is item["output"]

    for item in FIXTURES["encoding"]["base64_encode"]:
        data = item["input"].encode("utf-8")
        assert base64_encode(data) == item["output"]

    for item in FIXTURES["encoding"]["base64_decode"]:
        decoded = base64_decode(item["input"])
        assert decoded is not None
        assert decoded.decode("utf-8") == item["output"]

    for item in FIXTURES["encoding"]["hex_encode"]:
        data = item["input"].encode("utf-8")
        assert hex_encode(data) == item["output"]

    for item in FIXTURES["encoding"]["hex_decode"]:
        decoded = hex_decode(item["input"])
        assert decoded is not None
        assert decoded.decode("utf-8") == item["output"]

    for item in FIXTURES["encoding"]["url_encode"]:
        assert url_encode(item["input"]) == item["output"]

    for item in FIXTURES["encoding"]["url_decode"]:
        assert url_decode(item["input"]) == item["output"]

    for item in FIXTURES["encoding"]["base64url_encode"]:
        assert base64url_encode(item["input"]) == item["output"]

    for item in FIXTURES["encoding"]["base64url_decode"]:
        assert base64url_decode(item["input"]).decode("utf-8") == item["output"]

    merge = FIXTURES["object"]["deep_merge"]
    assert deep_merge(merge["base"], merge["overlay"]) == merge["output"]

    shallow = FIXTURES["object"]["shallow_merge"]
    assert shallow_merge(shallow["base"], shallow["overlay"]) == shallow["output"]

    path_item = FIXTURES["object"]["set_get_path"]
    target: dict = {}
    set_path(target, path_item["path"], path_item["value"])
    assert get_path(target, path_item["path"]) == path_item["output"]

    for item in FIXTURES["object"]["get_path"]:
        assert get_path(item["source"], item["path"]) == item["output"]

    for item in FIXTURES["object"]["set_path"]:
        result: dict = {}
        set_path(result, item["path"], item["value"])
        assert get_path(result, item["path"]) == item["read_back"]

    for item in FIXTURES["object"]["pick"]:
        assert pick(item["source"], item["keys"]) == item["output"]

    for item in FIXTURES["object"]["omit"]:
        assert omit(item["source"], item["keys"]) == item["output"]

    for item in FIXTURES["object"]["compact"]:
        assert compact_map(item["input"]) == item["output"]

    path_source = {"user": {"name": "Ada"}}
    for item in FIXTURES["object"]["has_path"]:
        assert has_path(path_source, item["path"]) is item["exists"]

    for item in FIXTURES["object"]["keys"]:
        assert keys(item["input"]) == item["output"]

    for item in FIXTURES["object"]["values"]:
        assert values(item["input"]) == item["output"]

    for item in FIXTURES["crypto"]["sha256_hash"]:
        assert sha256_hash(item["input"]) == item["output"]

    for item in FIXTURES["crypto"]["hmac_sha256"]:
        assert hmac_sha256(item["input"], item["secret"]) == item["output"]

    for item in FIXTURES["crypto"]["secure_compare"]:
        assert secure_compare(item["left"], item["right"]) is item["output"]

    for item in FIXTURES["number"]["clamp"]:
        assert clamp(item["value"], item["min"], item["max"]) == item["output"]

    for item in FIXTURES["number"]["round"]:
        assert round_number(item["value"], item["decimals"]) == item["output"]

    for item in FIXTURES["number"]["format_number"]:
        assert format_number(item["value"], item["decimals"]) == item["output"]

    for item in FIXTURES["number"]["parse_number"]:
        assert parse_number(item["input"]) == item["output"]

    for item in FIXTURES["number"]["is_integer"]:
        assert is_integer(item["value"]) is item["output"]

    for item in FIXTURES["number"]["parse_int"]:
        assert parse_int(item["input"]) == item["output"]

    for item in FIXTURES["number"]["percent_format"]:
        assert percent_format(item["value"], item["decimals"]) == item["output"]

    for item in FIXTURES["number"]["in_range"]:
        assert in_range(item["value"], item["min"], item["max"]) is item["output"]

    for item in FIXTURES["number"]["abs"]:
        assert abs(item["input"]) == item["output"]

    for item in FIXTURES["collection"]["unique"]:
        assert unique(item["input"]) == item["output"]

    for item in FIXTURES["collection"]["chunk"]:
        assert chunk(item["input"], item["size"]) == item["output"]

    for item in FIXTURES["collection"]["flatten"]:
        assert flatten(item["input"]) == item["output"]

    for item in FIXTURES["collection"]["compact"]:
        assert compact(item["input"]) == item["output"]

    for item in FIXTURES["collection"]["group_by"]:
        grouped = group_by(item["input"], lambda entry: entry["type"])
        assert grouped == item["output"]

    for item in FIXTURES["collection"]["first"]:
        assert first(item["input"]) == item["output"]

    for item in FIXTURES["collection"]["last"]:
        assert last(item["input"]) == item["output"]

    for item in FIXTURES["collection"]["sort_by"]:
        sorted_items = sort_by(item["input"], lambda entry: entry["k"])
        assert sorted_items == item["output"]

    for item in FIXTURES["collection"]["key_by"]:
        keyed = key_by(item["input"], lambda entry: entry["id"])
        assert keyed == item["output"]

    for item in FIXTURES["collection"]["filter"]:
        assert filter_items(item["input"], lambda value: value > item["threshold"]) == item["output"]

    for item in FIXTURES["collection"]["find"]:
        assert find(item["input"], lambda value: value > item["threshold"]) == item["output"]

    for item in FIXTURES["validation"]["is_email"]:
        assert is_email(item["input"]) is item["output"]

    for item in FIXTURES["validation"]["is_uuid"]:
        assert is_uuid(item["input"]) is item["output"]

    for item in FIXTURES["validation"]["is_url"]:
        assert is_url(item["input"]) is item["output"]

    for item in FIXTURES["validation"]["is_numeric"]:
        assert is_numeric(item["input"]) is item["output"]

    for item in FIXTURES["validation"]["is_ipv4"]:
        assert is_ipv4(item["input"]) is item["output"]

    for item in FIXTURES["validation"]["is_ipv6"]:
        assert is_ipv6(item["input"]) is item["output"]

    for item in FIXTURES["validation"]["is_phone_e164"]:
        assert is_phone_e164(item["input"]) is item["output"]

    for item in FIXTURES["id"]["uuid"]:
        assert UUID_V4_PATTERN.match(uuid()) is not None
        assert item["pattern"] == "uuid-v4"

    for item in FIXTURES["id"]["random_string"]:
        value = random_string(item["length"])
        assert len(value) == item["length"]
        assert re.fullmatch(r"[A-Za-z0-9]+", value) is not None

    for item in FIXTURES["path"]["join_path"]:
        assert join_path(*item["segments"]) == item["output"]

    for item in FIXTURES["path"]["normalize_path"]:
        assert normalize_path(item["input"]) == item["output"]

    for item in FIXTURES["i18n"]["format_number_locale"]:
        assert format_number_locale(item["value"], item["locale"], item["decimals"]) == item["output"]

    for item in FIXTURES["i18n"]["format_datetime_locale"]:
        formatted = format_datetime_locale_str(item["input"], item["locale"])
        assert formatted is not None
        assert item["contains"] in formatted

    for item in FIXTURES["i18n"]["parse_number_locale"]:
        assert parse_number_locale(item["input"], item["locale"]) == item["output"]

    for item in FIXTURES["optional"]["coalesce"]:
        assert coalesce(*item["values"]) == item["output"]

    for item in FIXTURES["optional"]["default_if_blank"]:
        assert default_if_blank(item["input"], item["default"]) == item["output"]

    for item in FIXTURES["result"]["ok"]:
        result = ResultValue.ok(item["value"])
        assert result.is_ok() is item["is_ok"]
        assert result.is_err() is item["is_err"]

    for item in FIXTURES["result"]["err"]:
        result = ResultValue.err(item["message"])
        assert result.is_ok() is item["is_ok"]
        assert result.is_err() is item["is_err"]

    for item in FIXTURES["result"]["is_ok"]:
        result = ResultValue.ok(item["value"]) if item["kind"] == "ok" else ResultValue.err(item["message"])
        assert result.is_ok() is item["output"]

    for item in FIXTURES["result"]["is_err"]:
        result = ResultValue.ok(item["value"]) if item["kind"] == "ok" else ResultValue.err(item["message"])
        assert result.is_err() is item["output"]

    for item in FIXTURES["result"]["unwrap_or"]:
        result = ResultValue.ok(item["value"]) if item["kind"] == "ok" else ResultValue.err(item["message"])
        assert result.unwrap_or(item["default"]) == item["output"]

    for item in FIXTURES["result"]["map"]:
        mapped = ResultValue.ok(item["value"]).map(lambda value: value * 2)
        assert mapped.value == item["output"]

    for item in FIXTURES["boolean"]["parse_bool"]:
        assert parse_bool(item["input"]) is item["output"]

    for item in FIXTURES["boolean"]["is_truthy"]:
        assert is_truthy(item["input"]) is item["output"]

    for item in FIXTURES["compare"]["deep_equal"]:
        assert deep_equal(item["left"], item["right"]) is item["output"]

    for item in FIXTURES["compare"]["deep_clone"]:
        cloned = deep_clone(item["input"])
        assert cloned == item["output"]
        cloned["b"][1]["c"] = 99
        assert item["input"]["b"][1]["c"] == 3

    for item in FIXTURES["currency"]["is_currency_code"]:
        assert is_currency_code(item["input"]) is item["output"]

    for item in FIXTURES["currency"]["minor_unit_exponent"]:
        assert minor_unit_exponent(item["code"]) == item["output"]

    for item in FIXTURES["currency"]["to_minor_units"]:
        assert to_minor_units(item["amount"], item["code"]) == item["output"]

    for item in FIXTURES["currency"]["from_minor_units"]:
        assert from_minor_units(item["minor"], item["code"]) == item["output"]

    for item in FIXTURES["currency"]["format_currency"]:
        assert format_currency(item["amount"], item["code"], item["locale"]) == item["output"]

    for item in FIXTURES["bloom"]["create"]:
        bloom_filter = create(item["expected_items"], item["false_positive_rate"])
        assert bloom_filter.bit_count == item["bit_count"]
        assert bloom_filter.hash_count == item["hash_count"]

    for item in FIXTURES["bloom"]["estimate_bit_count"]:
        assert estimate_bit_count(item["expected_items"], item["false_positive_rate"]) == item["output"]

    for item in FIXTURES["bloom"]["estimate_hash_count"]:
        assert estimate_hash_count(item["expected_items"], item["bit_count"]) == item["output"]

    for item in FIXTURES["bloom"]["might_contain"]:
        bloom_filter = create(128, 0.01)
        for value in item["added"]:
            add(bloom_filter, value)
        assert might_contain(bloom_filter, item["present"]) is item["present_output"]
        assert might_contain(bloom_filter, item["absent"]) is item["absent_output"]

    for item in FIXTURES["bytes"]["format_bytes"]:
        assert format_bytes(item["value"], item["decimals"]) == item["output"]
