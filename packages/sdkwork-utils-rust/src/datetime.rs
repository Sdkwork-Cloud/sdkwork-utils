use chrono::{DateTime, Duration, NaiveTime, TimeZone, Utc};

pub const DEFAULT_PATTERN: &str = "%Y-%m-%dT%H:%M:%S%.3fZ";

pub fn now() -> DateTime<Utc> {
    Utc::now()
}

pub fn format_datetime(value: DateTime<Utc>, pattern: Option<&str>) -> String {
    value.format(pattern.unwrap_or(DEFAULT_PATTERN)).to_string()
}

pub fn parse_datetime(value: &str, pattern: Option<&str>) -> Option<DateTime<Utc>> {
    let pattern = pattern.unwrap_or(DEFAULT_PATTERN);
    DateTime::parse_from_str(value, pattern)
        .ok()
        .map(|dt| dt.with_timezone(&Utc))
        .or_else(|| {
            chrono::DateTime::parse_from_rfc3339(value)
                .ok()
                .map(|dt| dt.with_timezone(&Utc))
        })
}

pub fn add_days(value: DateTime<Utc>, days: i64) -> DateTime<Utc> {
    value + Duration::days(days)
}

pub fn add_hours(value: DateTime<Utc>, hours: i64) -> DateTime<Utc> {
    value + Duration::hours(hours)
}

pub fn add_minutes(value: DateTime<Utc>, minutes: i64) -> DateTime<Utc> {
    value + Duration::minutes(minutes)
}

pub fn diff_millis(earlier: DateTime<Utc>, later: DateTime<Utc>) -> i64 {
    (later - earlier).num_milliseconds()
}

pub fn is_before(first: DateTime<Utc>, second: DateTime<Utc>) -> bool {
    first < second
}

pub fn is_after(first: DateTime<Utc>, second: DateTime<Utc>) -> bool {
    first > second
}

pub fn start_of_day_utc(value: DateTime<Utc>) -> DateTime<Utc> {
    let date = value.date_naive();
    Utc.from_utc_datetime(&date.and_time(NaiveTime::from_hms_opt(0, 0, 0).expect("midnight")))
}

pub fn end_of_day_utc(value: DateTime<Utc>) -> DateTime<Utc> {
    let date = value.date_naive();
    Utc.from_utc_datetime(
        &date.and_time(NaiveTime::from_hms_milli_opt(23, 59, 59, 999).expect("end of day")),
    )
}

pub fn to_unix_millis(value: DateTime<Utc>) -> i64 {
    value.timestamp_millis()
}

pub fn from_unix_millis(value: i64) -> Option<DateTime<Utc>> {
    DateTime::from_timestamp_millis(value)
}

pub fn is_same_instant(first: DateTime<Utc>, second: DateTime<Utc>) -> bool {
    first.timestamp_millis() == second.timestamp_millis()
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn parse_and_diff() {
        let first = parse_datetime("2024-01-01T00:00:00.000Z", None).unwrap();
        let second = add_hours(first, 2);
        assert_eq!(diff_millis(first, second), 7_200_000);
        assert!(is_before(first, second));
    }
}
