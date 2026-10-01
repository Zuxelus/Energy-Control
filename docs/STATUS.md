# Core gameplay milestone; incomplete port preview

This is an independent GPLv3 migration, not an upstream release or complete
Energy Control 1.12.2/1.20 feature replacement. The original 200 Java files in
src/ are retained for further migration and are outside active source sets.
The detailed, prioritized inventory is FEATURE-MATRIX.md; real game evidence
and its limitations are documented in QA.md.

Implemented in common/fabric/neoforge:

- Basic and advanced panels, both matching extender tiers, rectangular ownership
  up to 20 parts in each direction, six block facings, removal/rebuild.
- One basic / eight preview advanced card slots, inventory transfer, component
  persistence, menu validation and block entity update packets. Eight advanced
  slots are a preview design: upstream's three cards plus upgrades are not yet
  restored. Do not assume legacy inventory or world compatibility.
- Ten-line text card with bounded text, original @ formatting escape convention,
  real multiline editor and Save button. Text can include Unicode. Current new
  settings buttons are English; original registered item/block names have Chinese.
- Energy, world-time and redstone cards; sensor target dimension, position and
  face bound with sneak-use. Energy reads actual per-loader E/FE capabilities.
  Target range is 64 blocks without forced target chunk loading.
- Per-card sensor titles, label visibility and energy percentage visibility.
- Panel font (default/uniform), 50-200% scale with fitting, left/center/right
  alignment, three text colors, two backgrounds, refresh interval (5/10/20/40
  ticks), redstone/inverted/always-on/off power. Settings persist and synchronize.
- World renderer uses proper front-facing text, six-face geometry and the same
  polygon-offset text mode used by vanilla signs. Full-cube models are retained;
  upstream slopes, thickness and arbitrary rotation controls are NOT implemented.
- NeoForge registers menu/renderers at their dedicated client events. All common
  state/menus/networking are shared; only loader adapters and event hooks differ.
- Eight simplified preview recipes, loot tables and original GPL assets.

Not implemented: upstream advanced pages/layout, bars, upgrades, range upgrades,
fluid/inventory/array cards, holographic or portable panels, card holder, kit
assembler, alarms, thermal/remote monitors, timers/counters, lamps, touch actions,
websocket/web upgrade, specialized machine/reactor/computer/overlay integrations,
legacy data migration and upstream recipe balance. No placeholder registrations
pretend these features exist. COMPATIBILITY.md distinguishes missing original
projects from available-but-unported integrations.

Verification boundaries:

- Gameplay runs are isolated development builds with self-compiled QA fixtures.
  They validate actual Minecraft clients and dedicated servers, real network
  messages, menus, rendering and loader energy APIs. No extra content mod is used.
- The fixture is not evidence for compatibility with a specific external machine.
- Concurrent two-player editing, large wall performance and world upgrades are
  not game-tested. Target boundary cases have JVM tests.
- Release jars exclude QA entrypoints/classes and are separately built/inspected.
  They are not a full feature-complete release and were not installed into the
  user's launcher or existing saves. No public repository was created or pushed.
