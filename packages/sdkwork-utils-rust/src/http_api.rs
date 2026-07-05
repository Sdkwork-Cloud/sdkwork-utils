//! SDKWork HTTP API wire contracts (`API_SPEC.md` §14–§16).

use std::fmt;

use serde::de::{self, Unexpected, Visitor};
use serde::{Deserialize, Deserializer, Serialize};

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
    QuotaExceeded = 60002,
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
            Self::QuotaExceeded => "QUOTA_EXCEEDED",
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
            Self::RateLimitExceeded | Self::QuotaExceeded => 429,
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
            Self::QuotaExceeded => "Quota exceeded",
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

/// SQL window column alias for `COUNT(*) OVER()` total row counts in list queries.
pub const LIST_TOTAL_SQL_COLUMN: &str = "__list_total";

/// Default page size for offset list queries (`SdkWorkListQuery.pageSize`).
pub const DEFAULT_LIST_PAGE_SIZE: i32 = 20;

/// Maximum allowed page size for offset list queries (`SdkWorkListQuery.pageSize`).
pub const MAX_LIST_PAGE_SIZE: i32 = 200;

/// Parsed offset pagination parameters for database-backed list handlers.
#[derive(Clone, Copy, Debug, Eq, PartialEq)]
pub struct OffsetListPageParams {
    pub page: i64,
    pub page_size: i64,
    pub offset: i64,
}

impl OffsetListPageParams {
    pub fn parse(page: Option<i64>, page_size: Option<i64>) -> Self {
        let page_size = page_size
            .unwrap_or(i64::from(DEFAULT_LIST_PAGE_SIZE))
            .clamp(1, i64::from(MAX_LIST_PAGE_SIZE));
        let page = page.unwrap_or(1).max(1);
        let offset = (page - 1) * page_size;
        Self {
            page,
            page_size,
            offset,
        }
    }
}

/// Validates standard offset list params per PAGINATION_SPEC; rejects out-of-range values instead of clamping.
pub fn validated_offset_list_params(
    page: Option<i64>,
    page_size: Option<i64>,
) -> Result<OffsetListPageParams, SdkWorkResultCode> {
    let page_size = page_size.unwrap_or(i64::from(DEFAULT_LIST_PAGE_SIZE));
    if page_size < 1 || page_size > i64::from(MAX_LIST_PAGE_SIZE) {
        return Err(SdkWorkResultCode::InvalidParameter);
    }
    let page = page.unwrap_or(1);
    if page < 1 {
        return Err(SdkWorkResultCode::InvalidParameter);
    }
    Ok(OffsetListPageParams {
        page,
        page_size,
        offset: (page - 1) * page_size,
    })
}

/// Build offset pagination metadata from already-validated `page` and `page_size` values.
pub fn offset_list_page_params_from_values(page: i64, page_size: i64) -> OffsetListPageParams {
    OffsetListPageParams {
        page,
        page_size,
        offset: (page - 1) * page_size,
    }
}

/// Parse standard list query keys: `page` / `pageNo` / `page_no` and `pageSize` / `page_size`.
pub fn offset_list_page_params_from_map(
    query: &std::collections::HashMap<String, String>,
) -> OffsetListPageParams {
    let page = query
        .get("page")
        .or_else(|| query.get("pageNo"))
        .or_else(|| query.get("page_no"))
        .and_then(|value| value.parse::<i64>().ok());
    let page_size = query
        .get("page_size")
        .or_else(|| query.get("pageSize"))
        .and_then(|value| value.parse::<i64>().ok());
    OffsetListPageParams::parse(page, page_size)
}

/// Build offset-mode `PageInfo` with total counts for SQL-backed list responses.
pub fn offset_list_page_info(total_items: i64, params: OffsetListPageParams) -> PageInfo {
    let total_pages = if total_items == 0 {
        0
    } else {
        ((total_items + params.page_size - 1) / params.page_size) as i32
    };
    let has_more = params.page * params.page_size < total_items;
    PageInfo {
        mode: PageMode::Offset,
        page: Some(params.page as i32),
        page_size: Some(params.page_size as i32),
        total_items: Some(total_items.to_string()),
        total_pages: Some(total_pages),
        next_cursor: None,
        has_more: Some(has_more),
    }
}

/// Build standard `SdkWorkPageData` for typed list handlers.
pub fn offset_list_page_data<T>(
    items: Vec<T>,
    total_items: i64,
    params: OffsetListPageParams,
) -> SdkWorkPageData<T> {
    SdkWorkPageData {
        items,
        page_info: offset_list_page_info(total_items, params),
    }
}

#[derive(Clone, Debug, Eq, PartialEq)]
pub struct OffsetLimitPage<T> {
    pub items: Vec<T>,
    pub next_cursor: Option<String>,
    pub has_more: bool,
}

/// Parse an offset list cursor token. Missing or blank cursor resolves to `0`.
pub fn parse_offset_list_cursor(cursor: Option<&str>) -> Result<usize, SdkWorkResultCode> {
    let Some(cursor) = cursor.map(str::trim).filter(|value| !value.is_empty()) else {
        return Ok(0);
    };
    cursor
        .parse::<usize>()
        .map_err(|_| SdkWorkResultCode::InvalidParameter)
}

/// Collect at most `limit + 1` items from an ordered iterator after skipping `offset` rows.
pub fn offset_limit_page_from_iter<I, T>(iter: I, limit: usize, offset: usize) -> OffsetLimitPage<T>
where
    I: Iterator<Item = T>,
{
    if limit == 0 {
        return OffsetLimitPage {
            items: Vec::new(),
            next_cursor: None,
            has_more: false,
        };
    }

    let mut skipped = 0usize;
    let mut items = Vec::with_capacity(limit.saturating_add(1));
    let mut has_more = false;

    for item in iter {
        if skipped < offset {
            skipped += 1;
            continue;
        }
        items.push(item);
        if items.len() > limit {
            has_more = true;
            break;
        }
    }

    if has_more {
        items.truncate(limit);
    }

    let next_cursor = has_more.then(|| offset.saturating_add(items.len()).to_string());
    OffsetLimitPage {
        items,
        next_cursor,
        has_more,
    }
}

/// Parsed offset-mode cursor list parameters (`page_size` + numeric offset cursor).
#[derive(Clone, Copy, Debug, Eq, PartialEq)]
pub struct CursorListPageParams {
    pub page_size: usize,
    pub offset: usize,
}

impl CursorListPageParams {
    pub fn resolve(
        page_size: Option<i32>,
        limit: Option<i32>,
        cursor: Option<&str>,
    ) -> Result<Self, SdkWorkResultCode> {
        let page_size = page_size
            .or(limit)
            .map(i64::from)
            .unwrap_or(i64::from(DEFAULT_LIST_PAGE_SIZE))
            .clamp(1, i64::from(MAX_LIST_PAGE_SIZE)) as usize;
        let offset = parse_offset_list_cursor(cursor)?;
        Ok(Self { page_size, offset })
    }
}

fn deserialize_option_query_u64<'de, D>(deserializer: D) -> Result<Option<u64>, D::Error>
where
    D: Deserializer<'de>,
{
    struct OptU64Visitor;

    impl Visitor<'_> for OptU64Visitor {
        type Value = Option<u64>;

        fn expecting(&self, formatter: &mut fmt::Formatter) -> fmt::Result {
            formatter.write_str("an optional unsigned integer")
        }

        fn visit_none<E>(self) -> Result<Self::Value, E>
        where
            E: de::Error,
        {
            Ok(None)
        }

        fn visit_unit<E>(self) -> Result<Self::Value, E>
        where
            E: de::Error,
        {
            Ok(None)
        }

        fn visit_i64<E>(self, value: i64) -> Result<Self::Value, E>
        where
            E: de::Error,
        {
            u64::try_from(value)
                .map(Some)
                .map_err(|_| E::invalid_value(Unexpected::Signed(value), &self))
        }

        fn visit_u64<E>(self, value: u64) -> Result<Self::Value, E>
        where
            E: de::Error,
        {
            Ok(Some(value))
        }

        fn visit_str<E>(self, value: &str) -> Result<Self::Value, E>
        where
            E: de::Error,
        {
            let trimmed = value.trim();
            if trimmed.is_empty() {
                return Ok(None);
            }
            trimmed.parse::<u64>().map(Some).map_err(E::custom)
        }
    }

    deserializer.deserialize_any(OptU64Visitor)
}

fn deserialize_option_query_i32<'de, D>(deserializer: D) -> Result<Option<i32>, D::Error>
where
    D: Deserializer<'de>,
{
    struct OptI32Visitor;

    impl Visitor<'_> for OptI32Visitor {
        type Value = Option<i32>;

        fn expecting(&self, formatter: &mut fmt::Formatter) -> fmt::Result {
            formatter.write_str("an optional integer")
        }

        fn visit_none<E>(self) -> Result<Self::Value, E>
        where
            E: de::Error,
        {
            Ok(None)
        }

        fn visit_unit<E>(self) -> Result<Self::Value, E>
        where
            E: de::Error,
        {
            Ok(None)
        }

        fn visit_i64<E>(self, value: i64) -> Result<Self::Value, E>
        where
            E: de::Error,
        {
            i32::try_from(value)
                .map(Some)
                .map_err(|_| E::invalid_value(Unexpected::Signed(value), &self))
        }

        fn visit_u64<E>(self, value: u64) -> Result<Self::Value, E>
        where
            E: de::Error,
        {
            i32::try_from(value)
                .map(Some)
                .map_err(|_| E::invalid_value(Unexpected::Unsigned(value), &self))
        }

        fn visit_str<E>(self, value: &str) -> Result<Self::Value, E>
        where
            E: de::Error,
        {
            let trimmed = value.trim();
            if trimmed.is_empty() {
                return Ok(None);
            }
            trimmed.parse::<i32>().map(Some).map_err(E::custom)
        }
    }

    deserializer.deserialize_any(OptI32Visitor)
}

/// Standard cursor/offset list query (`pageSize` wire; legacy `limit` alias accepted).
#[derive(Clone, Debug, Default, Eq, PartialEq, Deserialize)]
#[serde(rename_all = "camelCase", default)]
pub struct SdkWorkCursorListQuery {
    #[serde(
        alias = "limit",
        default,
        deserialize_with = "deserialize_option_query_i32"
    )]
    pub page_size: Option<i32>,
    pub cursor: Option<String>,
}

impl SdkWorkCursorListQuery {
    pub fn resolve(&self) -> Result<CursorListPageParams, SdkWorkResultCode> {
        CursorListPageParams::resolve(self.page_size, None, self.cursor.as_deref())
    }
}

/// Single-field page size query (`pageSize` wire; legacy `limit` alias accepted).
#[derive(Clone, Debug, Default, Eq, PartialEq, Deserialize)]
#[serde(rename_all = "camelCase", default)]
pub struct SdkWorkPageSizeQuery {
    #[serde(
        alias = "limit",
        default,
        deserialize_with = "deserialize_option_query_i32"
    )]
    pub page_size: Option<i32>,
}

impl SdkWorkPageSizeQuery {
    pub fn resolve(&self) -> usize {
        self.page_size
            .map(i64::from)
            .unwrap_or(i64::from(DEFAULT_LIST_PAGE_SIZE))
            .clamp(1, i64::from(MAX_LIST_PAGE_SIZE)) as usize
    }

    pub fn resolve_i64(&self) -> i64 {
        i64::try_from(self.resolve()).unwrap_or(i64::from(MAX_LIST_PAGE_SIZE))
    }
}

/// Sequence-window list query for message/timeline feeds (`afterSeq` + `pageSize`).
#[derive(Clone, Debug, Default, Eq, PartialEq, Deserialize)]
#[serde(rename_all = "camelCase", default)]
pub struct SdkWorkSeqWindowQuery {
    #[serde(default, deserialize_with = "deserialize_option_query_u64")]
    pub after_seq: Option<u64>,
    #[serde(
        alias = "limit",
        default,
        deserialize_with = "deserialize_option_query_i32"
    )]
    pub page_size: Option<i32>,
}

impl SdkWorkSeqWindowQuery {
    pub fn resolved_page_size(&self) -> usize {
        self.page_size
            .map(i64::from)
            .unwrap_or(i64::from(DEFAULT_LIST_PAGE_SIZE))
            .clamp(1, i64::from(MAX_LIST_PAGE_SIZE)) as usize
    }
}

/// Build standard offset-mode `PageInfo` for numeric cursor windows.
pub fn offset_limit_page_info(next_cursor: Option<String>, has_more: bool) -> PageInfo {
    offset_window_page_info(None, next_cursor, has_more)
}

/// Build offset-mode `PageInfo` including resolved `pageSize` when available.
pub fn offset_window_page_info(
    page_size: Option<usize>,
    next_cursor: Option<String>,
    has_more: bool,
) -> PageInfo {
    PageInfo {
        mode: PageMode::Offset,
        page: None,
        page_size: page_size.map(|value| value as i32),
        total_items: None,
        total_pages: None,
        next_cursor,
        has_more: Some(has_more),
    }
}

/// Build cursor-mode `PageInfo` for opaque or numeric continuation tokens.
pub fn cursor_window_page_info(
    page_size: Option<usize>,
    next_cursor: Option<String>,
    has_more: bool,
) -> PageInfo {
    PageInfo {
        mode: PageMode::Cursor,
        page: None,
        page_size: page_size.map(|value| value as i32),
        total_items: None,
        total_pages: None,
        next_cursor,
        has_more: Some(has_more),
    }
}

/// Build standard cursor-mode `SdkWorkPageData`.
pub fn cursor_list_page_data<T>(
    items: Vec<T>,
    page_size: usize,
    next_cursor: Option<String>,
    has_more: bool,
) -> SdkWorkPageData<T> {
    SdkWorkPageData {
        items,
        page_info: cursor_window_page_info(Some(page_size), next_cursor, has_more),
    }
}

/// Standard single-resource payload inside `SdkWorkApiResponse.data`.
#[derive(Clone, Debug, Eq, PartialEq, Serialize, Deserialize)]
pub struct SdkWorkResourceData<T> {
    pub item: T,
}

/// Serialize standard single-resource payload (`SdkWorkResourceResponse.data`).
pub fn sdkwork_resource_json(item: serde_json::Value) -> serde_json::Value {
    serde_json::to_value(SdkWorkResourceData { item })
        .unwrap_or_else(|_| serde_json::json!({ "item": serde_json::Value::Null }))
}

/// Serialize hierarchical tree payload: `{ "item": { "nodes": [...] } }`.
pub fn sdkwork_tree_resource_json(nodes: Vec<serde_json::Value>) -> serde_json::Value {
    sdkwork_resource_json(serde_json::json!({ "nodes": nodes }))
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

    #[test]
    fn validated_offset_list_params_rejects_invalid_page_size() {
        assert_eq!(
            validated_offset_list_params(Some(1), Some(0)),
            Err(SdkWorkResultCode::InvalidParameter)
        );
        assert_eq!(
            validated_offset_list_params(Some(1), Some(201)),
            Err(SdkWorkResultCode::InvalidParameter)
        );
    }

    #[test]
    fn validated_offset_list_params_rejects_invalid_page() {
        assert_eq!(
            validated_offset_list_params(Some(0), Some(20)),
            Err(SdkWorkResultCode::InvalidParameter)
        );
    }

    #[test]
    fn validated_offset_list_params_defaults_match_spec() {
        let params = validated_offset_list_params(None, None).expect("defaults");
        assert_eq!(params.page, 1);
        assert_eq!(params.page_size, 20);
        assert_eq!(params.offset, 0);
    }

    #[test]
    fn offset_list_page_params_default_to_spec_page_size() {
        let params = OffsetListPageParams::parse(None, None);
        assert_eq!(1, params.page);
        assert_eq!(20, params.page_size);
        assert_eq!(0, params.offset);
    }

    #[test]
    fn offset_list_page_info_reports_has_more_from_total() {
        let params = OffsetListPageParams::parse(Some(1), Some(20));
        let info = offset_list_page_info(45, params);
        assert_eq!(Some(PageMode::Offset), Some(info.mode));
        assert_eq!(Some(3), info.total_pages);
        assert_eq!(Some(true), info.has_more);
        assert_eq!(Some("45".to_owned()), info.total_items);
    }

    #[test]
    fn offset_limit_page_from_iter_applies_cursor_without_materializing_full_collection() {
        let page = offset_limit_page_from_iter((1..=5).map(|value| value.to_string()), 2, 1);
        assert_eq!(page.items, vec!["2".to_owned(), "3".to_owned()]);
        assert!(page.has_more);
        assert_eq!(page.next_cursor.as_deref(), Some("3"));
    }

    #[test]
    fn parse_offset_list_cursor_defaults_to_zero() {
        assert_eq!(parse_offset_list_cursor(None).expect("missing cursor"), 0);
        assert_eq!(
            parse_offset_list_cursor(Some("  ")).expect("blank cursor"),
            0
        );
        assert_eq!(
            parse_offset_list_cursor(Some("4")).expect("numeric cursor"),
            4
        );
    }

    #[test]
    fn cursor_list_page_params_resolve_page_size_and_legacy_limit() {
        let from_page_size =
            CursorListPageParams::resolve(Some(10), None, Some("20")).expect("page size");
        assert_eq!(from_page_size.page_size, 10);
        assert_eq!(from_page_size.offset, 20);

        let from_limit = CursorListPageParams::resolve(None, Some(15), None).expect("limit");
        assert_eq!(from_limit.page_size, 15);
        assert_eq!(from_limit.offset, 0);
    }

    #[test]
    fn sdkwork_cursor_list_query_deserializes_page_size_and_limit_alias() {
        let from_page_size: SdkWorkCursorListQuery =
            serde_json::from_str(r#"{"pageSize":12,"cursor":"3"}"#).expect("pageSize");
        assert_eq!(from_page_size.resolve().expect("resolve").page_size, 12);

        let from_limit: SdkWorkCursorListQuery =
            serde_json::from_str(r#"{"limit":8,"cursor":"1"}"#).expect("limit");
        assert_eq!(from_limit.resolve().expect("resolve").page_size, 8);
    }

    #[derive(Debug, Default, Deserialize)]
    #[serde(rename_all = "camelCase", default)]
    struct FlattenedPageSizeListQuery {
        pub after_audit_seq: Option<u64>,
        #[serde(flatten)]
        pub paging: SdkWorkPageSizeQuery,
    }

    #[test]
    fn flattened_page_size_query_deserializes_from_urlencoded_query_string() {
        let query: FlattenedPageSizeListQuery =
            serde_urlencoded::from_str("afterAuditSeq=0&pageSize=2").expect("urlencoded query");
        assert_eq!(query.after_audit_seq, Some(0));
        assert_eq!(query.paging.resolve(), 2);
    }

    #[derive(Debug, Default, Deserialize)]
    #[serde(rename_all = "camelCase", default)]
    struct FlattenedSeqWindowListQuery {
        #[serde(flatten)]
        pub paging: SdkWorkSeqWindowQuery,
    }

    #[test]
    fn sdkwork_seq_window_query_deserializes_after_seq_from_urlencoded_query_string() {
        let query: SdkWorkSeqWindowQuery =
            serde_urlencoded::from_str("afterSeq=0&pageSize=2").expect("urlencoded query");
        assert_eq!(query.after_seq, Some(0));
        assert_eq!(query.resolved_page_size(), 2);

        let flattened: FlattenedSeqWindowListQuery =
            serde_urlencoded::from_str("afterSeq=0&limit=3").expect("flattened urlencoded query");
        assert_eq!(flattened.paging.after_seq, Some(0));
        assert_eq!(flattened.paging.resolved_page_size(), 3);
    }
}
