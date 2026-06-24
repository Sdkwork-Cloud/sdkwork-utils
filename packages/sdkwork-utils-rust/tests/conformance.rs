use sdkwork_utils_rust::{
    add, base64_encode, base64url_decode, base64url_encode, bit_count, clamp, coalesce, create,
    deep_clone, default_if_blank, diff_millis, estimate_bit_count, estimate_hash_count,
    format_number_locale, get_path, hash_count, hex_encode, hmac_sha256, is_blank, is_email,
    is_ipv6, is_phone_e164, join_path, might_contain, parse_datetime, parse_number_locale,
    set_path, sha256_hash, slugify, unique, ResultValue,
};
use serde_json::{json, Value};
use std::fs;

#[test]
fn conformance_fixtures() {
    let fixtures: Value = serde_json::from_str(
        &fs::read_to_string(concat!(
            env!("CARGO_MANIFEST_DIR"),
            "/../../specs/conformance/fixtures.json"
        ))
        .expect("fixtures"),
    )
    .expect("json");

    for item in fixtures["string"]["is_blank"].as_array().unwrap() {
        let input = item["input"].as_str();
        assert_eq!(is_blank(input), item["output"].as_bool().unwrap());
    }

    let slug = &fixtures["string"]["slugify"][0];
    assert_eq!(
        slugify(slug["input"].as_str().unwrap()),
        slug["output"].as_str().unwrap()
    );

    let diff = &fixtures["datetime"]["diff_millis"];
    let earlier = parse_datetime(diff["earlier"].as_str().unwrap(), None).unwrap();
    let later = parse_datetime(diff["later"].as_str().unwrap(), None).unwrap();
    assert_eq!(
        diff_millis(earlier, later),
        diff["output"].as_i64().unwrap()
    );

    let hello = b"hello";
    assert_eq!(
        base64_encode(hello),
        fixtures["encoding"]["base64_encode"][0]["output"]
            .as_str()
            .unwrap()
    );
    assert_eq!(
        hex_encode(hello),
        fixtures["encoding"]["hex_encode"][0]["output"]
            .as_str()
            .unwrap()
    );

    let base64url = &fixtures["encoding"]["base64url_encode"][0];
    assert_eq!(
        base64url_encode(base64url["input"].as_str().unwrap().as_bytes()),
        base64url["output"].as_str().unwrap()
    );
    let base64url_decode_item = &fixtures["encoding"]["base64url_decode"][0];
    assert_eq!(
        String::from_utf8(
            base64url_decode(base64url_decode_item["input"].as_str().unwrap()).unwrap()
        )
        .unwrap(),
        base64url_decode_item["output"].as_str().unwrap()
    );

    let merge = &fixtures["object"]["deep_merge"];
    let merged = sdkwork_utils_rust::deep_merge(&merge["base"], &merge["overlay"]);
    assert_eq!(merged, merge["output"]);

    let sha = &fixtures["crypto"]["sha256_hash"][0];
    assert_eq!(
        sha256_hash(sha["input"].as_str().unwrap().as_bytes()),
        sha["output"].as_str().unwrap()
    );

    let hmac = &fixtures["crypto"]["hmac_sha256"][0];
    assert_eq!(
        hmac_sha256(
            hmac["input"].as_str().unwrap().as_bytes(),
            hmac["secret"].as_str().unwrap().as_bytes()
        ),
        hmac["output"].as_str().unwrap()
    );

    assert_eq!(
        coalesce(&[None, Some(""), Some("  "), Some("ok")]),
        Some("ok")
    );
    let default_item = &fixtures["optional"]["default_if_blank"][0];
    assert_eq!(
        default_if_blank(
            Some(default_item["input"].as_str().unwrap()),
            default_item["default"].as_str().unwrap()
        ),
        default_item["output"].as_str().unwrap()
    );

    let ok_item = &fixtures["result"]["unwrap_or"][0];
    assert_eq!(
        ResultValue::ok(ok_item["value"].as_i64().unwrap() as i32).unwrap_or(0),
        ok_item["output"].as_i64().unwrap() as i32
    );

    let clamp_item = &fixtures["number"]["clamp"][0];
    assert_eq!(
        clamp(
            clamp_item["value"].as_f64().unwrap(),
            clamp_item["min"].as_f64().unwrap(),
            clamp_item["max"].as_f64().unwrap()
        ),
        clamp_item["output"].as_f64().unwrap()
    );

    let unique_item = &fixtures["collection"]["unique"][0];
    let input: Vec<i64> = unique_item["input"]
        .as_array()
        .unwrap()
        .iter()
        .map(|v| v.as_i64().unwrap())
        .collect();
    let expected: Vec<i64> = unique_item["output"]
        .as_array()
        .unwrap()
        .iter()
        .map(|v| v.as_i64().unwrap())
        .collect();
    assert_eq!(unique(input), expected);

    let email_item = &fixtures["validation"]["is_email"][0];
    assert_eq!(
        is_email(email_item["input"].as_str().unwrap()),
        email_item["output"].as_bool().unwrap()
    );
    for item in fixtures["validation"]["is_ipv6"].as_array().unwrap() {
        assert_eq!(
            is_ipv6(item["input"].as_str().unwrap()),
            item["output"].as_bool().unwrap()
        );
    }
    for item in fixtures["validation"]["is_phone_e164"].as_array().unwrap() {
        assert_eq!(
            is_phone_e164(item["input"].as_str().unwrap()),
            item["output"].as_bool().unwrap()
        );
    }

    let path_item = &fixtures["path"]["join_path"][0];
    let segments: Vec<&str> = path_item["segments"]
        .as_array()
        .unwrap()
        .iter()
        .map(|v| v.as_str().unwrap())
        .collect();
    assert_eq!(join_path(&segments), path_item["output"].as_str().unwrap());

    let number_item = &fixtures["i18n"]["format_number_locale"][0];
    assert_eq!(
        format_number_locale(
            number_item["value"].as_f64().unwrap(),
            number_item["locale"].as_str().unwrap(),
            number_item["decimals"].as_u64().unwrap() as u32
        ),
        number_item["output"].as_str().unwrap()
    );
    for item in fixtures["i18n"]["parse_number_locale"].as_array().unwrap() {
        let parsed = parse_number_locale(
            item["input"].as_str().unwrap(),
            item["locale"].as_str().unwrap(),
        );
        if item["output"].is_null() {
            assert!(parsed.is_none());
        } else {
            assert_eq!(parsed, Some(item["output"].as_f64().unwrap()));
        }
    }

    let clone_item = &fixtures["compare"]["deep_clone"][0];
    let mut cloned = deep_clone(&clone_item["input"]);
    cloned["b"][1]["c"] = json!(99);
    assert_eq!(clone_item["input"]["b"][1]["c"], json!(3));

    let mut target = Value::Object(Default::default());
    let path_case = &fixtures["object"]["set_get_path"];
    set_path(
        &mut target,
        path_case["path"].as_str().unwrap(),
        Value::String(path_case["value"].as_str().unwrap().to_string()),
    );
    assert_eq!(
        get_path(&target, path_case["path"].as_str().unwrap()),
        Some(Value::String(
            path_case["output"].as_str().unwrap().to_string()
        ))
    );

    let bloom = &fixtures["bloom"];
    let create_item = &bloom["create"][0];
    let filter = create(
        create_item["expected_items"].as_u64().unwrap() as usize,
        create_item["false_positive_rate"].as_f64().unwrap(),
    );
    assert_eq!(
        bit_count(&filter),
        create_item["bit_count"].as_u64().unwrap() as usize
    );
    assert_eq!(
        hash_count(&filter),
        create_item["hash_count"].as_u64().unwrap() as usize
    );

    let estimate_bit = &bloom["estimate_bit_count"][0];
    assert_eq!(
        estimate_bit_count(
            estimate_bit["expected_items"].as_u64().unwrap() as usize,
            estimate_bit["false_positive_rate"].as_f64().unwrap(),
        ),
        estimate_bit["output"].as_u64().unwrap() as usize
    );

    let estimate_hash = &bloom["estimate_hash_count"][0];
    assert_eq!(
        estimate_hash_count(
            estimate_hash["expected_items"].as_u64().unwrap() as usize,
            estimate_hash["bit_count"].as_u64().unwrap() as usize,
        ),
        estimate_hash["output"].as_u64().unwrap() as usize
    );

    let might = &bloom["might_contain"][0];
    let mut membership = create(128, 0.01);
    for value in might["added"].as_array().unwrap() {
        add(&mut membership, value.as_str().unwrap());
    }
    assert!(might_contain(
        &membership,
        might["present"].as_str().unwrap()
    ));
    assert!(!might_contain(
        &membership,
        might["absent"].as_str().unwrap()
    ));
}
