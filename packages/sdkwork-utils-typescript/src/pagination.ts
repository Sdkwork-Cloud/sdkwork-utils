/** SDKWork list pagination helpers (`PAGINATION_SPEC.md`, `API_SPEC.md` §14.1/§16). */

export const DEFAULT_LIST_PAGE_SIZE = 20;
export const MAX_LIST_PAGE_SIZE = 200;

export type SdkWorkOffsetListQuery = {
  page?: number;
  page_size?: number;
};

export type OffsetListPageParams = {
  offset: number;
  pageSize: number;
};

export type OffsetListPage<TItem> = OffsetListPageParams & {
  items: TItem[];
  total: number;
};

/** Normalize `page` / `page_size` query params to SDKWork bounds (default 20, max 200). */
export function normalizeOffsetListQuery(
  query: SdkWorkOffsetListQuery = {},
): { page: number; page_size: number } {
  const page =
    typeof query.page === "number" && Number.isFinite(query.page)
      ? Math.max(1, Math.floor(query.page))
      : 1;
  const rawPageSize =
    typeof query.page_size === "number" && Number.isFinite(query.page_size)
      ? Math.floor(query.page_size)
      : DEFAULT_LIST_PAGE_SIZE;
  const page_size = Math.min(MAX_LIST_PAGE_SIZE, Math.max(1, rawPageSize));
  return { page, page_size };
}

export function offsetFromPage(page: number, pageSize: number): number {
  return (page - 1) * pageSize;
}

/** Normalize offset/limit to SDKWork bounds (default page size 20, max 200). */
export function clampListPageSize(
  offset?: number,
  limit?: number,
): OffsetListPageParams {
  const normalizedOffset =
    typeof offset === "number" && Number.isFinite(offset)
      ? Math.max(0, Math.floor(offset))
      : 0;
  const rawLimit =
    typeof limit === "number" && Number.isFinite(limit) ? Math.floor(limit) : DEFAULT_LIST_PAGE_SIZE;
  const pageSize = Math.min(MAX_LIST_PAGE_SIZE, Math.max(1, rawLimit));
  return { offset: normalizedOffset, pageSize };
}

/** Slice a materialized list after filters; use only when SQL pagination is unavailable. */
export function paginateItems<TItem>(
  items: readonly TItem[],
  options: { offset?: number; limit?: number } = {},
): OffsetListPage<TItem> {
  const { offset, pageSize } = clampListPageSize(options.offset, options.limit);
  const total = items.length;
  return {
    items: items.slice(offset, offset + pageSize),
    total,
    offset,
    pageSize,
  };
}
