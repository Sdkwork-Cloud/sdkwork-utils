import assert from "node:assert/strict";
import test from "node:test";
import {
  buildSharedGatewayToolSnippets,
  type SharedGatewayToolId,
} from "../gatewayToolSnippets.js";

const INPUT = {
  apiKeyPlaceholder: "<KEY>",
  modelId: "claude-sonnet-4-5",
  openAiBaseUrl: "https://gateway.example.test/v1",
  anthropicBaseUrl: "https://gateway.example.test/anthropic",
  geminiBaseUrl: "https://gateway.example.test/google/v1beta",
};

const EXPECTED_TOOL_IDS: SharedGatewayToolId[] = [
  "codex",
  "claude-code",
  "gemini",
  "opencode",
  "openclaw",
  "hermes-agent",
  "mimo-code",
  "rig",
];

test("buildSharedGatewayToolSnippets covers every supported tool id", () => {
  const snippets = buildSharedGatewayToolSnippets(INPUT);
  for (const id of EXPECTED_TOOL_IDS) {
    assert.equal(typeof snippets[id], "string", `${id} snippet missing`);
    assert.ok(snippets[id].length > 0, `${id} snippet empty`);
  }
});

test("codex snippet uses model_providers table with responses wire api", () => {
  const snippet = buildSharedGatewayToolSnippets(INPUT).codex;
  assert.match(snippet, /# ~\/\.codex\/config\.toml/);
  assert.match(snippet, /model_provider = "cloudrouter"/);
  assert.match(snippet, /model = "claude-sonnet-4-5"/);
  assert.match(snippet, /\[model_providers\.cloudrouter\]/);
  assert.match(snippet, /base_url = "https:\/\/gateway\.example\.test\/v1"/);
  assert.match(snippet, /env_key = "CLOUDROUTER_API_KEY"/);
  assert.match(snippet, /wire_api = "responses"/);
  assert.doesNotMatch(snippet, /wire_api = "chat"/);
});

test("claude-code snippet uses base url and auth token env vars", () => {
  const snippet = buildSharedGatewayToolSnippets(INPUT)["claude-code"];
  assert.match(
    snippet,
    /export ANTHROPIC_BASE_URL="https:\/\/gateway\.example\.test\/anthropic"/,
  );
  assert.match(snippet, /export ANTHROPIC_AUTH_TOKEN="<KEY>"/);
  assert.match(snippet, /ANTHROPIC_MODEL="claude-sonnet-4-5"/);
  assert.match(snippet, /^claude$/m);
});

test("gemini snippet selects gemini-api-key auth before setting base url", () => {
  const snippet = buildSharedGatewayToolSnippets(INPUT).gemini;
  assert.match(snippet, /"selectedType": "gemini-api-key"/);
  assert.match(snippet, /export GEMINI_API_KEY="<KEY>"/);
  assert.match(
    snippet,
    /export GOOGLE_GEMINI_BASE_URL="https:\/\/gateway\.example\.test\/google\/v1beta"/,
  );
  assert.match(snippet, /gemini -m "claude-sonnet-4-5"/);
  assert.match(snippet, /^gemini$/m);
});

test("opencode snippet uses @ai-sdk/openai-compatible npm provider", () => {
  const snippet = buildSharedGatewayToolSnippets(INPUT).opencode;
  assert.match(snippet, /"npm": "@ai-sdk\/openai-compatible"/);
  assert.match(snippet, /"baseURL": "https:\/\/gateway\.example\.test\/v1"/);
  assert.match(snippet, /"apiKey": "\{env:CLOUDROUTER_API_KEY\}"/);
  assert.match(snippet, /"model": "cloudrouter\/claude-sonnet-4-5"/);
  assert.match(snippet, /"claude-sonnet-4-5": \{\}/);
  assert.match(snippet, /^opencode$/m);
});

test("openclaw snippet uses openclaw.json models.providers shape", () => {
  const snippet = buildSharedGatewayToolSnippets(INPUT).openclaw;
  assert.match(snippet, /\/\/ ~\/\.openclaw\/openclaw\.json/);
  assert.doesNotMatch(snippet, /config\.yaml/);
  assert.match(snippet, /"providers": \{/);
  assert.match(snippet, /"api": "openai-completions"/);
  assert.match(snippet, /"baseUrl": "https:\/\/gateway\.example\.test\/v1"/);
  assert.match(snippet, /"apiKey": "<KEY>"/);
  assert.match(snippet, /"id": "claude-sonnet-4-5"/);
  assert.match(snippet, /"primary": "cloudrouter\/claude-sonnet-4-5"/);
  assert.doesNotMatch(snippet, /type: openai-compatible/);
});

test("hermes-agent snippet uses config.yaml providers dict shape", () => {
  const snippet = buildSharedGatewayToolSnippets(INPUT)["hermes-agent"];
  assert.match(snippet, /# ~\/\.hermes\/config\.yaml/);
  assert.doesNotMatch(snippet, /agent\.yaml/);
  assert.match(snippet, /providers:/);
  assert.match(snippet, /base_url: https:\/\/gateway\.example\.test\/v1/);
  assert.match(snippet, /api_key: "<KEY>"/);
  assert.match(snippet, /api_mode: openai_chat/);
  assert.match(snippet, /model: claude-sonnet-4-5/);
  assert.doesNotMatch(snippet, /protocol:/);
  assert.doesNotMatch(snippet, /capabilities:/);
  assert.match(snippet, /^hermes$/m);
});

test("mimo-code snippet uses mimocode.jsonc provider shape", () => {
  const snippet = buildSharedGatewayToolSnippets(INPUT)["mimo-code"];
  assert.match(snippet, /\/\/ ~\/\.config\/mimocode\/mimocode\.jsonc/);
  assert.match(snippet, /"npm": "@ai-sdk\/openai-compatible"/);
  assert.match(snippet, /"only_configured_models": true/);
  assert.match(snippet, /"baseURL": "https:\/\/gateway\.example\.test\/v1"/);
  assert.match(snippet, /"apiKey": "<KEY>"/);
  assert.match(snippet, /"model": "cloudrouter\/claude-sonnet-4-5"/);
  assert.match(snippet, /npm install -g @mimo-ai\/cli/);
  assert.match(snippet, /^mimo$/m);
});

test("rig snippet uses openai ClientBuilder with base_url", () => {
  const snippet = buildSharedGatewayToolSnippets(INPUT).rig;
  assert.match(snippet, /rig = "0\.41"/);
  assert.match(snippet, /use rig::providers::openai;/);
  assert.match(snippet, /openai::Client::builder\(\)/);
  assert.match(snippet, /\.base_url\("https:\/\/gateway\.example\.test\/v1"\)/);
  assert.match(snippet, /\.completion_model\("claude-sonnet-4-5"\)/);
  assert.match(snippet, /\.completions_api\(\)/);
  assert.match(snippet, /CLOUDROUTER_API_KEY/);
});

test("snippets fall back to gpt-4o-mini when modelId is omitted", () => {
  const { modelId: _omitted, ...inputWithoutModel } = INPUT;
  const snippets = buildSharedGatewayToolSnippets(inputWithoutModel);
  assert.match(snippets.codex, /model = "gpt-4o-mini"/);
  assert.match(snippets.opencode, /"model": "cloudrouter\/gpt-4o-mini"/);
  assert.match(snippets.rig, /\.completion_model\("gpt-4o-mini"\)/);
});

test("snippets embed the api key placeholder everywhere", () => {
  const snippets = buildSharedGatewayToolSnippets(INPUT);
  for (const [id, snippet] of Object.entries(snippets)) {
    if (id === "rig") {
      // rig reads the key from the environment variable
      assert.match(snippet, /CLOUDROUTER_API_KEY/);
    } else {
      assert.ok(
        snippet.includes("<KEY>"),
        `${id} snippet does not reference the api key`,
      );
    }
  }
});
