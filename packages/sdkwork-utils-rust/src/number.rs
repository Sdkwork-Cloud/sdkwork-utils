pub fn clamp<T: PartialOrd>(value: T, min: T, max: T) -> T {
    if value < min {
        min
    } else if value > max {
        max
    } else {
        value
    }
}

pub fn round(value: f64, decimals: Option<u32>) -> f64 {
    let decimals = decimals.unwrap_or(0);
    let factor = 10_f64.powi(decimals as i32);
    (value * factor).round() / factor
}

pub fn format_number(value: f64, decimals: Option<u32>) -> String {
    format!("{:.1$}", value, decimals.unwrap_or(0) as usize)
}

pub fn parse_number(value: &str) -> Option<f64> {
    value.trim().parse::<f64>().ok()
}

pub fn is_integer(value: f64) -> bool {
    value.is_finite() && value.fract() == 0.0
}

pub fn parse_int(value: &str) -> Option<i64> {
    value.trim().parse::<i64>().ok()
}

pub fn percent_format(value: f64, decimals: Option<u32>) -> String {
    format!("{}%", format_number(value * 100.0, decimals))
}

pub fn in_range<T: PartialOrd>(value: T, min: T, max: T) -> bool {
    value >= min && value <= max
}

pub fn abs(value: f64) -> f64 {
    value.abs()
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn number_helpers() {
        assert_eq!(clamp(5.0, 0.0, 3.0), 3.0);
        assert_eq!(round(1.235, Some(2)), 1.24);
        assert_eq!(parse_number("42"), Some(42.0));
        assert!(is_integer(42.0));
    }
}
