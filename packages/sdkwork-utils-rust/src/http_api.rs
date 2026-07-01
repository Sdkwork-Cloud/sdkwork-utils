//! SDKWork HTTP API wire contracts (`API_SPEC.md` §14–§16).

use serde::{Deserialize, Serialize};

/// Response header echoing `SdkWorkApiResponse.traceId` / `ProblemDetail.traceId`.
pub const SDKWORK_TRACE_ID_HEADER: &str = "X-SdkWork-Trace-Id";

/// Canonical success result code for HTTP 2xx JSON bodies.
pub const SDKWORK_SUCCESS_CODE: i32 = 0;

/// Platform result codes (`API_SPEC.md` §15.3).
#[derive(Clone, Copy, Debug, Eq, PartialEq)]
#[repr(i32)]
pub enum SdkWorkResultCode {
    Ok = 0,
    ValidationError = 40001,
    MalformedRequest = 40002,
    InvalidParameter = 40003,
    MissingRequiredField = 40004,
    AuthenticationRequired = 40101,
    TokenExpired = 40102,
    InvalidToken = 40103,
    SessionRevoked = 40104,
    PermissionRequired = 40301,
    InsufficientScope = 40302,
    TenantAccessDenied = 40303,
    OrganizationAccessDenied = 40304,
    NotFound = 40401,
    MethodNotAllowed = 40501,
    RequestTimeout = 40801,
    Conflict = 40901,
    Gone = 41001,
    PreconditionFailed = 41201,
    PayloadTooLarge = 41301,
    UnsupportedMediaType = 41501,
    UnprocessableEntity = 42201,
    Locked = 42301,
    PreconditionRequired = 42801,
    RateLimitExceeded = 42901,
    InternalError = 50001,
    BadGateway = 50201,
    ServiceUnavailable = 50301,
    GatewayTimeout = 50401,
}

impl SdkWorkResultCode {
    pub const fn as_i32(self) -> i32 {
        self as i32
    }

    pub const fn symbol(self) -> &'static str {
        match self {
            Self::Ok => "OK",
            Self::ValidationError => "VALIDATION_ERROR",
            Self::MalformedRequest => "MALFORMED_REQUEST",
            Self::InvalidParameter => "INVALID_PARAMETER",
            Self::MissingRequiredField => "MISSING_REQUIRED_FIELD",
            Self::AuthenticationRequired => "AUTHENTICATION_REQUIRED",
            Self::TokenExpired => "TOKEN_EXPIRED",
            Self::InvalidToken => "INVALID_TOKEN",
            Self::SessionRevoked => "SESSION_REVOKED",
            Self::PermissionRequired => "PERMISSION_REQUIRED",
            Self::InsufficientScope => "INSUFFICIENT_SCOPE",
            Self::TenantAccessDenied => "TENANT_ACCESS_DENIED",
            Self::OrganizationAccessDenied => "ORGANIZATION_ACCESS_DENIED",
            Self::NotFound => "NOT_FOUND",
            Self::MethodNotAllowed => "METHOD_NOT_ALLOWED",
            Self::RequestTimeout => "REQUEST_TIMEOUT",
            Self::Conflict => "CONFLICT",
            Self::Gone => "GONE",
            Self::PreconditionFailed => "PRECONDITION_FAILED",
            Self::PayloadTooLarge => "PAYLOAD_TOO_LARGE",
            Self::UnsupportedMediaType => "UNSUPPORTED_MEDIA_TYPE",
            Self::UnprocessableEntity => "UNPROCESSABLE_ENTITY",
            Self::Locked => "LOCKED",
            Self::PreconditionRequired => "PRECONDITION_REQUIRED",
            Self::RateLimitExceeded => "RATE_LIMIT_EXCEEDED",
            Self::InternalError => "INTERNAL_ERROR",
            Self::BadGateway => "BAD_GATEWAY",
            Self::ServiceUnavailable => "SERVICE_UNAVAILABLE",
            Self::GatewayTimeout => "GATEWAY_TIMEOUT",
        }
    }

    pub const fn http_status_code(self) -> u16 {
        match self {
            Self::Ok => 200,
            Self::ValidationError
            | Self::MalformedRequest
            | Self::InvalidParameter
            | Self::MissingRequiredField => 400,
            Self::AuthenticationRequired
            | Self::TokenExpired
            | Self::InvalidToken
            | Self::SessionRevoked => 401,
            Self::PermissionRequired
            | Self::InsufficientScope
            | Self::TenantAccessDenied
            | Self::OrganizationAccessDenied => 403,
            Self::NotFound => 404,
            Self::MethodNotAllowed => 405,
            Self::RequestTimeout => 408,
            Self::Conflict => 409,
            Self::Gone => 410,
            Self::PreconditionFailed => 412,
            Self::PayloadTooLarge => 413,
            Self::UnsupportedMediaType => 415,
            Self::UnprocessableEntity => 422,
            Self::Locked => 423,
            Self::PreconditionRequired => 428,
            Self::RateLimitExceeded => 429,
            Self::InternalError => 500,
            Self::BadGateway => 502,
            Self::ServiceUnavailable => 503,
            Self::GatewayTimeout => 504,
        }
    }

    pub const fn title(self) -> &'static str {
        match self {
            Self::Ok => "OK",
            Self::ValidationError => "Validation failed",
            Self::MalformedRequest => "Malformed request",
            Self::InvalidParameter => "Invalid parameter",
            Self::MissingRequiredField => "Missing required field",
            Self::AuthenticationRequired => "Authentication required",
            Self::TokenExpired => "Token expired",
            Self::InvalidToken => "Invalid token",
            Self::SessionRevoked => "Session revoked",
            Self::PermissionRequired => "Permission required",
            Self::InsufficientScope => "Insufficient scope",
            Self::TenantAccessDenied => "Tenant access denied",
            Self::OrganizationAccessDenied => "Organization access denied",
            Self::NotFound => "Not found",
            Self::MethodNotAllowed => "Method not allowed",
            Self::RequestTimeout => "Request timeout",
            Self::Conflict => "Conflict",
            Self::Gone => "Gone",
            Self::PreconditionFailed => "Precondition failed",
            Self::PayloadTooLarge => "Payload too large",
            Self::UnsupportedMediaType => "Unsupported media type",
            Self::UnprocessableEntity => "Unprocessable entity",
            Self::Locked => "Locked",
            Self::PreconditionRequired => "Precondition required",
            Self::RateLimitExceeded => "Rate limit exceeded",
            Self::InternalError => "Internal server error",
            Self::BadGateway => "Bad gateway",
            Self::ServiceUnavailable => "Service unavailable",
            Self::GatewayTimeout => "Gateway timeout",
        }
    }
}

/// Canonical HTTP success envelope (`API_SPEC.md` §15.1.1).
#[derive(Clone, Debug, Eq, PartialEq, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct SdkWorkApiResponse<T> {
    pub code: i32,
    pub data: T,
    pub trace_id: String,
}

impl<T> SdkWorkApiResponse<T> {
    pub fn success(data: T, trace_id: impl Into<String>) -> Self {
        Self {
            code: SDKWORK_SUCCESS_CODE,
            data,
            trace_id: trace_id.into(),
        }
    }
}

/// Pagination mode (`API_SPEC.md` §16).
#[derive(Clone, Copy, Debug, Eq, PartialEq, Serialize, Deserialize)]
#[serde(rename_all = "lowercase")]
pub enum PageMode {
    Offset,
    Cursor,
}

/// Standard list pagination metadata (`API_SPEC.md` §16).
#[derive(Clone, Debug, Eq, PartialEq, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct PageInfo {
    pub mode: PageMode,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub page: Option<i32>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub page_size: Option<i32>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub total_items: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub total_pages: Option<i32>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub next_cursor: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub has_more: Option<bool>,
}

/// Standard list payload inside `SdkWorkApiResponse.data`.
#[derive(Clone, Debug, Eq, PartialEq, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct SdkWorkPageData<T> {
    pub items: Vec<T>,
    pub page_info: PageInfo,
}

/// Standard single-resource payload inside `SdkWorkApiResponse.data`.
#[derive(Clone, Debug, Eq, PartialEq, Serialize, Deserialize)]
pub struct SdkWorkResourceData<T> {
    pub item: T,
}

/// Standard command payload inside `SdkWorkApiResponse.data`.
#[derive(Clone, Debug, Eq, PartialEq, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct SdkWorkCommandData {
    pub accepted: bool,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub resource_id: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub status: Option<String>,
}

impl SdkWorkCommandData {
    pub fn accepted() -> Self {
        Self {
            accepted: true,
            resource_id: None,
            status: None,
        }
    }
}

/// Request routing context attached to `ProblemDetail` (`API_SPEC.md` §15.2).
#[derive(Clone, Debug, Default, Eq, PartialEq)]
pub struct SdkWorkProblemRouting {
    pub method: Option<String>,
    pub route_template: Option<String>,
    pub fallback_path: Option<String>,
    pub operation_id: Option<String>,
}

impl SdkWorkProblemRouting {
    pub fn from_parts(
        method: Option<&str>,
        route_template: Option<&str>,
        fallback_path: Option<&str>,
        operation_id: Option<&str>,
    ) -> Self {
        Self {
            method: non_empty_text(method),
            route_template: non_empty_text(route_template),
            fallback_path: non_empty_text(fallback_path),
            operation_id: non_empty_text(operation_id),
        }
    }

    /// RFC 9457 `instance`: `{METHOD} {routeTemplate}` with safe fallback redaction.
    pub fn instance(&self) -> Option<String> {
        let route = self
            .route_template
            .as_deref()
            .or(self.fallback_path.as_deref())?;
        let route = if self.route_template.is_some() {
            route.to_owned()
        } else {
            redact_http_path_segments(route)
        };
        let method = self
            .method
            .as_deref()
            .unwrap_or("GET")
            .trim()
            .to_ascii_uppercase();
        Some(format!("{method} {route}"))
    }
}

/// Redact numeric and uuid-like HTTP path segments for Problem `instance` values.
pub fn redact_http_path_segments(path: &str) -> String {
    path.split('/')
        .map(|segment| {
            if segment.is_empty() {
                return String::new();
            }
            if segment.chars().all(|ch| ch.is_ascii_digit())
                || segment.len() >= 32
                    && segment
                        .chars()
                        .all(|ch| ch.is_ascii_hexdigit() || ch == '-')
            {
                "{id}".to_owned()
            } else {
                segment.to_owned()
            }
        })
        .collect::<Vec<_>>()
        .join("/")
}

fn non_empty_text(value: Option<&str>) -> Option<String> {
    value
        .map(str::trim)
        .filter(|value| !value.is_empty())
        .map(str::to_owned)
}

/// RFC 9457 `application/problem+json` body (`API_SPEC.md` §15.2).
#[derive(Clone, Debug, Eq, PartialEq, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct SdkWorkProblemDetail {
    #[serde(rename = "type")]
    pub problem_type: String,
    pub title: String,
    pub status: u16,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub detail: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub instance: Option<String>,
    pub code: i32,
    pub trace_id: String,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub operation_id: Option<String>,
}

impl SdkWorkProblemDetail {
    pub fn platform(
        result_code: SdkWorkResultCode,
        detail: impl Into<String>,
        trace_id: impl Into<String>,
    ) -> Self {
        Self::platform_body(result_code, detail, trace_id)
    }

    pub fn platform_enriched(
        result_code: SdkWorkResultCode,
        detail: impl Into<String>,
        trace_id: impl Into<String>,
        routing: SdkWorkProblemRouting,
    ) -> Self {
        Self::platform_body(result_code, detail, trace_id).with_routing(routing)
    }

    pub fn with_routing(mut self, routing: SdkWorkProblemRouting) -> Self {
        self.instance = routing.instance();
        self.operation_id = routing.operation_id;
        self
    }

    /// Client-safe Problem `detail` — internal failures must not leak implementation details.
    pub fn client_safe_detail(result_code: SdkWorkResultCode, detail: &str) -> String {
        match result_code {
            SdkWorkResultCode::InternalError => "An internal error occurred".to_owned(),
            SdkWorkResultCode::ServiceUnavailable => {
                "A required dependency is temporarily unavailable".to_owned()
            }
            _ if detail.trim().is_empty() => result_code.title().to_owned(),
            _ => detail.to_owned(),
        }
    }

    fn platform_body(
        result_code: SdkWorkResultCode,
        detail: impl Into<String>,
        trace_id: impl Into<String>,
    ) -> Self {
        let detail_text = Self::client_safe_detail(result_code, &detail.into());
        Self {
            problem_type: format!("https://docs.sdkwork.com/problems/{}", result_code.as_i32()),
            title: result_code.title().to_string(),
            status: result_code.http_status_code(),
            detail: if detail_text.is_empty() {
                None
            } else {
                Some(detail_text)
            },
            instance: None,
            code: result_code.as_i32(),
            trace_id: trace_id.into(),
            operation_id: None,
        }
    }
}

/// Maps legacy Claw Router string wire codes and symbolic aliases to platform codes.
pub fn legacy_wire_result_code(wire_code: &str) -> SdkWorkResultCode {
    match wire_code.trim() {
        "2000" => SdkWorkResultCode::Ok,
        "4001" => SdkWorkResultCode::ValidationError,
        "4004" => SdkWorkResultCode::NotFound,
        "4010" => SdkWorkResultCode::AuthenticationRequired,
        "4040" | "not_found" => SdkWorkResultCode::NotFound,
        "4090" | "conflict" => SdkWorkResultCode::Conflict,
        "4220" => SdkWorkResultCode::UnprocessableEntity,
        "5000" | "5001" | "4000" => SdkWorkResultCode::InternalError,
        "5030" => SdkWorkResultCode::ServiceUnavailable,
        "invalid_input" | "validation_error" => SdkWorkResultCode::ValidationError,
        "forbidden" => SdkWorkResultCode::PermissionRequired,
        "rate_limited" => SdkWorkResultCode::RateLimitExceeded,
        "provider_error" => SdkWorkResultCode::BadGateway,
        _ => SdkWorkResultCode::InternalError,
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn success_envelope_uses_zero_code() {
        let body = SdkWorkApiResponse::success(SdkWorkResourceData { item: 42 }, "trace-1");
        assert_eq!(0, body.code);
        assert_eq!("trace-1", body.trace_id);
    }

    #[test]
    fn platform_codes_match_spec_ranges() {
        assert_eq!(40001, SdkWorkResultCode::ValidationError.as_i32());
        assert_eq!(40101, SdkWorkResultCode::AuthenticationRequired.as_i32());
        assert_eq!(50001, SdkWorkResultCode::InternalError.as_i32());
    }

    #[test]
    fn legacy_claw_router_codes_map_to_platform_codes() {
        assert_eq!(40401, legacy_wire_result_code("4004").as_i32());
        assert_eq!(40101, legacy_wire_result_code("4010").as_i32());
        assert_eq!(50301, legacy_wire_result_code("5030").as_i32());
    }

    #[test]
    fn problem_detail_uses_numeric_code_and_trace_id() {
        let problem = SdkWorkProblemDetail::platform(
            SdkWorkResultCode::NotFound,
            "Workspace not found",
            "trace-404",
        );
        let json = serde_json::to_value(problem).expect("serialize problem");
        assert_eq!(json["code"], 40401);
        assert_eq!(json["status"], 404);
        assert_eq!(json["traceId"], "trace-404");
        assert_eq!(json["detail"], "Workspace not found");
    }

    #[test]
    fn problem_detail_enriched_with_instance_and_operation_id() {
        let routing = SdkWorkProblemRouting::from_parts(
            Some("get"),
            Some("/app/v3/api/wallet/transactions"),
            None,
            Some("wallet.transactions.list"),
        );
        let problem = SdkWorkProblemDetail::platform_enriched(
            SdkWorkResultCode::InternalError,
            "sql leak",
            "trace-500",
            routing,
        );
        let json = serde_json::to_value(problem).expect("serialize problem");
        assert_eq!(json["instance"], "GET /app/v3/api/wallet/transactions");
        assert_eq!(json["operationId"], "wallet.transactions.list");
        assert_eq!(json["detail"], "An internal error occurred");
    }

    #[test]
    fn redact_http_path_segments_masks_ids() {
        assert_eq!(
            "/app/v3/api/users/{id}/orders/{id}",
            redact_http_path_segments("/app/v3/api/users/42/orders/99")
        );
    }
}
