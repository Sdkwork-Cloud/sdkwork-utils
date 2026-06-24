package utils

import "time"

const DefaultPattern = "iso8601"

const iso8601MillisLayout = "2006-01-02T15:04:05.000Z"

func Now() time.Time {
	return time.Now().UTC()
}

func FormatDatetime(value time.Time, pattern string) string {
	if pattern == "" {
		pattern = DefaultPattern
	}
	if pattern != DefaultPattern {
		return ""
	}
	return value.UTC().Format(iso8601MillisLayout)
}

func ParseDatetime(value string, pattern string) (time.Time, bool) {
	if pattern == "" {
		pattern = DefaultPattern
	}
	if pattern != DefaultPattern {
		return time.Time{}, false
	}
	parsed, err := time.Parse(time.RFC3339Nano, value)
	if err != nil {
		parsed, err = time.Parse(time.RFC3339, value)
	}
	if err != nil {
		return time.Time{}, false
	}
	return parsed.UTC(), true
}

func AddDays(value time.Time, days int) time.Time {
	return value.Add(time.Duration(days) * 24 * time.Hour)
}

func AddHours(value time.Time, hours int) time.Time {
	return value.Add(time.Duration(hours) * time.Hour)
}

func AddMinutes(value time.Time, minutes int) time.Time {
	return value.Add(time.Duration(minutes) * time.Minute)
}

func DiffMillis(earlier, later time.Time) int64 {
	return later.Sub(earlier).Milliseconds()
}

func IsBefore(first, second time.Time) bool {
	return first.Before(second)
}

func IsAfter(first, second time.Time) bool {
	return first.After(second)
}

func StartOfDayUTC(value time.Time) time.Time {
	utc := value.UTC()
	return time.Date(utc.Year(), utc.Month(), utc.Day(), 0, 0, 0, 0, time.UTC)
}

func EndOfDayUTC(value time.Time) time.Time {
	start := StartOfDayUTC(value)
	return start.Add(24*time.Hour - time.Millisecond)
}

func ToUnixMillis(value time.Time) int64 {
	return value.UTC().UnixMilli()
}

func FromUnixMillis(value int64) (time.Time, bool) {
	return time.UnixMilli(value).UTC(), true
}

func IsSameInstant(first, second time.Time) bool {
	return first.UTC().UnixMilli() == second.UTC().UnixMilli()
}
