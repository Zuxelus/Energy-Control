# Stage status — incomplete port, not a usable release

This preview is a real but limited vertical slice, not feature parity with
Energy Control 1.20.1 or 1.12.2. The untouched 200-file 1.20.1 source baseline
remains in `src/` for further migration; it is not silently included in jars.

## Implemented in the new source sets

- Common Architectury registration of basic/advanced panels and both extenders,
  preserving original registry IDs and reusing upstream models/textures.
- One basic / eight advanced card slots, shift-click transfer, persistence using
  1.21.1 item components, menu open and inventory synchronization.
- Text, world-time, bound energy and redstone cards. Sneak-use binds a target
  dimension, position and face. Energy lookup is read-only and range-limited.
- Text editor with selected slot, save, three text colors and display on/off.
  This is a simplified interface, not the upstream full configuration GUI.
- Common rectangular screen growth, limited to 20 extenders per direction,
  same facing and tier, exclusive ownership, periodic rebuild and no forced
  target chunk loading. Rebuild runs every 20 ticks.
- Block entity update packets for display lines and geometry; renderer spans
  the group rectangle on all six faces, with text fitted to available area.
- Typed, bounded text payload; server requires the sender's currently open
  menu, matching container ID, valid distance and non-spectator player.
- Separate NeoForge FE and Fabric Team Reborn Energy readers.
- Eight vanilla-material preview recipes, loot tables, English/Chinese UI.

## Required work that remains

- **Runtime validation:** neither loader has been started. Visual correctness,
  menu interaction, dedicated server class isolation, multi-player synchronization,
  chunk save/reload and real provider compatibility remain unverified.
- Advanced panel page/layout settings, labels, bars, alignment, slopes, upgrades,
  range upgrades, touch actions and the complete upstream GUI are not ported.
- Holographic and portable panels, card holder, arrays, fluid/inventory cards,
  kit assembler, alarms, thermal monitors, timers, counters and websocket/web
  integration are not ported. No placeholder registry entries claim otherwise.
- Specialized mod integrations are listed separately in COMPATIBILITY.md.
- Original recipe balance, legacy card/world migration and redstone-controlled
  panel power semantics are not preserved by this preview. Use only a future
  disposable test world; do not load an existing Energy Control world with it.
- The GUI currently edits one line at a time. Stored card data and renderer can
  carry line breaks; a full multiline editor remains to be migrated.
- Screen rebuild performance/overlap arbitration on large walls needs runtime
  testing. Client block-entity availability during menu-open is also a test gate.
- No Maven/CurseForge/GitHub publication or public fork was created.

## Verification boundary

Compile success is distinct from a working mod. The current user instruction
forbids starting/stopping games or touching existing instances; only compilation,
pure JVM tests, vanilla-registry/component tests and artifact inspection are run.
This is the remaining gate before any claim of usability, along with missing
feature work above. See BUILD.md and evidence logs for exact commands/results.

Verified stage result: both loader builds pass; 17 JVM tests pass; both mod jars
and source jars pass structural/resource/license checks and are byte-identical
after an offline clean rebuild in this workspace. No gameplay validation occurred.
