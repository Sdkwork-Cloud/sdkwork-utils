import { readFileSync, existsSync } from "node:fs";
import { fileURLToPath } from "node:url";
import path from "node:path";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const contract = JSON.parse(
  readFileSync(path.join(root, "specs", "utils.contract.json"), "utf8"),
);

const expectations = {
  rust: (moduleName) =>
    path.join(root, "packages", "sdkwork-utils-rust", "src", `${moduleName}.rs`),
  typescript: (moduleName) =>
    path.join(root, "packages", "sdkwork-utils-typescript", "src", `${moduleName}.ts`),
  python: (moduleName) =>
    path.join(root, "packages", "sdkwork-utils-python", "sdkwork_utils", `${moduleName}.py`),
  go: (moduleName) =>
    path.join(root, "packages", "sdkwork-utils-go", `${moduleName}.go`),
  java: (moduleName) =>
    path.join(
      root,
      "packages",
      "sdkwork-utils-java",
      "src",
      "main",
      "java",
      "com",
      "sdkwork",
      "utils",
      `${toPascal(moduleName)}Utils.java`,
    ),
  kotlin: (moduleName) =>
    path.join(
      root,
      "packages",
      "sdkwork-utils-kotlin",
      "src",
      "main",
      "kotlin",
      "com",
      "sdkwork",
      "utils",
      `${toPascal(moduleName)}Utils.kt`,
    ),
  csharp: (moduleName) =>
    path.join(root, "packages", "sdkwork-utils-csharp", `${toPascal(moduleName)}Utils.cs`),
  php: (moduleName) =>
    path.join(root, "packages", "sdkwork-utils-php", "src", `${toPascal(moduleName)}Utils.php`),
};

function toPascal(moduleName) {
  return moduleName
    .split("_")
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join("");
}

const modules = Object.keys(contract.modules);
const missing = [];

for (const [language, resolver] of Object.entries(expectations)) {
  for (const moduleName of modules) {
    const filePath = resolver(moduleName);
    if (!existsSync(filePath)) {
      missing.push(`${language}: ${path.relative(root, filePath)}`);
    }
  }
}

if (missing.length > 0) {
  console.error("Missing module files:");
  for (const entry of missing) {
    console.error(`- ${entry}`);
  }
  process.exit(1);
}

console.log(`Module parity check passed for ${modules.length} modules across ${Object.keys(expectations).length} languages.`);
