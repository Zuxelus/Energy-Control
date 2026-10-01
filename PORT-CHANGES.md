# Port change notice — 2026-09-30 UTC

Independent migration work produced for the local 1.21.1 Fabric/NeoForge task,
with implementation assistance from Codex. This is not an upstream Zuxelus release.

- Replaced the active single-loader build with Architectury common/fabric/neoforge
  modules; original build configuration remains at the parent commit.
- Added a limited panel/card/grouping/rendering preview in a separate `port`
  package; original 200 Java source files and upstream README/LICENSE are intact.
- Adapted the upstream ScreenManager rectangle-growth semantics to testable common
  code and reused original panel/card models and textures with their GPL license.
- Added 1.21.1 component data, typed network edit, vanilla menus and loader-specific
  read-only FE/E adapters. Simplified UI/recipes and missing features are disclosed
  in `docs/STATUS.md`; no legacy world migration or full feature parity is claimed.
- Added tests, dependency integrity metadata, source jars, license/source inventory,
  verified wrapper, build logs and artifact/reproducibility checks.

All original history, authorship and license notices remain. New code is distributed
under GPL-3.0-only. No remote publication or user game modification was performed.

## 2026-10-01 core gameplay continuation

- Added actual development client/dedicated-server acceptance for both loaders,
  real energy capability fixtures and unedited framebuffer screenshots. QA is
  compiled only with -Pqa and is excluded from release jars.
- Fixed invisible/inverted panel text, horizontal-facing convention and font
  depth; changed NeoForge client registration to dedicated loader events after
  a real right-click test exposed its late menu registration.
- Added multiline text editing, original ten-line/@ format behavior, sensor
  titles/field visibility, font/scale/alignment/background/refresh settings and
  four power modes. Initial slot sync no longer overwrites unsaved typing.
- Tested live energy packets, actual Save button, card binding, reload and screen
  rebuild; retained failures and regression evidence. User creative-mode
  interaction interrupted one test; QA-only protection now prevents accidental
  panel breaking during automation. Production mining behavior is unchanged.
- Expanded the precise missing-feature inventory; full upstream parity is still
  not claimed. Preserved all original history, copyrights, licenses and sources.

## 2026-10-01 storage and portable continuation (0.3)

- Added bounded variant-aware fluid probes, arrays, three effective upgrade
  types, component-backed portable inventory and transparent grouped panels.
- Kept shared evaluation/state/menus/networking in common; platform capabilities
  remain in each loader. No test readings or machine-specific constants enter
  production. No external industrial mod is bundled.
- Fixed dedicated-server S2C registration and two real GUI layout/clipping
  defects. Added meaningful red/green tests for measurements, target binding
  and portable component persistence.
- Removed the previous QA-only break prevention hook completely. Accepted
  ordinary creative breaking/sneak-placement, concurrent observers and actual
  disk reload on both loaders with production server initialization.
- Added126 upstream source rows and exact new acceptance evidence. Existing
  original source/history/license are retained. Full parity remains unfinished.


## 2026-10-01 real IE and display continuation (0.4)

- Added bounded manual/automatic pages, preserving lines beyond the old32-line
  display limit; synchronized page metadata and saved settings.
- Added grouped hologram pitch/yaw and visual plane depth, GUI controls and
  persistence. Solid case geometry and upstream circuit height remain pending.
- Accepted six tilted facings and disk restart on both loaders. Fixed hologram
  menu title. No normal breaking behavior changed.
- Accepted actual IE LV capacitor/formed tank reads, arrays, detach/reform,
  unloaded targets without force-loading and real-machine disk restart.
- Fixed optional IE development classpath discovery of its approved nested
  DualCodecs/BlockModelSplitter; retained dependency verification and exact hashes.
- No IE source/assets/binaries redistributed. All upstream history/license remain.
