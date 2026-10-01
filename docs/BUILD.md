# Current gameplay continuation

See QA.md for authorized isolated runServer/runClient commands and final runtime evidence.
The stage-1 results below are preserved as history; current artifact verification uses 39 JVM tests.

# Reproducible local build

Use an existing 64-bit Java 21 JDK; no global Gradle installation is needed.
Wrapper 8.10.2 and direct plugin/API/loader versions are pinned. The official
Gradle distribution checksum is checked automatically. Gradle verification
metadata records dependency checksums. Caches and Mojang binaries/mappings are
excluded from source delivery.

PowerShell, from repository root:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Zulu\zulu-21' # or your existing JDK 21
$env:GRADLE_USER_HOME = Join-Path (Get-Location) '.gradle-user-home'
.\gradlew.bat :common:test :fabric:build :neoforge:build --console=plain
```

Or `./tools/build.ps1 -JavaHome <jdk21-path>`. Other operating systems can set the
equivalent environment variables and use `./gradlew`. No runClient/runServer task
is included. `py -3 tools/port_resources.py` regenerates resources from preserved
upstream assets; generated files are already delivered.

Only plain `*-port-preview.jar` files under each loader's `build/libs/` are mod
artifacts; `dev-shadow` jars are intermediate. Source jars include common sources.
Fabric requires Fabric API and Architectury API; Energy API is nested. NeoForge
requires Architectury API. Do not install both loader artifacts together.

## Evidence

- `docs/evidence/upstream-refs.txt`: original remote branches and tags.
- `gradle-bootstrap.log`: initial platform-selection failure.
- `gradle-bootstrap-2.log`: all three projects configured successfully.
- `core-red.log` / `core-green.log`: initial 7 tests red then green.
- `target-red.log`: 5 target-policy cases fail before implementation; 7 prior pass.
- `build-1.log`: common compiled; test compilation caught a new test added while
  the build was running before its new main class entered that build's snapshot.
- `build-2.log`: both loader builds succeeded; 14 common tests passed.
- `build-final.log`: final wrapper, expanded source jars, network codec tests and
  dependency integrity metadata. Read its actual result, not earlier success.

Known warnings: Loom 1.7 is marked unsupported by its publisher; Gradle/plugin and
Java deprecation warnings remain. One earlier daemon shutdown log reported a
file-contention-handler closure after BUILD SUCCESSFUL. Final exit status and
artifacts are verified separately. No compatibility with Gradle 9 is claimed.

## Runtime acceptance

The formerly blocked game checks were authorized and executed in this continuation.
See QA.md and FEATURE-MATRIX.md for passed cases and remaining gaps.

## Historical stage-1 result (0.1.0)

The final build and a subsequent **offline clean rebuild** both succeeded.
17 tests ran with zero failures/errors: 4 geometry, 3 energy, 5 target-policy,
2 Minecraft item-component and 3 typed-payload tests. Loader test tasks contain
no tests: both loaders were compiled/packaged, not boot-tested.

`build-offline-clean.log` records 26 executed tasks. The two mod jars and two
source jars were byte-identical to the preceding build on this machine, recorded
in `reproducibility.json`. This verifies repeatability in this workspace with
the populated dependency cache; cross-machine byte identity is not claimed.
`artifact-verification.json` contains SHA-256 values and test suite counts.
Copied JUnit XML reports are retained alongside those logs.

## Core gameplay stage (0.2.0)

Java: existing Azul Zulu 21.0.12.1+1-LTS. No Java/Gradle system installation.
Minecraft 1.21.1; Architectury 13.0.8; Fabric Loader 0.16.14 and Fabric API
0.116.6+1.21.1; NeoForge 21.1.209; Fabric Energy API 4.1.0 nested (MIT).

Final release build command (without -Pqa):

```powershell
.\gradlew.bat clean :common:test :fabric:build :neoforge:build --offline --console=plain
py -3 tools/verify_artifacts.py
```

`build-core-gameplay.log` and `build-core-reproducible.log` record two clean
builds. `reproducibility-core.json` compares both loader jars and both source
jars. `artifact-verification.json` identifies the current artifacts and all
20 passing JVM tests (the prior 17 plus three text normalization/bounds tests).
The original stage-1 logs and reproducibility.json are historical evidence.

`runtime/summary.json` records 21 server and 9 client assertions per loader,
with 11 unedited PNGs per loader, zero final assertion failures. Final runtime
runs use the same production Java source; their metadata still says 0.1.0
because the 0.2.0 version bump followed acceptance. QA-only sources/entrypoints
are present solely under -Pqa. Release jars and source jars are inspected to
reject any QA leakage. Development game evidence does not substitute for a
separate production-launcher installation test.

## Storage stage (0.3)

Same pinned official dependencies/toolchain; no additional runtime dependency.
Command: `./gradlew clean build --offline --console=plain` WITHOUT -Pqa, then
`py -3 -X utf8 tools/verify_artifacts.py`. Repeated clean build checks the two
remapped jars and two source jars byte-for-byte on this cached workspace.
Records: build-0.3-first.log, build-0.3-second.log, reproducibility-0.3.json,
artifact-verification.json and junit-0.3/ under docs/evidence.
31 JVM tests are expected. New tests cover measurement conversion/overflow/
variant separation, all upgrade tiers, target array mutations and portable
component persistence. Red-phase evidence is retained separately.

Runtime records under runtime-v3 use0.2.0 metadata because only the version was
bumped after gameplay acceptance. Production Java/resources tested are the
0.3 implementation. Each loader has35 final game assertions across storage,
normal multiplayer and normal disk restart. See QA-V3.md for boundaries.
Final jar verification requires Java21 classes,17 recipes, metadata, textures,
licenses, nested MIT Fabric Energy API and absence of every /qa/ class/source.

## Real IE and display stage (0.4)

Release command: `./gradlew clean build --offline --console=plain` with neither
-Pqa nor -PieQa; then `py -3 -X utf8 tools/verify_artifacts.py`. Repeat clean build
and compare two remapped jars plus two source jars. Records: build-0.4-first.log,
build-0.4-second.log, reproducibility-0.4.json, artifact-verification.json,
junit-0.4/.39 JVM cases comprise31 prior and8 new paging/projection cases.

QA-V4.md describes actual isolated game runs:36 IE assertions and19 display
assertions per loader, including disk restart. Runtime metadata says0.3.0 because
the0.4 version bump follows acceptance. The final production difference from
initial display acceptance is a holographic menu title correction, itself seen
in reload acceptance. New display runs load neither IE nor mock storage providers.
Optional -PieQa requires -Pqa and private exact-hash artifacts; it never affects
release dependency declarations. QA/IE classes and sources must be absent from
both release jars and source jars. No IE jar/source/assets are redistributed.
