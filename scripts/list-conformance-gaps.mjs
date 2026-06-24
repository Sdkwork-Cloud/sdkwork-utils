import { readFileSync } from "node:fs";
import { fileURLToPath } from "node:url";
import path from "node:path";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const contract = JSON.parse(readFileSync(path.join(root, "specs", "utils.contract.json"), "utf8"));
const thresholdsConfig = JSON.parse(
  readFileSync(path.join(root, "specs", "conformance", "coverage-thresholds.json"), "utf8"),
);

const langs = {
  java: "packages/sdkwork-utils-java/src/test/java/com/sdkwork/utils/ConformanceTest.java",
  kotlin: "packages/sdkwork-utils-kotlin/src/test/kotlin/com/sdkwork/utils/ConformanceTest.kt",
  csharp: "packages/sdkwork-utils-csharp/Sdkwork.Utils.Tests/ConformanceTests.cs",
  go: [
    "packages/sdkwork-utils-go/conformance_test.go",
    "packages/sdkwork-utils-go/conformance_extra_test.go",
  ],
  rust: "packages/sdkwork-utils-rust/tests/conformance.rs",
};

const operations = [];
for (const [moduleName, moduleOps] of Object.entries(contract.modules)) {
  for (const operationName of Object.keys(moduleOps)) {
    operations.push({ moduleName, operationName, key: `${moduleName}.${operationName}` });
  }
}

function isCovered(text, moduleName, operationName, key) {
  const aliases = thresholdsConfig.covered_by?.[key] ?? [key];
  for (const alias of aliases) {
    const [aliasModule, aliasOperation] = alias.split(".");
    const patterns = [
      `${aliasModule}.${aliasOperation}`,
      `${aliasModule}"]["${aliasOperation}`,
      `${aliasModule}']['${aliasOperation}`,
      `${aliasModule}.get("${aliasOperation}")`,
      `${aliasModule}.getProperty("${aliasOperation}")`,
      `${aliasModule}.getJSONArray("${aliasOperation}")`,
      `getJSONArray("${aliasOperation}")`,
      `GetProperty("${aliasOperation}")`,
      `fixtures.${aliasModule}.${aliasOperation}`,
      `FIXTURES["${aliasModule}"]["${aliasOperation}"]`,
      `${aliasModule}Cases.get("${aliasOperation}")`,
    ];
    if (patterns.some((pattern) => text.includes(pattern))) {
      return true;
    }
  }
  if (moduleName === operationName) {
    return false;
  }
  const camel = operationName.replace(/_([a-z])/g, (_, char) => char.toUpperCase());
  const pascal = camel.charAt(0).toUpperCase() + camel.slice(1);
  const utilPatterns = [`${pascal}(`, `${camel}(`, `::${camel}(`, `.${camel}(`, `.${pascal}(`];
  return utilPatterns.some((pattern) => text.includes(pattern));
}

for (const [lang, relativePath] of Object.entries(langs)) {
  const paths = Array.isArray(relativePath) ? relativePath : [relativePath];
  const text = paths.map((entry) => readFileSync(path.join(root, entry), "utf8")).join("\n");
  const missing = operations.filter((op) => !isCovered(text, op.moduleName, op.operationName, op.key));
  const covered = operations.length - missing.length;
  const percent = Math.round((covered / operations.length) * 100);
  console.log(`\n${lang}: ${covered}/${operations.length} (${percent}%), missing ${missing.length}`);
  for (const op of missing) {
    console.log(`  ${op.key}`);
  }
}
