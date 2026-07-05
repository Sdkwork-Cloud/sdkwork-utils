export type GatewayEndpointKind = "openai" | "anthropic" | "gemini";

export interface GatewayEndpointSet {
  openAiBaseUrl: string;
  anthropicBaseUrl: string;
  geminiBaseUrl: string;
}

export function resolveGatewayEndpoint(baseUrl: string, kind: GatewayEndpointKind): string {
  const normalizedBaseUrl = normalizeGatewayBaseUrl(baseUrl);
  if (kind === "anthropic") {
    return replaceGatewaySuffix(normalizedBaseUrl, ["anthropic"]);
  }
  if (kind === "gemini") {
    return replaceGatewaySuffix(normalizedBaseUrl, ["google", "v1beta"]);
  }
  return normalizedBaseUrl || "/v1";
}

export function resolveGatewayEndpoints(baseUrl = "/v1"): GatewayEndpointSet {
  return {
    openAiBaseUrl: resolveGatewayEndpoint(baseUrl, "openai"),
    anthropicBaseUrl: resolveGatewayEndpoint(baseUrl, "anthropic"),
    geminiBaseUrl: resolveGatewayEndpoint(baseUrl, "gemini"),
  };
}

export function resolveGatewayEndpointForKind(
  kind: GatewayEndpointKind,
  endpoints: GatewayEndpointSet,
): string {
  if (kind === "anthropic") {
    return endpoints.anthropicBaseUrl;
  }
  if (kind === "gemini") {
    return endpoints.geminiBaseUrl;
  }
  return endpoints.openAiBaseUrl;
}

function normalizeGatewayBaseUrl(baseUrl: string): string {
  const trimmed = baseUrl.trim().replace(/\/+$/, "");
  if (!trimmed) {
    return "/v1";
  }
  return trimmed.startsWith("/") || /^[a-z][a-z0-9+.-]*:\/\//i.test(trimmed)
    ? trimmed
    : `/${trimmed}`;
}

function replaceGatewaySuffix(baseUrl: string, suffix: string[]): string {
  const segments = splitUrlSegments(baseUrl);
  const baseSegments = stripProviderGatewaySuffix(segments.pathSegments);
  return buildUrlFromSegments(segments.prefix, [...baseSegments, ...suffix]);
}

function splitUrlSegments(value: string): { prefix: string; pathSegments: string[] } {
  if (/^[a-z][a-z0-9+.-]*:\/\//i.test(value)) {
    try {
      const url = new URL(value);
      return {
        prefix: `${url.protocol}//${url.host}`,
        pathSegments: url.pathname.split("/").filter(Boolean),
      };
    } catch {
      return { prefix: "", pathSegments: value.split("/").filter(Boolean) };
    }
  }
  return { prefix: "", pathSegments: value.split("/").filter(Boolean) };
}

function stripProviderGatewaySuffix(pathSegments: string[]): string[] {
  if (endsWithSegments(pathSegments, ["google", "v1beta"])) {
    return pathSegments.slice(0, -2);
  }
  if (endsWithSegments(pathSegments, ["anthropic"])) {
    return pathSegments.slice(0, -1);
  }
  if (endsWithSegments(pathSegments, ["v1"])) {
    return pathSegments.slice(0, -1);
  }
  return pathSegments;
}

function endsWithSegments(value: string[], suffix: string[]): boolean {
  if (suffix.length > value.length) {
    return false;
  }
  return suffix.every((segment, index) => value[value.length - suffix.length + index] === segment);
}

function buildUrlFromSegments(prefix: string, pathSegments: string[]): string {
  const path = pathSegments.length > 0 ? `/${pathSegments.join("/")}` : "";
  return prefix ? `${prefix}${path}` : path || "/";
}
