use serde_json::Value;

pub fn deep_equal(left: &Value, right: &Value) -> bool {
    left == right
}

pub fn deep_clone(value: &Value) -> Value {
    value.clone()
}

#[cfg(test)]
mod tests {
    use super::*;
    use serde_json::json;

    #[test]
    fn compare_helpers() {
        assert!(deep_equal(
            &json!({"a": 1, "b": [2]}),
            &json!({"b": [2], "a": 1})
        ));
        assert!(!deep_equal(&json!({"a": 1}), &json!({"a": 2})));
        let original = json!({"a": 1, "b": [{"c": 3}]});
        let mut cloned = deep_clone(&original);
        cloned["b"][0]["c"] = json!(99);
        assert_eq!(original["b"][0]["c"], json!(3));
    }
}
