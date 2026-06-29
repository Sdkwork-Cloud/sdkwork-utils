//! Platform detection and normalization utilities.
//!
//! Provides cross-platform functions for detecting the current platform family
//! and normalizing CPU architecture strings to canonical forms.

/// Detects the current platform family based on compile-time cfg flags.
///
/// Returns:
/// - `"windows"` on Windows
/// - `"macos"` on macOS
/// - `"linux"` on Linux
/// - `"unknown"` on other platforms
///
/// # Example
/// ```
/// use sdkwork_utils_rust::platform::detect_platform_family;
/// let platform = detect_platform_family();
/// assert!(matches!(platform.as_str(), "windows" | "macos" | "linux" | "unknown"));
/// ```
pub fn detect_platform_family() -> String {
    if cfg!(windows) {
        "windows".into()
    } else if cfg!(target_os = "macos") {
        "macos".into()
    } else if cfg!(target_os = "linux") {
        "linux".into()
    } else {
        "unknown".into()
    }
}

/// Normalizes CPU architecture strings to canonical forms.
///
/// Canonical forms:
/// - `"x64"` for x86_64/amd64 architectures
/// - `"arm64"` for aarch64/arm64 architectures
/// - Other values are returned lowercase and trimmed
///
/// # Example
/// ```
/// use sdkwork_utils_rust::platform::normalize_cpu_arch;
/// assert_eq!(normalize_cpu_arch("x86_64"), "x64");
/// assert_eq!(normalize_cpu_arch("AMD64"), "x64");
/// assert_eq!(normalize_cpu_arch("aarch64"), "arm64");
/// assert_eq!(normalize_cpu_arch("ARM64"), "arm64");
/// ```
pub fn normalize_cpu_arch(value: &str) -> String {
    match value.trim().to_ascii_lowercase().as_str() {
        "x86_64" | "amd64" | "x64" => "x64".into(),
        "aarch64" | "arm64" => "arm64".into(),
        other => other.into(),
    }
}

/// Detects the default platform family for runtime-node deployments.
///
/// Unlike `detect_platform_family`, this returns:
/// - `"windows"` on Windows
/// - `"macos"` on macOS
/// - `"ubuntu-server"` on Linux (default for server deployments)
///
/// This matches the default behavior expected by `RuntimeNodeBootstrapConfig`.
pub fn detect_runtime_node_platform_family() -> String {
    if cfg!(windows) {
        "windows".into()
    } else if cfg!(target_os = "macos") {
        "macos".into()
    } else {
        "ubuntu-server".into()
    }
}

/// Detects the default platform family for AI CLI host deployments.
///
/// Returns:
/// - `"windows"` on Windows
/// - `"macos"` on macOS
/// - `"ubuntu-desktop"` on Linux (default for desktop deployments)
///
/// This matches the default behavior expected by AI CLI host.
pub fn detect_desktop_platform_family() -> String {
    if cfg!(windows) {
        "windows".into()
    } else if cfg!(target_os = "macos") {
        "macos".into()
    } else {
        "ubuntu-desktop".into()
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn detect_platform_family_returns_valid_value() {
        let platform = detect_platform_family();
        assert!(matches!(
            platform.as_str(),
            "windows" | "macos" | "linux" | "unknown"
        ));
    }

    #[test]
    fn normalize_cpu_arch_handles_common_aliases() {
        assert_eq!(normalize_cpu_arch("x86_64"), "x64");
        assert_eq!(normalize_cpu_arch("amd64"), "x64");
        assert_eq!(normalize_cpu_arch("x64"), "x64");
        assert_eq!(normalize_cpu_arch("X86_64"), "x64");
        assert_eq!(normalize_cpu_arch("AMD64"), "x64");
        assert_eq!(normalize_cpu_arch("X64"), "x64");

        assert_eq!(normalize_cpu_arch("aarch64"), "arm64");
        assert_eq!(normalize_cpu_arch("arm64"), "arm64");
        assert_eq!(normalize_cpu_arch("AARCH64"), "arm64");
        assert_eq!(normalize_cpu_arch("ARM64"), "arm64");
    }

    #[test]
    fn normalize_cpu_arch_preserves_unknown() {
        assert_eq!(normalize_cpu_arch("riscv64"), "riscv64");
        assert_eq!(normalize_cpu_arch("  wasm32  "), "wasm32");
    }

    #[test]
    fn runtime_node_platform_family_returns_valid_value() {
        let platform = detect_runtime_node_platform_family();
        assert!(matches!(
            platform.as_str(),
            "windows" | "macos" | "ubuntu-server"
        ));
    }

    #[test]
    fn desktop_platform_family_returns_valid_value() {
        let platform = detect_desktop_platform_family();
        assert!(matches!(
            platform.as_str(),
            "windows" | "macos" | "ubuntu-desktop"
        ));
    }
}
