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

const SDKWORK_SURFACE_PREFIXES: &[&str] = &["/app/v3/api", "/backend/v3/api", "/gateway/v3/api"];

/// Collapses accidental duplicate SDKWork API surface prefixes in inbound paths.
///
/// Clients that configure `baseUrl` as `https://host/app/v3/api` while operation
/// paths also start with `/app/v3/api/...` produce `/app/v3/api/app/v3/api/...`.
/// The web framework route manifest only registers the canonical single-prefix
/// shape, so the doubled path must be normalized before auth classification.
pub fn collapse_duplicate_surface_prefix(value: &str) -> String {
    let (path, query) = split_path_and_query(value);
    let mut collapsed = normalize_path(path);
    for prefix in SDKWORK_SURFACE_PREFIXES {
        let doubled = format!("{prefix}{prefix}");
        while collapsed.starts_with(&doubled) {
            collapsed = format!("{prefix}{}", &collapsed[doubled.len()..]);
        }
    }
    match query {
        Some(query) if !query.is_empty() => format!("{collapsed}?{query}"),
        _ => collapsed,
    }
}

fn split_path_and_query(value: &str) -> (&str, Option<&str>) {
    match value.split_once('?') {
        Some((path, query)) => (path, Some(query)),
        None => (value, None),
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

    #[test]
    fn collapse_duplicate_surface_prefix_rewrites_doubled_app_api_paths() {
        assert_eq!(
            collapse_duplicate_surface_prefix("/app/v3/api/app/v3/api/assets"),
            "/app/v3/api/assets"
        );
        assert_eq!(
            collapse_duplicate_surface_prefix("/app/v3/api/app/v3/api/assets?page=1"),
            "/app/v3/api/assets?page=1"
        );
        assert_eq!(
            collapse_duplicate_surface_prefix("/backend/v3/api/backend/v3/api/users"),
            "/backend/v3/api/users"
        );
        assert_eq!(
            collapse_duplicate_surface_prefix("/app/v3/api/assets"),
            "/app/v3/api/assets"
        );
    }
}
