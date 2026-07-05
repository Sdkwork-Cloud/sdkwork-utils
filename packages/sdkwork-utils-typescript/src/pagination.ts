/** SDKWork list pagination helpers (`PAGINATION_SPEC.md`, `API_SPEC.md` §14.1/§16). */

export const DEFAULT_LIST_PAGE_SIZE = 20;
export const MAX_LIST_PAGE_SIZE = 200;

export type OffsetListPageParams = {
  offset: number;
  pageSize: number;
};

export type OffsetListPage<TItem> = OffsetListPageParams & {
  items: TItem[];
  total: number;
};

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
