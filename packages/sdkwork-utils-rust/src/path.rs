pub fn join_path(segments: &[&str]) -> String {
    segments
        .iter()
        .map(|segment| segment.trim_matches('/'))
        .filter(|segment| !segment.is_empty())
        .collect::<Vec<_>>()
        .join("/")
}

pub fn normalize_path(value: &str) -> String {
    let joined = value
        .split('/')
        .filter(|part| !part.is_empty())
        .collect::<Vec<_>>()
        .join("/");
    if value.starts_with('/') {
        format!("/{joined}")
    } else {
        joined
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn path_helpers() {
        assert_eq!(join_path(&["a", "", "/b/", "c"]), "a/b/c");
        assert_eq!(normalize_path("//a//b//"), "/a/b");
    }
}
