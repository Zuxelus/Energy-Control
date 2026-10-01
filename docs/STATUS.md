# Storage and portable-screen milestone — incomplete0.3 port

This independent GPLv3 port continues the full-history branch from263f6ae.
Original200 Java sources, README files and LICENSE remain intact outside active
source sets. This is not a complete upstream replacement. See the source-by-source
UPSTREAM-FEATURE-MATRIX.md and acceptance FEATURE-MATRIX.md.

Implemented in common/fabric/neoforge: basic/advanced matching panel groups;
six facings; live glyph rendering; card components and GUI editing; font/scale/
alignment/color/background/refresh/power settings; real per-face E/FE and fluid
interfaces; fluid/energy arrays; range/capacity/precision upgrades; portable
screen inventory/menu/live packets; transparent grouped holographic screens.

Basic panels have one card; advanced/holographic panels have eight. All have
three separate upgrade slots. These slot counts are this port's design and do
not imply compatibility with legacy inventories. Arrays retain at most16 unique
positions and operate4/8/12/16 according to capacity. Range is64/128/256/512.
Precision is0..3 decimals. Fabric fluids convert native units to mB without
integer loss; aggregates use BigDecimal, preserve variants and do not mix units.
No target chunk is forced or cross-dimension target read performed.

Normal right-click opens a panel; sneak-place builds against existing panels.
Creative left-click breaks normally. The former QA-only prevention hook has
been removed. Normal-server initialization without -Pqa, two simultaneous
clients, actual editing/detach/rebuild and save/restart are accepted on both
loaders. Gameplay automation remains self-compiled client QA; it is absent from
normal release jars. No user launcher/game/world was modified or stopped.

Remaining work, by value:
1. Real modern machine acceptance: Immersive Engineering is a concrete1.21.1
   NeoForge target. Generic FE/FluidHandler source exists; external runtime
   approval was requested but has not been granted. No IE code was executed or
   redistributed. Other modern targets require exact platform/version checks.
2. Full advanced per-field layout, bars, paging and advanced fluid selection;
   inventory cards; original color/touch upgrades and user-facing localization.
3. Projection geometry, slopes/thickness/rotation beyond six block facings.
4. Card holder, kits/assembler and original recipe balance; alarms/monitors,
   timers/triggers/counters/lamps; machine/reactor/network specializations.
5. Authorized touch/control actions, web upgrade/protocol, optional overlays,
   legacy world/card migration. These are not placeholder registrations.

Seventeen simplified recipes are present. Full original feature/recipe parity,
survival drop acceptance, large-wall/load stress, offhand portable acceptance,
conflicting simultaneous edits and external-machine compatibility are not claimed.
Detailed real-game evidence and failures are in QA-V3.md and QA.md. Old APIs are
marked unavailable only where COMPATIBILITY.md has an official release check.

Known toolchain warnings: pinned Architectury Loom1.7 unsupported notice,
Gradle9 deprecations, NeoForge deprecated event-bus annotation. Builds retain
the previously verified official dependencies; no new content mod was installed.
