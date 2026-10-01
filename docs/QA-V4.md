# 0.4 real-machine and display acceptance

All worlds/processes belong to this project. No existing user game was changed
or started/stopped. Minecraft EULA and the exact IE runtime scope were already
explicitly accepted. Screenshots are unmodified Minecraft framebuffer captures.

## Real Immersive Engineering — NeoForge only

Official12.4.2-194 jar,14,180,259 bytes, SHA256
816685b898eed52080d08a4009042c511560ecc185601f21fe744936b63b5127.
Source and embedded-library hashes: integration-research/IE-artifact.json.
Only approved BlockModelSplitter2.0.1 and DualCodecs0.1.2 accompany it.
No other content mod, JEI, third-party login or new protocol was introduced.
No IE package/assets/implementation source is redistributed. The optional QA
classes call the installed public APIs, not copied implementation or mock data.

Commands: :neoforge:runServer / :neoforge:runClient with
`-Pqa -PieQa -PqaStage=ie --offline --console=plain`. The new server directory
is neoforge/run/server-ie-v4, client directory client-ie-v4, loopback port25576.
The build rejects altered hashes. Both nested libraries are extracted verbatim
from the approved jar for the Loom dev classpath; normal release configurations
never resolve any of these optional dependencies.

The first startup exposed a missing DualCodecs class: Loom's development launch
did not discover the embedded library. The fix explicitly places both approved
embedded jars on that optional classpath and records their transformed checksums.
Dependency verification remains enabled. Original failure evidence is preserved
under runtime-v4/ie-before-embedded-fix.

The server places actual capacitor_lv blocks and fills their actual FE storage
through receiveEnergy. Three actual sheetmetal tanks are built from IE's template
and formed by IE's normal recognition API; their real FluidHandler receives water.
These are automatically assembled real machines, not manually hammered structures
or fake providers. No chest capabilities are registered in the IE scenario.

| Real IE checks | Results |
|---|---:|
| Server: actual entities,512000mB tanks,non-port rejection,card/API equality,arrays,capacitor break/place,tank disassembly/reform,chunk unload/reload | 24 PASS |
| Client: actual changing reads,sneak binding,menu,normal capacitor break/place,restored array | 7 PASS |
| Clean server restart: actual machine amounts,remote contents,bindings/upgrades | 4 PASS |
| Reconnected client: persisted arrays and real tank readout | 1 PASS |

After removing the remote chunk ticket, the harness waited until hasChunkAt was
false. Both cards reported unloaded, and evaluating them did not reload the chunk.
An explicit harness reload then recovered the real machine contents. The server
was stopped normally and all dimensions saved; `ie-reload` checks recorded amounts
without replacing/reinitializing machines. This is actual disk persistence.

Final evidence: runtime-v4/neoforge-ie-first-pass and neoforge-ie-reload,36 assertions,
5 PNGs. Tests cover LV capacitor and formed sheetmetal tank exposed storage only.
MV/HV variants, wires/network flow, every other IE machine, fluid mixes and
survival dismantling have not been accepted. This is NOT Fabric IE compatibility.

## Shared paging and holographic projection

Commands use -Pqa -PqaStage=pages, then pages-reload after a clean server stop.
Both loaders use run/server-pages-v4 and run/client-pages-v4; Fabric25577 and
NeoForge25578, bound to127.0.0.1. No IE or mock storage providers are loaded in
these display scenes. Four text cards supply40 lines, including lines past the
old32-line truncation. The actual GUI drives Next, page-size, auto-cycle and
hologram projection controls over real menu packets.

Page sizes4/8/16/32; server auto-cycle off/40/100/200ticks. At most256 source
lines are retained for paging, with an explicit omitted-lines message at the
limit. Clients receive only their current bounded page plus page metadata.
Depth1..16 means the projection plane position in sixteenths from the back of the
block; default16 preserves the previous front position. Pitch/yaw each range
-56..56degrees. Rotations are about the center of the entire screen group.
These control visual holographic projection, not a resized solid case/collision.
Original advanced physical slopes/thickness and holo circuit height remain pending.

Passed per loader:2 setup assertions,14 client assertions including six facings,
2 server restart assertions and1 reconnect assertion;9 initial+2 reload PNGs.
Final status and original screenshot hashes are in runtime-v4/summary.json:74
new actual game assertions, zero failures,27 original PNGs across the six runs. Existing0.3 normal multiplayer/break and0.2 core evidence is
retained as historical acceptance. New paging collision/conflicting edits,
large-wall stress and long uptime are not separately accepted.

JVM rules cover lossless page traversal, stale/empty pages, auto-cycle/manual mode,
page bounds, old-save projection defaults, NBT roundtrip,malformed limits and
full control cycles. Final release builds must exclude QA and all IE library
classes. GPL history and original source remain intact.

## Retained log diagnostics

Initial flat-world creation logs `No key layers in MapLike[{}]` from the empty
QA generator settings; worlds loaded with the default flat generator and all
assertions completed. IE client logs a missing `immersiveengineering.alert`
subtitle translation. QA camera teleports can emit moved-too-quickly warnings.
These are retained verbatim, not reported as zero-error logs. No final run has
an assertion failure or fatal startup failure. The earlier missing DualCodecs
startup failure is separately preserved and fixed by the approved classpath fix.
All six clients exited normally (Gradle exit0). Each server recorded all
 dimensions saved before its lingering Gradle development watcher was stopped.
