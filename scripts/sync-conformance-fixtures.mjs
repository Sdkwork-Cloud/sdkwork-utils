import { copyFileSync, mkdirSync } from "node:fs";
import { fileURLToPath } from "node:url";
import path from "node:path";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const source = path.join(root, "specs", "conformance", "fixtures.json");
const targets = [
  path.join(
    root,
    "packages",
    "sdkwork-utils-java",
    "src",
    "test",
    "resources",
    "conformance",
    "fixtures.json",
  ),
];

for (const target of targets) {
  mkdirSync(path.dirname(target), { recursive: true });
  copyFileSync(source, target);
}

console.log(`Synced conformance fixtures to ${targets.length} test resource location(s).`);
