# Isolated gameplay acceptance

The user authorized task-owned development clients/servers and accepted the
official Minecraft EULA for this task. No existing game instance, save, launcher
profile, account, or unrelated process was accessed or changed.

`-Pqa` adds `src/qa/java` to the development build and its loader entrypoints.
Without that property, QA code and metadata are excluded. `verify_artifacts.py`
rejects any leaked QA classes/entrypoints in the deliverable jars.

The QA chest is a self-compiled fixture exposing a changing value through the
actual Fabric EnergyStorage lookup / NeoForge block energy capability. The port's
real adapter reads it. This validates platform protocols; it does NOT establish
compatibility with Tech Reborn, Mekanism, or any uninstalled machine mod.

## Repeat the scenarios

Use Java 21. Start the server, then the client in a second terminal:

```powershell
.\tools\qa.ps1 -Loader fabric -Role server -JavaHome 'C:\Program Files\Zulu\zulu-21' -AcceptMinecraftEula
.\tools\qa.ps1 -Loader fabric -Role client -JavaHome 'C:\Program Files\Zulu\zulu-21'
```

Replace `fabric` with `neoforge` for the second loader. Subsequent cached runs
can add `-Offline`. EULA acceptance is required for a new recipient's server;
do not infer their consent from this task. Server addresses are loopback-only
127.0.0.1:25571 / :25572, offline dev identities, no credentials or remote players.
If a prior manual intervention broke the fixture, `-ResetFixture` rebuilds only
the QA 3x2 screen in this disposable world. It does not reset unrelated files.

The task uses `fabric/run/{client,server}` and `neoforge/run/{client,server}`.
Never point the harness at an existing world. It writes fixtures, teleports its
test player, records assertions and exits the client at the end of the run.
During automated QA, player attempts to break panels are rejected by QA-only
code. Release gameplay keeps ordinary creative/survival mining behavior.
Right-click a panel or its extender to configure it. Sneak-right-click a target
while holding an energy/redstone card to bind it.

## Evidence and limits

`docs/evidence/runtime/<loader>-final/` contains the final client/server results,
game logs and unedited framebuffer PNGs (Minecraft Screenshot.grab). Screenshots
are real Minecraft frames, not mockups or drawings. The harness invokes actual
client interaction, GUI Save button, menu actions and network packets. Six basic
2x2 groups and an advanced 3x2 wall exercise geometry and live rendering.

Server cases: grouping, extender ownership, removal/rebuild, real energy API
updates, redstone/inverted/on/off power, reload of cards/geometry/style/fields.
Client cases: sneak-use binding, initial/repeated data updates, real menu open,
multiline Save button, display settings, sensor title/percentage mask, rejection
of a stale menu packet, screenshots of all six directions.

`*-before-*` evidence preserves failures that caused real fixes: invisible text,
NeoForge's late menu registration, floor/ceiling orientation. World text now uses vanilla-style polygon-offset rendering.
A later suspected missing-glyph issue was resolved as test-world dropped-item
occlusion / scaled-preview readability: clean original PNGs contain all glyphs.
`neoforge-interrupted` records a user-reported creative-mode panel break during
the automated scenario; the fixture was restored and protected before rerunning.
The harness deliberately aborts a run when an assertion fails. An aborted run
is never counted as passing. Prior runs can coexist in raw qa-results.txt;
the final extracts begin at the most recent RUN_START marker.

This validates development runs with the final source, not a separate production
launcher installation. Concurrent two-player editing, large-wall stress tests,
cross-dimensional machines and third-party content-mod interoperability are not
game-tested. Range/chunk/dimension validation has JVM tests. No legacy world
migration is provided. This remains an incomplete port preview.
