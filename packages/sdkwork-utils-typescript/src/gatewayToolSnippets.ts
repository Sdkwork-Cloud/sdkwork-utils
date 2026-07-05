import type { GatewayEndpointSet } from "./gatewayEndpoint.js";

export type SharedGatewayToolId =
  | "codex"
  | "claude-code"
  | "gemini"
  | "opencode"
  | "openclaw"
  | "hermes-agent";

export interface GatewayToolSnippetInput extends GatewayEndpointSet {
  apiKeyPlaceholder: string;
}

export type SharedGatewayToolSnippetMap = Record<SharedGatewayToolId, string>;

export function buildSharedGatewayToolSnippets(
  input: GatewayToolSnippetInput,
): SharedGatewayToolSnippetMap {
  const apiKey = input.apiKeyPlaceholder;
  return {
    codex: [
      `export CLAW_ROUTER_API_KEY="${apiKey}"`,
      "",
      "# ~/.codex/config.toml",
      'model_provider = "clawrouter"',
      'model = "gpt-4o-mini"',
      "",
      "[model_providers.clawrouter]",
      'name = "Claw Router"',
      `base_url = "${input.openAiBaseUrl}"`,
      'env_key = "CLAW_ROUTER_API_KEY"',
      'wire_api = "responses"',
    ].join("\n"),
    "claude-code": [
      `export ANTHROPIC_BASE_URL="${input.anthropicBaseUrl}"`,
      `export ANTHROPIC_AUTH_TOKEN="${apiKey}"`,
      "",
      "claude",
    ].join("\n"),
    gemini: [
      `export GEMINI_API_KEY="${apiKey}"`,
      `export GOOGLE_GEMINI_BASE_URL="${input.geminiBaseUrl}"`,
      "",
      "gemini",
    ].join("\n"),
    opencode: [
      "{",
      '  "$schema": "https://opencode.ai/config.json",',
      '  "provider": {',
      '    "clawrouter": {',
      '      "npm": "@ai-sdk/openai-compatible",',
      '      "name": "Claw Router",',
      '      "options": {',
      `        "baseURL": "${input.openAiBaseUrl}",`,
      '        "apiKey": "{env:CLAW_ROUTER_API_KEY}"',
      "      },",
      '      "models": {',
      '        "gpt-4o-mini": {}',
      "      }",
      "    }",
      "  }",
      "}",
      "",
      `export CLAW_ROUTER_API_KEY="${apiKey}"`,
      "opencode",
    ].join("\n"),
    openclaw: [
      "# ~/.openclaw/config.yaml",
      "providers:",
      "  clawrouter:",
      "    type: openai-compatible",
      `    base_url: ${input.openAiBaseUrl}`,
      "    api_key: ${CLAW_ROUTER_API_KEY}",
      "",
      `export CLAW_ROUTER_API_KEY="${apiKey}"`,
      "",
      "openclaw",
    ].join("\n"),
    "hermes-agent": [
      "# ~/.hermes/agent.yaml",
      "providers:",
      "  - name: clawrouter",
      "    protocol: openai",
      `    baseUrl: "${input.openAiBaseUrl}"`,
      "    credentials:",
      `      apiKey: "${apiKey}"`,
      "    models:",
      "      - name: gpt-4o-mini",
      "        capabilities:",
      "          streaming: true",
      "          functionCalling: true",
      "",
      "hermes-agent",
    ].join("\n"),
  };
}
