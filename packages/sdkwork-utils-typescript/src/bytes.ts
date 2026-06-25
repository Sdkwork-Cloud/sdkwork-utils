const UNITS = ["B", "KB", "MB", "GB", "TB", "PB"] as const;

export function formatBytes(value: number, decimals = 1): string {
  const bytes = Math.max(0, Math.trunc(value));
  if (bytes < 1024) {
    return `${bytes} B`;
  }

  let size = bytes;
  let unitIndex = 0;
  while (size >= 1024 && unitIndex < UNITS.length - 1) {
    size /= 1024;
    unitIndex += 1;
  }

  return `${size.toFixed(decimals)} ${UNITS[unitIndex]}`;
}
