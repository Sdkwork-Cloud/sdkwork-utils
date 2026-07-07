/** SDKWork HTTP API wire contracts (`API_SPEC.md` §14–§16). */

import { DEFAULT_LIST_PAGE_SIZE, MAX_LIST_PAGE_SIZE } from "./pagination.js";
import { isBlank, trim } from "./string.js";

export const SDKWORK_TRACE_ID_HEADER = "X-SdkWork-Trace-Id";
export const SDKWORK_SUCCESS_CODE = 0 as const;

export const SdkWorkResultCode = {
  OK: 0,
  VALIDATION_ERROR: 40001,
  MALFORMED_REQUEST: 40002,
  INVALID_PARAMETER: 40003,
  MISSING_REQUIRED_FIELD: 40004,
  AUTHENTICATION_REQUIRED: 40101,
  TOKEN_EXPIRED: 40102,
  INVALID_TOKEN: 40103,
  SESSION_REVOKED: 40104,
  PERMISSION_REQUIRED: 40301,
  INSUFFICIENT_SCOPE: 40302,
  TENANT_ACCESS_DENIED: 40303,
  ORGANIZATION_ACCESS_DENIED: 40304,
  NOT_FOUND: 40401,
  METHOD_NOT_ALLOWED: 40501,
  REQUEST_TIMEOUT: 40801,
  CONFLICT: 40901,
  GONE: 41001,
  PRECONDITION_FAILED: 41201,
  PAYLOAD_TOO_LARGE: 41301,
  UNSUPPORTED_MEDIA_TYPE: 41501,
  UNPROCESSABLE_ENTITY: 42201,
  LOCKED: 42301,
  PRECONDITION_REQUIRED: 42801,
  RATE_LIMIT_EXCEEDED: 42901,
  INTERNAL_ERROR: 50001,
  BAD_GATEWAY: 50201,
  SERVICE_UNAVAILABLE: 50301,
  GATEWAY_TIMEOUT: 50401,
} as const;

export type SdkWorkResultCodeValue =
  (typeof SdkWorkResultCode)[keyof typeof SdkWorkResultCode];

export type SdkWorkApiResponse<TData> = {
  code: typeof SDKWORK_SUCCESS_CODE;
  data: TData;
  traceId: string;
};

export type PageMode = "offset" | "cursor";

export type PageInfo = {
  mode: PageMode;
  page?: number;
  pageSize?: number;
  totalItems?: string;
  totalPages?: number;
  nextCursor?: string | null;
  hasMore?: boolean;
};

export type SdkWorkPageData<TItem> = {
  items: TItem[];
  pageInfo: PageInfo;
};

export type SdkWorkResourceData<TItem> = {
  item: TItem;
};

export type SdkWorkProblemDetail = {
  type: string;
  title: string;
  status: number;
  code: number;
  traceId: string;
  detail?: string;
  errors?: Array<{ field: string; message: string; code?: number }>;
};

export function isSdkWorkSuccessCode(code: number): code is typeof SDKWORK_SUCCESS_CODE {
  return code === SDKWORK_SUCCESS_CODE;
}

export function extractSdkWorkResourceItem<T>(response: unknown): T {
  if (response && typeof response === "object" && "data" in response) {
    const data = (response as SdkWorkApiResponse<SdkWorkResourceData<T> | T>).data;
    if (data && typeof data === "object" && "item" in data) {
      return (data as SdkWorkResourceData<T>).item;
    }
    return data as T;
  }
  return response as T;
}

export function unwrapSdkWorkApiResponse<TData>(
  envelope: SdkWorkApiResponse<TData>,
): TData {
  if (envelope.code !== SDKWORK_SUCCESS_CODE) {
    throw new Error(`Unexpected non-success SdkWorkApiResponse.code: ${envelope.code}`);
  }
  return envelope.data;
}

function readNumericResultCode(code: unknown): number | undefined {
  if (typeof code === "number" && Number.isInteger(code)) {
    return code;
  }
  if (typeof code === "string" && !isBlank(code) && /^-?\d+$/u.test(trim(code))) {
    return Number.parseInt(trim(code), 10);
  }
  return undefined;
}

export function isSdkWorkApiEnvelope(
  value: unknown,
): value is { code: unknown; data?: unknown; traceId?: string } {
  return Boolean(value && typeof value === "object" && "code" in value);
}

export function readSdkWorkProblemMessage(response: unknown, fallbackMessage: string): string {
  if (!response || typeof response !== "object") {
    return fallbackMessage;
  }
  const record = response as Record<string, unknown>;
  const detail = typeof record.detail === "string" ? trim(record.detail) : "";
  if (detail) {
    return detail;
  }
  const title = typeof record.title === "string" ? trim(record.title) : "";
  if (title) {
    return title;
  }
  const message = typeof record.message === "string" ? trim(record.message) : "";
  if (message) {
    return message;
  }
  const msg = typeof record.msg === "string" ? trim(record.msg) : "";
  if (msg) {
    return msg;
  }
  return fallbackMessage;
}

export function assertSdkWorkEnvelopeSuccess(
  response: unknown,
  fallbackMessage = "Request failed",
): void {
  if (!isSdkWorkApiEnvelope(response)) {
    return;
  }
  const numericCode = readNumericResultCode(response.code);
  if (numericCode === undefined || !isSdkWorkSuccessCode(numericCode)) {
    throw new Error(readSdkWorkProblemMessage(response, fallbackMessage));
  }
}

export function readSdkWorkEnvelopeData<TData>(response: unknown): TData {
  assertSdkWorkEnvelopeSuccess(response);
  if (isSdkWorkApiEnvelope(response) && "data" in response) {
    return (response as SdkWorkApiResponse<TData>).data;
  }
  return response as TData;
}

function readPageInfoField(pageInfo: unknown, legacy: Record<string, unknown>): PageInfo {
  if (pageInfo && typeof pageInfo === "object") {
    const record = pageInfo as Record<string, unknown>;
    const mode = record.mode === "cursor" ? "cursor" : "offset";
    const pageSize =
      typeof record.pageSize === "number"
        ? record.pageSize
        : typeof record.page_size === "number"
          ? record.page_size
          : undefined;
    return {
      mode,
      ...(typeof record.page === "number" ? { page: record.page } : {}),
      ...(pageSize !== undefined ? { pageSize } : {}),
      ...(typeof record.hasMore === "boolean" ? { hasMore: record.hasMore } : {}),
      ...(record.nextCursor !== undefined ? { nextCursor: record.nextCursor as string | null } : {}),
      ...(record.totalItems !== undefined ? { totalItems: String(record.totalItems) } : {}),
      ...(typeof record.totalPages === "number" ? { totalPages: record.totalPages } : {}),
    };
  }

  const page = typeof legacy.page === "number" ? legacy.page : undefined;
  const pageSize =
    typeof legacy.pageSize === "number"
      ? legacy.pageSize
      : typeof legacy.page_size === "number"
        ? legacy.page_size
        : undefined;
  const total = legacy.total;
  const hasMore =
    typeof legacy.hasMore === "boolean"
      ? legacy.hasMore
      : page !== undefined && pageSize !== undefined && typeof total === "number"
        ? page * pageSize < total
        : false;

  return {
    mode: "offset",
    ...(page !== undefined ? { page } : {}),
    ...(pageSize !== undefined ? { pageSize } : {}),
    hasMore,
    ...(total !== undefined ? { totalItems: String(total) } : {}),
  };
}

export function extractSdkWorkListPage<TItem>(data: unknown): SdkWorkPageData<TItem> {
  if (Array.isArray(data)) {
    return {
      items: data,
      pageInfo: {
        mode: "offset",
        page: 1,
        pageSize: data.length,
        hasMore: false,
      },
    };
  }

  const record = data && typeof data === "object" ? (data as Record<string, unknown>) : null;
  if (!record) {
    throw new Error("List response data is required");
  }

  const items = Array.isArray(record.items) ? (record.items as TItem[]) : [];
  return {
    items,
    pageInfo: readPageInfoField(record.pageInfo, record),
  };
}

export async function collectSdkWorkOffsetListPages<TItem>(
  fetchPage: (query: { page: number; page_size: number }) => Promise<SdkWorkPageData<TItem>>,
  options: { page_size?: number; maxPages?: number } = {},
): Promise<TItem[]> {
  const page_size = Math.min(
    MAX_LIST_PAGE_SIZE,
    Math.max(1, options.page_size ?? DEFAULT_LIST_PAGE_SIZE),
  );
  const maxPages = options.maxPages ?? 100;
  const collected: TItem[] = [];
  let page = 1;
  let hasMore = true;

  while (hasMore && page <= maxPages) {
    const result = await fetchPage({ page, page_size });
    collected.push(...result.items);
    hasMore = result.pageInfo.hasMore === true;
    page += 1;
  }

  return collected;
}
