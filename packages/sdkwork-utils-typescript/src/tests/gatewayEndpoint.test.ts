import assert from "node:assert/strict";
import test from "node:test";
import {
  resolveGatewayEndpoint,
  resolveGatewayEndpointForKind,
  resolveGatewayEndpoints,
} from "../gatewayEndpoint.js";
import { buildSharedGatewayToolSnippets } from "../gatewayToolSnippets.js";

test("resolveGatewayEndpoint maps provider suffixes", () => {
  assert.equal(
    resolveGatewayEndpoint("https://console.example.test/v1", "openai"),
    "https://console.example.test/v1",
  );
  assert.equal(
    resolveGatewayEndpoint("https://console.example.test/v1", "anthropic"),
    "https://console.example.test/anthropic",
  );
  assert.equal(
    resolveGatewayEndpoint("https://console.example.test/proxy/v1", "gemini"),
    "https://console.example.test/proxy/google/v1beta",
  );
});

test("resolveGatewayEndpoints returns all provider endpoints", () => {
  const endpoints = resolveGatewayEndpoints("https://gateway.example.test/v1");
  assert.deepEqual(endpoints, {
    openAiBaseUrl: "https://gateway.example.test/v1",
    anthropicBaseUrl: "https://gateway.example.test/anthropic",
    geminiBaseUrl: "https://gateway.example.test/google/v1beta",
  });
});

test("resolveGatewayEndpointForKind selects endpoint by kind", () => {
  const endpoints = resolveGatewayEndpoints("/v1");
  assert.equal(resolveGatewayEndpointForKind("openai", endpoints), "/v1");
  assert.equal(resolveGatewayEndpointForKind("anthropic", endpoints), "/anthropic");
  assert.equal(resolveGatewayEndpointForKind("gemini", endpoints), "/google/v1beta");
});

test("buildSharedGatewayToolSnippets emits canonical gateway tool config", () => {
  const snippets = buildSharedGatewayToolSnippets({
    apiKeyPlaceholder: "<YOUR_CLOUDROUTER_API_KEY>",
    openAiBaseUrl: "https://console.example.test/v1",
    anthropicBaseUrl: "https://console.example.test/anthropic",
    geminiBaseUrl: "https://console.example.test/google/v1beta",
  });

  assert.match(snippets.codex, /model_provider = "cloudrouter"/);
  assert.match(snippets.codex, /env_key = "CLOUDROUTER_API_KEY"/);
  assert.match(
    snippets["claude-code"],
    /ANTHROPIC_BASE_URL="https:\/\/console\.example\.test\/anthropic"/,
  );
  assert.match(
    snippets.gemini,
    /GOOGLE_GEMINI_BASE_URL="https:\/\/console\.example\.test\/google\/v1beta"/,
  );
  assert.match(snippets.opencode, /"npm": "@ai-sdk\/openai-compatible"/);
  assert.match(
    snippets.openclaw,
    /"baseUrl": "https:\/\/console\.example\.test\/v1"/,
  );
  assert.match(snippets.openclaw, /"api": "openai-completions"/);
  assert.match(
    snippets["hermes-agent"],
    /base_url: https:\/\/console\.example\.test\/v1/,
  );
  assert.match(snippets["hermes-agent"], /api_mode: openai_chat/);
  assert.doesNotMatch(snippets["hermes-agent"], /protocol: openai/);
  assert.doesNotMatch(snippets["hermes-agent"], /OPENAI_API_KEY/);
  assert.match(snippets["mimo-code"], /mimocode\.jsonc/);
  assert.match(snippets.rig, /openai::Client::builder\(\)/);
});
