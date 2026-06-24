package utils

import "regexp"
import "strings"

var (
	emailRe = regexp.MustCompile(`^[A-Za-z0-9._%+\-]+@[A-Za-z0-9.\-]+\.[A-Za-z]{2,}$`)
	uuidRe  = regexp.MustCompile(`^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$`)
	urlRe   = regexp.MustCompile(`^https?://[^\s/$.?#].[^\s]*$`)
	ipv4Re  = regexp.MustCompile(`^(25[0-5]|2[0-4]\d|1?\d?\d)(\.(25[0-5]|2[0-4]\d|1?\d?\d)){3}$`)
	e164Re  = regexp.MustCompile(`^\+[1-9]\d{1,14}$`)
)

func IsEmail(value string) bool {
	return emailRe.MatchString(Trim(value))
}

func IsUUID(value string) bool {
	return uuidRe.MatchString(Trim(value))
}

func IsURL(value string) bool {
	return urlRe.MatchString(Trim(value))
}

func IsNumeric(value string) bool {
	_, ok := ParseNumber(value)
	return ok
}

func IsIPv4(value string) bool {
	return ipv4Re.MatchString(Trim(value))
}

func isIPv6Shape(value string) bool {
	if value == "" {
		return false
	}
	for _, ch := range value {
		if (ch < '0' || ch > '9') && (ch < 'a' || ch > 'f') && (ch < 'A' || ch > 'F') && ch != ':' {
			return false
		}
	}
	if strings.Count(value, "::") > 1 {
		return false
	}
	isValidPart := func(part string) bool {
		return len(part) > 0 && len(part) <= 4
	}
	if strings.Contains(value, "::") {
		parts := strings.SplitN(value, "::", 2)
		leftParts := strings.FieldsFunc(parts[0], func(r rune) bool { return r == ':' })
		rightParts := strings.FieldsFunc(parts[1], func(r rune) bool { return r == ':' })
		for _, part := range append(leftParts, rightParts...) {
			if part != "" && !isValidPart(part) {
				return false
			}
		}
		leftCount := 0
		for _, part := range leftParts {
			if part != "" {
				leftCount++
			}
		}
		rightCount := 0
		for _, part := range rightParts {
			if part != "" {
				rightCount++
			}
		}
		return leftCount+rightCount < 8
	}
	segments := strings.Split(value, ":")
	if len(segments) != 8 {
		return false
	}
	for _, part := range segments {
		if !isValidPart(part) {
			return false
		}
	}
	return true
}

func IsIPv6(value string) bool {
	return isIPv6Shape(Trim(value))
}

func IsPhoneE164(value string) bool {
	return e164Re.MatchString(Trim(value))
}
