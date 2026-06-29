/** SDKWork HTTP API wire contracts (`API_SPEC.md` §14–§16). */

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

export function unwrapSdkWorkApiResponse<TData>(
  envelope: SdkWorkApiResponse<TData>,
): TData {
  if (envelope.code !== SDKWORK_SUCCESS_CODE) {
    throw new Error(`Unexpected non-success SdkWorkApiResponse.code: ${envelope.code}`);
  }
  return envelope.data;
}
