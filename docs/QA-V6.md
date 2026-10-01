#0.6 step2 verification (active finalization)

Inventory, kits and holder:117 successful assertion executions across five accepted
runs,31 original PNGs, seven successful client exits. See QA-INVENTORY.md for exact
case scope and the retained failed attempts/fixes. No energy/fluid fixture exists
in production sources.

Solid slopes: Fabric and NeoForge each14 server+12 editor+6 observer assertions,
12 original PNGs. Both clients use normal GUI operations, real block break/place,
and actual world-time text. Six2x2 groups cover all mounting faces. Edits set main
group to8/16 thickness, H8/V4; reference groups use8/16,H4/V-4. Adjacent parts have
identical depths at shared edges. Core/extender geometry is saved independently,
so unloaded owners do not need forced chunks.

Each loader's saved world then runs on a normal dedicated server WITHOUT -Pqa.
Before launching QA-only clients, all three modules' compiled classes and loader
metadata were verified free of QA entrypoints/classes (slopes-normal-server-entrypoints.json).
Each reconnecting client passes4 checks: saved settings, continuous part geometry,
sloped collision and resumed real time updates. One original PNG each. The NeoForge
world-metadata warning energycontrolqa1.0->MISSING confirms removal of the former
test mod; no server QA callback runs. These runs have no new server QA assertions.
Normal stop saves every dimension. Architectury's development file watcher can
remain after Minecraft exits; ending that watcher is not a failed game shutdown.

Geometry uses an exact rendered plane and conservative voxel collision/selection:
16 strips per active axis, at most256 boxes per part, cached until surface changes.
The selected outline shows subdivision lines. Maximum excess depth is bounded by
(abs(dx)+abs(dy))/16 <=1/16 block. Tests cover all17x17 controls and16 thicknesses,
flat-case equivalence and all six coordinate bases. All-case actual large-group
performance is not claimed.

65 JVM assertions/tests passed in slope-first-build.log (19 suites). The final
production clean build and artifact hash checks will be recorded before delivery.
The first64 slope runtime checks and8 normal-server reload checks add72 to the
117 inventory executions:189 total before the final real IE kit regression.

Scope change: user requested completion of this step2 then Modrinth publication;
other original features remain explicit beta limitations. See FEATURE-MATRIX.md,
UPSTREAM-FEATURE-MATRIX.md and MODRINTH-PUBLICATION.md. No public upload occurred.

Two final IE replay attempts were rejected and kept separately: an old saved
circuit retained connection/storage state and failed sustained positive power;
a fresh two-client replay then failed client-local-tick menu/stop ordering under
load, while server positive-power and real adapter equality checks had passed.
Neither is counted as accepted. The new kit-only scenario waits for actual server
inventory/card responses, uses one client, and does not claim to replace the prior
0.5 successful two-client stop/resume/reload coverage. The older timed IE replay
still has a synchronization limitation; its failures are disclosed rather than
removed from the archive or relabeled as successful production regression.

The first kit-only attempt timed out before interacting because Minecraft centers
integer teleport coordinates at+.5. Changed only the QA command to explicit decimal
coordinates and kept the failed log. New kit acceptance uses ie-special-kit-muted
with run/server-ie-kit-muted and run/client-ie-kit-muted, fresh task-owned world.
All task client options and QA callbacks now set master sound volume0 at startup,
after the user reported audible test sound. This setting is excluded from release.

Final muted IE kit scenario accepted8 server+4 client checks and one original PNG.
It proves real survival kit consumption, returned bound target/face, no accidental
machine GUI, and the new card's value equals the actual master meter. The real
copper circuit reports positive power and both machine adapters match real data.
The excluded loading-screen attempt had a mislabeled timing diagnostic; the final accepted run uses the corrected label and its raw log records actual event order.

Accepted total:201 assertion executions,58 original1280x720 PNGs across10 runs;
14 client Gradle exit0 results,10 servers with all dimensions saved. Seven excluded
attempt folders remain separate. Inventory and slope acceptance is both-loader;
actual IE is NeoForge only. Final clean build verification remains separate.

A technically passing kit run captured the loading screen; it is retained under
neoforge-ie-kit-loading-screen but excluded from final acceptance. The final QA
waits for Minecraft's normal game view before interacting and delays the screenshot
after the returned card arrives. Reproduce using -Pqa -PieQa
-PqaStage=ie-special-kit-final in its own fresh run directories (master volume0).

Final verification: both clean offline production builds completed successfully;
all65 JVM tests passed and both remapped jars plus both source jars were byte-identical.
The artifact audit confirms25 recipes, GPL/change notices, Java21 classes, required
assets/entrypoints and no QA/IE/Minecraft class leakage. SHA256 values are in
artifact-verification.json and reproducibility-0.6.json. All task game processes
were verified absent after normal accepted-server shutdown; user games untouched.
