use serde_json::{Map, Value};

pub fn pick(source: &Map<String, Value>, keys: &[&str]) -> Map<String, Value> {
    keys.iter()
        .filter_map(|key| {
            source
                .get(*key)
                .map(|value| ((*key).to_string(), value.clone()))
        })
        .collect()
}

pub fn omit(source: &Map<String, Value>, keys: &[&str]) -> Map<String, Value> {
    source
        .iter()
        .filter(|(key, _)| !keys.contains(&key.as_str()))
        .map(|(key, value)| (key.clone(), value.clone()))
        .collect()
}

pub fn get_path(source: &Value, path: &str) -> Option<Value> {
    let mut current = source;
    for segment in path.split('.').filter(|part| !part.is_empty()) {
        current = current.get(segment)?;
    }
    Some(current.clone())
}

pub fn has_path(source: &Value, path: &str) -> bool {
    get_path(source, path)
        .map(|value| !value.is_null())
        .unwrap_or(false)
}

pub fn shallow_merge(base: &Value, overlay: &Value) -> Value {
    match (base.as_object(), overlay.as_object()) {
        (Some(base_map), Some(overlay_map)) => {
            let mut merged = base_map.clone();
            for (key, value) in overlay_map {
                merged.insert(key.clone(), value.clone());
            }
            Value::Object(merged)
        }
        (_, _) => overlay.clone(),
    }
}

pub fn set_path(source: &mut Value, path: &str, value: Value) -> bool {
    let segments: Vec<&str> = path.split('.').filter(|part| !part.is_empty()).collect();
    if segments.is_empty() {
        return false;
    }

    if !source.is_object() {
        *source = Value::Object(Map::new());
    }

    let mut current = source;
    for segment in &segments[..segments.len() - 1] {
        if !current.get(*segment).map(Value::is_object).unwrap_or(false) {
            current
                .as_object_mut()
                .expect("object root")
                .insert((*segment).to_string(), Value::Object(Map::new()));
        }
        current = current
            .as_object_mut()
            .expect("object root")
            .get_mut(*segment)
            .expect("segment object");
    }

    current
        .as_object_mut()
        .expect("object root")
        .insert(segments[segments.len() - 1].to_string(), value);
    true
}

pub fn deep_merge(base: &Value, overlay: &Value) -> Value {
    match (base, overlay) {
        (Value::Object(base_map), Value::Object(overlay_map)) => {
            let mut merged = base_map.clone();
            for (key, overlay_value) in overlay_map {
                merged.insert(
                    key.clone(),
                    if merged.contains_key(key) {
                        deep_merge(merged.get(key).expect("existing key"), overlay_value)
                    } else {
                        overlay_value.clone()
                    },
                );
            }
            Value::Object(merged)
        }
        (_, overlay) => overlay.clone(),
    }
}

pub fn compact_map(source: &Map<String, Value>) -> Map<String, Value> {
    source
        .iter()
        .filter(|(_, value)| !value.is_null())
        .map(|(key, value)| (key.clone(), value.clone()))
        .collect()
}

pub fn keys(source: &Map<String, Value>) -> Vec<String> {
    source.keys().cloned().collect()
}

pub fn values(source: &Map<String, Value>) -> Vec<Value> {
    source.values().cloned().collect()
}

#[cfg(test)]
mod tests {
    use super::*;
    use serde_json::json;

    #[test]
    fn object_helpers() {
        let source = json!({"user": {"name": "Ada"}, "role": "admin", "team": "core"});
        let picked = pick(source.as_object().unwrap(), &["user", "role"]);
        assert_eq!(picked.get("team"), None);
        assert_eq!(get_path(&source, "user.name"), Some(json!("Ada")));

        let mut target = json!({});
        set_path(&mut target, "user.city", json!("Paris"));
        assert_eq!(get_path(&target, "user.city"), Some(json!("Paris")));

        let merged = deep_merge(
            &json!({"a": 1, "nested": {"x": 1}}),
            &json!({"b": 2, "nested": {"y": 2}}),
        );
        assert_eq!(merged, json!({"a": 1, "b": 2, "nested": {"x": 1, "y": 2}}));
    }
}
