import { readFileSync, existsSync } from "node:fs";
import { spawnSync } from "node:child_process";
import { fileURLToPath } from "node:url";
import path from "node:path";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const packageRoot = path.join(root, "packages", "sdkwork-utils-typescript");
const distIndex = path.join(packageRoot, "dist", "index.js");

if (!existsSync(distIndex)) {
  console.log("Building @sdkwork/utils before export parity check...");
  const build = spawnSync("pnpm", ["--dir", packageRoot, "build"], {
    cwd: root,
    stdio: "inherit",
    shell: true,
  });
  if (build.status !== 0) {
    process.exit(build.status ?? 1);
  }
}
const contract = JSON.parse(
  readFileSync(path.join(root, "specs", "utils.contract.json"), "utf8"),
);
const packageJsonPath = path.join(
  root,
  "packages",
  "sdkwork-utils-typescript",
  "package.json",
);
const packageJson = JSON.parse(readFileSync(packageJsonPath, "utf8"));
const exportsField = packageJson.exports ?? {};
const expectedModules = Object.keys(contract.modules);
const missingExports = [];
const missingDistFiles = [];

for (const moduleName of expectedModules) {
  const exportKey = `./${moduleName}`;
  const exportEntry = exportsField[exportKey];
  if (!exportEntry || typeof exportEntry !== "object") {
    missingExports.push(exportKey);
    continue;
  }

  for (const field of ["types", "import"]) {
    const relativePath = exportEntry[field];
    if (typeof relativePath !== "string") {
      missingExports.push(`${exportKey} (${field})`);
      continue;
    }
    const absolutePath = path.join(
      root,
      "packages",
      "sdkwork-utils-typescript",
      relativePath,
    );
    if (!existsSync(absolutePath)) {
      missingDistFiles.push(path.relative(root, absolutePath));
    }
  }
}

if (packageJson.name !== "@sdkwork/utils") {
  console.error(`Expected package name @sdkwork/utils, received ${packageJson.name}`);
  process.exit(1);
}

if (missingExports.length > 0) {
  console.error("Missing @sdkwork/utils package.json exports:");
  for (const entry of missingExports) {
    console.error(`- ${entry}`);
  }
  process.exit(1);
}

if (missingDistFiles.length > 0) {
  console.error("Missing dist artifacts referenced by @sdkwork/utils exports:");
  for (const entry of missingDistFiles) {
    console.error(`- ${entry}`);
  }
  console.error("Run pnpm --dir packages/sdkwork-utils-typescript build before verify.");
  process.exit(1);
}

console.log(
  `@sdkwork/utils export parity check passed for ${expectedModules.length} contract modules.`,
);
