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

## Runtime checklist — not executed

After authorization to launch an isolated disposable instance, for each loader:
place all panels in six orientations; insert/remove/shift-click all cards; edit
text; bind a real energy provider; test range, dimension and chunk boundaries;
add/remove extenders; save/reload; join a second client; test dedicated-server
startup. Check front-face/depth behavior, scaling, panel overlap and menu-open
timing. Record exact dependencies, logs and screenshots before claiming usable.
Existing user instances must remain untouched.

## Final stage result

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
