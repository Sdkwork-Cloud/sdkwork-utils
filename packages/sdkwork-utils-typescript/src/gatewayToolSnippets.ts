import type { GatewayEndpointSet } from "./gatewayEndpoint.js";

export type SharedGatewayToolId =
  | "codex"
  | "claude-code"
  | "gemini"
  | "opencode"
  | "openclaw"
  | "hermes-agent"
  | "mimo-code"
  | "rig";

export interface GatewayToolSnippetInput extends GatewayEndpointSet {
  apiKeyPlaceholder: string;
  /** 网关为该 key 解析的模型 ID；缺省时使用 gpt-4o-mini 占位 */
  modelId?: string;
}

export type SharedGatewayToolSnippetMap = Record<SharedGatewayToolId, string>;

/**
 * Builds per-tool config snippets aligned with the real configuration formats
 * of each agent tool (verified against the upstream sources mirrored under
 * sdkwork-kernel/external):
 *
 * - codex:        ~/.codex/config.toml — model_provider + [model_providers.<id>]
 * - claude-code:  ANTHROPIC_BASE_URL + ANTHROPIC_AUTH_TOKEN environment vars
 * - gemini:       ~/.gemini/settings.json auth selection + GEMINI_API_KEY +
 *                 GOOGLE_GEMINI_BASE_URL (base URL alone forces GATEWAY auth
 *                 mode, which the non-interactive CLI rejects)
 * - opencode:     ~/.config/opencode/opencode.json — provider with
 *                 @ai-sdk/openai-compatible npm package
 * - openclaw:     ~/.openclaw/openclaw.json — models.providers.<id> with
 *                 api: "openai-completions" (no type/base_url/api_key fields)
 * - hermes-agent: ~/.hermes/config.yaml — providers.<id> with base_url,
 *                 api_key/key_env and api_mode: "openai_chat"
 * - mimo-code:    ~/.config/mimocode/mimocode.jsonc — provider with
 *                 @ai-sdk/openai-compatible npm package (mimocode.jsonc, not
 *                 opencode.json)
 * - rig:          Rust code — rig::providers::openai::Client::builder()
 *                 with .base_url() (no config file)
 */
export function buildSharedGatewayToolSnippets(
  input: GatewayToolSnippetInput,
): SharedGatewayToolSnippetMap {
  const apiKey = input.apiKeyPlaceholder;
  const modelId = input.modelId?.trim() || "gpt-4o-mini";
  return {
    codex: [
      `export CLOUDROUTER_API_KEY="${apiKey}"`,
      "",
      "# ~/.codex/config.toml",
      'model_provider = "cloudrouter"',
      `model = "${modelId}"`,
      "",
      "[model_providers.cloudrouter]",
      'name = "Cloud Router"',
      `base_url = "${input.openAiBaseUrl}"`,
      'env_key = "CLOUDROUTER_API_KEY"',
      // Only wire_api = "responses" is supported by current Codex CLI.
      'wire_api = "responses"',
    ].join("\n"),
    "claude-code": [
      `export ANTHROPIC_BASE_URL="${input.anthropicBaseUrl}"`,
      `export ANTHROPIC_AUTH_TOKEN="${apiKey}"`,
      "",
      "# Optional: pin the model used by this session, e.g.",
      `# export ANTHROPIC_MODEL="${modelId}"`,
      "",
      "claude",
    ].join("\n"),
    gemini: [
      "# 1) Select the \"Gemini API Key\" auth method once:",
      "#    ~/.gemini/settings.json",
      "{",
      '  "security": { "auth": { "selectedType": "gemini-api-key" } }',
      "}",
      "#    (or run `gemini auth login` and pick Gemini API Key)",
      "",
      "# 2) Point the CLI at the gateway:",
      `export GEMINI_API_KEY="${apiKey}"`,
      `export GOOGLE_GEMINI_BASE_URL="${input.geminiBaseUrl}"`,
      "",
      "# Optional: pin the model, otherwise the CLI defaults to its own model",
      `# gemini -m "${modelId}"`,
      "",
      "gemini",
    ].join("\n"),
    opencode: [
      "{",
      '  "$schema": "https://opencode.ai/config.json",',
      `  "model": "cloudrouter/${modelId}",`,
      '  "provider": {',
      '    "cloudrouter": {',
      '      "npm": "@ai-sdk/openai-compatible",',
      '      "name": "Cloud Router",',
      '      "options": {',
      `        "baseURL": "${input.openAiBaseUrl}",`,
      '        "apiKey": "{env:CLOUDROUTER_API_KEY}"',
      "      },",
      '      "models": {',
      `        "${modelId}": {}`,
      "      }",
      "    }",
      "  }",
      "}",
      "",
      `export CLOUDROUTER_API_KEY="${apiKey}"`,
      "opencode",
    ].join("\n"),
    openclaw: [
      "// ~/.openclaw/openclaw.json",
      "{",
      '  "models": {',
      '    "mode": "merge",',
      '    "providers": {',
      '      "cloudrouter": {',
      `        "baseUrl": "${input.openAiBaseUrl}",`,
      `        "apiKey": "${apiKey}",`,
      '        "api": "openai-completions",',
      '        "models": [',
      `          { "id": "${modelId}", "name": "${modelId}" }`,
      "        ]",
      "      }",
      "    }",
      "  },",
      '  "agents": {',
      '    "defaults": {',
      `      "model": { "primary": "cloudrouter/${modelId}" }`,
      "    }",
      "  }",
      "}",
    ].join("\n"),
    "hermes-agent": [
      "# ~/.hermes/config.yaml",
      "providers:",
      "  cloudrouter:",
      '    name: "Cloud Router"',
      `    base_url: ${input.openAiBaseUrl}`,
      `    api_key: "${apiKey}"`,
      "    api_mode: openai_chat",
      `    model: ${modelId}`,
      "    models:",
      `      ${modelId}:`,
      "        context_length: 131072",
      "",
      "hermes",
    ].join("\n"),
    "mimo-code": [
      "// ~/.config/mimocode/mimocode.jsonc",
      "{",
      '  "$schema": "https://mimo.xiaomi.com/mimocode/config.json",',
      `  "model": "cloudrouter/${modelId}",`,
      '  "provider": {',
      '    "cloudrouter": {',
      '      "name": "Cloud Router",',
      '      "npm": "@ai-sdk/openai-compatible",',
      '      "only_configured_models": true,',
      '      "models": {',
      `        "${modelId}": { "name": "${modelId}" }`,
      "      },",
      '      "options": {',
      `        "baseURL": "${input.openAiBaseUrl}",`,
      `        "apiKey": "${apiKey}"`,
      "      }",
      "    }",
      "  }",
      "}",
      "",
      "# npm install -g @mimo-ai/cli",
      "mimo",
    ].join("\n"),
    rig: [
      "# Cargo.toml",
      "# [dependencies]",
      '# rig = "0.41"',
      '# tokio = { version = "1", features = ["full"] }',
      '# anyhow = "1"',
      "",
      `export CLOUDROUTER_API_KEY="${apiKey}"`,
      "",
      "// src/main.rs",
      "use rig::providers::openai;",
      "",
      "#[tokio::main]",
      "async fn main() -> anyhow::Result<()> {",
      "    let client = openai::Client::builder()",
      '        .api_key(std::env::var("CLOUDROUTER_API_KEY")?)',
      `        .base_url("${input.openAiBaseUrl}")`,
      "        .build()?;",
      "",
      "    let agent = client",
      `        .completion_model("${modelId}")`,
      "        .completions_api()",
      "        .into_agent_builder()",
      '        .preamble("You are a helpful assistant.")',
      "        .build();",
      "",
      '    let response = agent.prompt("Hello!").await?;',
      "    println!(\"{response}\");",
      "    Ok(())",
      "}",
    ].join("\n"),
  };
}
