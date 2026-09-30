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
