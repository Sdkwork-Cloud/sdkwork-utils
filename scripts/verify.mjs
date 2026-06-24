import { spawnSync } from "node:child_process";
import { existsSync } from "node:fs";
import { fileURLToPath } from "node:url";
import path from "node:path";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");

function run(label, command, args, cwd = root, env = {}) {
  console.log(`\n==> ${label}`);
  const result = spawnSync(command, args, {
    cwd,
    stdio: "inherit",
    shell: true,
    env: { ...process.env, ...env },
  });
  if (result.status !== 0) {
    process.exit(result.status ?? 1);
  }
}

function isAvailable(command, args) {
  const result = spawnSync(command, args, { shell: true, stdio: "ignore" });
  return result.status === 0;
}

run("Rust", "cargo", ["test", "--workspace"]);
run("TypeScript", "pnpm", ["--dir", "packages/sdkwork-utils-typescript", "test"], root);
run(
  "Python",
  "python",
  ["-m", "pytest", "packages/sdkwork-utils-python/tests", "-q"],
  root,
  { PYTHONPATH: path.join(root, "packages", "sdkwork-utils-python") },
);

if (isAvailable("go", ["version"])) {
  run("Go", "go", ["test", "./..."], path.join(root, "packages", "sdkwork-utils-go"));
} else {
  console.log("\n==> Go\nSkipped: go toolchain not found on PATH.");
}

if (isAvailable("mvn", ["-v"])) {
  run("Java", "mvn", ["-q", "test"], path.join(root, "packages", "sdkwork-utils-java"));
} else {
  console.log("\n==> Java\nSkipped: mvn not found on PATH.");
}

const kotlinDir = path.join(root, "packages", "sdkwork-utils-kotlin");
const gradlew = process.platform === "win32" ? "gradlew.bat" : "gradlew";
if (existsSync(path.join(kotlinDir, gradlew))) {
  run("Kotlin", path.join(kotlinDir, gradlew), ["test", "--no-daemon"], kotlinDir);
} else if (isAvailable("gradle", ["-v"])) {
  run("Kotlin", "gradle", ["test", "--no-daemon"], kotlinDir);
} else {
  console.log("\n==> Kotlin\nSkipped: Gradle wrapper or gradle not found.");
}

const csharpDir = path.join(root, "packages", "sdkwork-utils-csharp");
if (isAvailable("dotnet", ["--version"])) {
  run(
    "C#",
    "dotnet",
    ["test", path.join("Sdkwork.Utils.Tests", "Sdkwork.Utils.Tests.csproj")],
    csharpDir,
  );
} else {
  console.log("\n==> C#\nSkipped: dotnet not found on PATH.");
}

const phpDir = path.join(root, "packages", "sdkwork-utils-php");
if (isAvailable("composer", ["--version"])) {
  run("PHP", "composer", ["test"], phpDir);
} else if (isAvailable("php", ["-v"])) {
  run("PHP", "php", ["vendor/bin/phpunit"], phpDir);
} else {
  console.log("\n==> PHP\nSkipped: composer/php not found on PATH.");
}

console.log("\nAll sdkwork-utils verification checks passed.");
