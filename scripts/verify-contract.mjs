import { readFileSync } from "node:fs";
import { fileURLToPath } from "node:url";
import path from "node:path";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const contract = JSON.parse(
  readFileSync(path.join(root, "specs", "utils.contract.json"), "utf8"),
);
const fixtures = JSON.parse(
  readFileSync(path.join(root, "specs", "conformance", "fixtures.json"), "utf8"),
);

const requiredModules = Object.keys(contract.modules);
const fixtureModules = Object.keys(fixtures).filter((key) => key !== "version");
const unknownModules = fixtureModules.filter((moduleName) => !requiredModules.includes(moduleName));
const uncoveredModules = requiredModules.filter((moduleName) => !fixtureModules.includes(moduleName));

if (unknownModules.length > 0) {
  console.error("Unknown fixture modules:", unknownModules.join(", "));
  process.exit(1);
}

if (uncoveredModules.length > 0) {
  console.error("Modules without conformance fixtures:", uncoveredModules.join(", "));
  process.exit(1);
}

const missingOperations = [];
for (const [moduleName, operations] of Object.entries(contract.modules)) {
  const moduleFixtures = fixtures[moduleName] ?? {};
  for (const operationName of Object.keys(operations)) {
    if (!(operationName in moduleFixtures)) {
      missingOperations.push(`${moduleName}.${operationName}`);
    }
  }
}

if (missingOperations.length > 0) {
  console.error("Contract operations missing fixture keys:");
  for (const entry of missingOperations) {
    console.error(`- ${entry}`);
  }
  process.exit(1);
}

if (fixtures.version !== contract.version) {
  console.warn(
    `Fixture version (${fixtures.version}) differs from contract version (${contract.version}).`,
  );
}

const operationCount = Object.values(contract.modules).reduce(
  (total, module) => total + Object.keys(module).length,
  0,
);

console.log(`Contract modules: ${requiredModules.length}`);
console.log(`Contract operations: ${operationCount}`);
console.log(`Fixture modules: ${fixtureModules.length}`);
console.log("Conformance fixture structure check passed.");
