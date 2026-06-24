const UNITS: [&str; 6] = ["B", "KB", "MB", "GB", "TB", "PB"];

pub fn format_bytes(bytes: i64, decimals: u32) -> String {
    let bytes = bytes.max(0) as u64;
    if bytes < 1024 {
        return format!("{bytes} B");
    }

    let mut size = bytes as f64;
    let mut unit_index = 0usize;
    while size >= 1024.0 && unit_index < UNITS.len() - 1 {
        size /= 1024.0;
        unit_index += 1;
    }

    format!(
        "{size:.decimals$} {}",
        UNITS[unit_index],
        decimals = decimals as usize
    )
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn format_bytes_helpers() {
        assert_eq!(format_bytes(0, 1), "0 B");
        assert_eq!(format_bytes(512, 1), "512 B");
        assert_eq!(format_bytes(1536, 1), "1.5 KB");
        assert_eq!(format_bytes(1_048_576, 2), "1.00 MB");
    }
}
