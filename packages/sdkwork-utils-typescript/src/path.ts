export function joinPath(...segments: string[]): string {
  return segments
    .map((segment) => segment.replace(/^\/+|\/+$/g, ""))
    .filter(Boolean)
    .join("/");
}

export function normalizePath(value: string): string {
  const parts = value.split("/").filter(Boolean);
  const joined = parts.join("/");
  return value.startsWith("/") ? `/${joined}` : joined;
}
