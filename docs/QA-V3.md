# 0.3 acceptance record

Date:2026-10-01. Windows/Java21. Existing0.2 instances were not reused; this
stage uses each module's `run/server-v3`, `run/client-v3`, `run/client-v3-observer`.
Servers bind127.0.0.1 only,Fabric25573,NeoForge25574,offline accounts ECEditor
and ECObserver. Only task-owned processes/worlds were started/stopped.

## Reproduction

Use JAVA_HOME and GRADLE_USER_HOME from BUILD.md. Start one loader at a time.
For storage scenarios: `:fabric:runServer -Pqa -PqaStage=storage`, then
`:fabric:runClient -Pqa -PqaStage=storage`; substitute neoforge for Fabric.
Server fixture initializes real platform energy/fluid providers on chest blocks.
The providers are compiled only by -Pqa, live under src/qa, and are not production
fallbacks. Tests read these through exactly the production capability interfaces.

For normal gameplay, retain the stage world but start the server WITHOUT -Pqa:
`:fabric:runServer -PqaStage=normal`. Start the editor with
`:fabric:runClient -Pqa -PqaStage=normal` and the observer with the same command
plus `-PqaObserver`. The client-side QA harness drives actual game interaction,
GUI widgets, network packets and framebuffer screenshot capture. It does not
initialize any server providers. Grant both case-sensitive offline test accounts
op in this isolated server for positioning; no authentication is involved.
After clients finish, issue server `stop`, wait for all dimensions saved, close
the Gradle runtime watcher, restart the same no-QA server, then run one client
with `-Pqa -PqaStage=reload`. Repeat for NeoForge.

QA files are selected only with -Pqa and excluded from release binary/source
jars. No BREAK prevention event remains registered even in the QA source.
Architectury's dev watcher notices common-jar rebuilds during client launches;
server entrypoints are already initialized. Acceptance here is a development
launch with production server initialization, not a standalone installed release
pack. Final remapped jars receive separate content/build verification.

## Results and evidence

| Scenario per loader | Assertions | Evidence directory |
|---|---:|---|
| Storage server including saved target/upgrades/portable reload | 10 | `<loader>-storage-final/server.txt` |
| Storage client binding/shift-click/menu/live data/portable/holo | 11 | `<loader>-storage-final/client.txt` |
| Normal editor simultaneous edit/break/place/portable | 7 | `<loader>-normal-final/editor.log` or `.txt` |
| Normal observer receives menu/world and detach/rebuild | 4 | `<loader>-normal-final/observer.log` or `.txt` |
| Normal server restart and player-item reload | 3 | `<loader>-normal-reload/client.log` or `.txt` |

All evidence directories are under `evidence/runtime-v3`. Screenshots are original
Minecraft captures; no compositing or image edits. Both loaders' portable and
holographic final PNGs were visually inspected. FullmB/capacity/unit strings now
fit the portable panel. The placed-panel edit box no longer overlaps upgrade labels.

## Failures retained, then corrected

1. Initial portable S2C registration tried to register a client receiver on the
   dedicated Fabric server, causing AbstractMethodError. Register the payload
   codec on dedicated servers and the receiver on clients; both servers rerun.
2. Portable value strings were truncated at fixed width; fit by actual font
   width and line count. Panel editor overlapped upgrade labels; reduce its height.
3. First normal QA pass used a plain right-click for placing on a panel, opening
   its menu. Corrected QA to actual sneak-place. Observer permission was initially
   granted to a differently cased offline name; fixed exact names and both-player
   readiness. Production interaction was not weakened to make tests pass.

The before-fix crash/layout and failed normal run remain in evidence. No normal
server has QA PASS records; its Minecraft log proves boot/join/save. NeoForge QA
stdout is also captured separately in qa-results text files.

## Limits

Creative normal breaking/placement is accepted; survival drops/tool-speed are
not separately accepted. Each-target GUI toggle, malformed-client fuzzing,
chunk-boundary stress, long uptime, large walls, offhand portable use and
simultaneous conflicting edits remain untested. External IE/other machines
are not accepted by self-compiled fixtures. IE runtime approval is still pending.
No complete upstream parity or old-world migration is claimed.
