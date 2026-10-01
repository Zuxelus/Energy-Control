# 0.5 bars, physical case and real IE power acceptance

This is a continuation of77715a5, not a full upstream replacement. All games
belong to the independent task. Existing user instances/worlds are untouched.
Only already authorized dependencies and exact IE12.4.2-194 with its embedded
BlockModelSplitter2.0.1/DualCodecs0.1.2 are used. No new external mod or protocol.

## Production implementation

Cards carry a saved showBars option. Server measurements produce typed rows with
text and a bounded0..10000 fraction. Text cannot impersonate a numeric bar.
Energy/fluid totals and optional each-target rows have horizontal bars, preserving
fluid variants and unit distinctions. Zero/negative capacities and overfills are
bounded; BigDecimal avoids overflow. World and portable screens render these rows.
Portable payload changes require matching0.5 clients and servers. No fixture
values or test providers appear in production code or jars.

Advanced solid panels and matching extenders use block-state thickness1..16,
anchored at the mounting back. Generated models retain original GPL textures;
selection/collision and text front plane use the same depth and all six facings.
Normal placement inherits the owner thickness during grouping. Basic panels keep
full size; holograms retain their independent projection controls. Solid slopes,
rotation and upstream hologram circuits remain pending.

The new Machine Data Card uses the common read-only MachineProbe. NeoForge's
optional adapter reads the actual IE meter public getAveragePower method (up to
20 samples) and the exact12.4 thermoelectric generator saved enegyOutput field.
The latter is labeled generation potential, not delivered output. No IE classes
are linked/bundled; class matching/reflection is limited to that public meter
method, with a visible unavailable result if its contract changes. Unsupported
blocks/platforms explicitly report no supported machine data. Fabric does not
claim IE or another external industrial mod compatibility.

## Shared graphics and multiplayer scenario

`-Pqa -PqaStage=graphics`, editor plus `-PqaObserver`, then `graphics-reload`
after a normal stop/save. Fabric loopback25579 and NeoForge25580. Run folders
are loader/run/server-graphics-v5 and client-graphics-v5[-observer]. Fresh task
worlds contain QA-only dynamic energy/fluid chest capabilities; these validate
real platform lookup and network paths, not external-machine compatibility.

Two simultaneous actual clients open normal menus. One changes two bar settings
and8/16 thickness; both verify synchronized row fractions and core/extender states.
Real client creative breaking removes an extender; normal sneak placement rebuilds
it and inherits8/16 thickness. No break protection exists. Portable bar UI and
packets are exercised. Six oblique camera views show4/16 cases and matching
collisions. Reload verifies saved cards, state and portable component data.

A real screenshot exposed coplanar bar fill/background z-fighting in the first
Fabric run, although numerical/network assertions passed. Separate render depths
fix it; pre-fix screenshots/logs remain under fabric-graphics-before-bar-depth-fix.
Only final runs are counted in summary.json. Two waiting clients exited normally before assertions without a crash reason.
These interrupted pairing attempts are not counted. Subsequent QA clients hide
only their own GLFW window and still capture real rendered framebuffer images;
this test-only behavior is excluded from production jars. The remaining waiting
observer was stopped by exact task process arguments after a clean server save.

## Actual IE specialized data

`-Pqa -PieQa -PqaStage=ie-special`, two clients, then ie-special-reload. Loopback
25581; dedicated server/client-ie-special-v5[-observer]. No mock chest capabilities
are registered in IE scenarios. The actual world includes two LV connectors,
copper wires, a current transformer and a real LV capacitor. Wire connections are
created via IE's public network API; energy enters through the actual FE capability.
The IE meter calculates its own samples: the harness never edits lastPackets.
Input is stopped and resumed. A real thermoelectric generator has blue ice and
contained lava; removing/replacing the heat source changes its potential.

The harness compares card output with the actual meter and saved generator field.
Both clients observe positive/zero/resumed power, toggle a real capacitor bar and
physical thickness through the menu. Actual sneak-use binds the machine card to
the meter's upper dummy. Clean restart checks real copper-network persistence and
resumed measurements; these are not test constants or simulated machine objects.
Final counts and original screenshot hashes are in runtime-v5/summary.json.

## Boundaries

Creative interaction only; no survival drop/durability acceptance. No claim of all
IE machines, wires, voltage tiers, loss details, directional flow or recipes.
No arbitrary field layout, vertical/multi-color/threshold bar styles, solid slope
geometry, full localization or original kits are implemented by this stage.
Historical0.2-0.4 evidence is retained separately and is not counted again.
JVM tests total49 (39 previous,10 numeric row/packet/case rules). Formal release
builds exclude -Pqa/-PieQa and are inspected for QA/IE leakage.

## Final accepted results

| Actual scenario | Server | Editor/client | Observer | Original PNG |
|---|---:|---:|---:|---:|
| Fabric graphics | 9 | 14 | 6 | 13 |
| Fabric disk reload | 5 | 3 | - | 2 |
| NeoForge graphics | 9 | 14 | 6 | 13 |
| NeoForge disk reload | 5 | 3 | - | 2 |
| NeoForge real IE power | 17 | 7 | 5 | 9 |
| NeoForge IE disk reload | 6 | 1 | - | 1 |

110 actual assertions, 40 original PNGs. Nine clients exited normally (Gradle0).
All six accepted servers saved every dimension before watcher shutdown. Current
runs used development 0.4 metadata; final artifacts are versioned 0.5 after
acceptance. Matching new portable protocol is required on both ends.

IE's first harness assertion incorrectly treated getConnections(BlockPos) as all
meter points. Official source shows it means point0 only. The corrected assertion
unions points0/1 and requires two external wires and one internal shunt. Original
failure is retained and excluded. Reusing that saved circuit logs duplicate wire
addition in the second run; actual topology, FE transfer, stop/resume and restart
all passed. No meter samples or production measurements were overwritten.
Array and holographic bar-specific visuals were not separately game-accepted.
