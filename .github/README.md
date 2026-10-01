# Energy Control Enhanced

Independent GPLv3 fork of [Energy Control by Zuxelus and contributors](https://github.com/Zuxelus/Energy-Control) for **Minecraft 1.21.1, Fabric and NeoForge**, using an Architectury common layer. **Incomplete beta, not an official upstream release or full legacy parity.**

[Download beta releases](https://github.com/sdy623/Energy-Control-Enhanced/releases) · [Feature matrix](../docs/FEATURE-MATRIX.md) · [Upstream comparison](../docs/UPSTREAM-FEATURE-MATRIX.md) · [Build instructions](../docs/BUILD.md)

Information panels display live energy, fluid, inventory, time and redstone data. Features include grouped advanced/extender screens, text/font/scale controls, pages, horizontal bars, portable single-card displays, energy/fluid arrays, range/capacity/precision upgrades, holographic projection controls, and solid screen thickness/slopes.

Version 0.6 adds sided inventory cards with five field switches, five consumable sensor kits, a 54-slot card holder, and continuous slopes across grouped solid screens.

## Install

Use exactly one matching loader jar on both client and server. Java 21 and Minecraft 1.21.1 are required. Tested dependencies:

- Fabric Loader 0.16.14, Fabric API 0.116.6+1.21.1, Architectury API 13.0.8. Team Reborn Energy API is included.
- NeoForge 21.1.209, Architectury API 13.0.8.
- Optional Immersive Engineering 1.21.1-12.4.2-194 on NeoForge. IE is never bundled.

Do not combine with another Energy Control jar (same `energycontrol` mod ID). Legacy 1.12/1.20 world migration is unsupported. Use a backed-up world when adopting this beta. Right-click opens panel configuration; creative left-click breaks blocks normally. There is no release anti-break test protection.

## Compatibility and evidence

Fabric uses real Team Reborn Energy / Transfer APIs; NeoForge uses real energy/fluid/item capabilities. Actual IE acceptance covers LV capacitors, formed sheetmetal tanks, current-transformer average wire power and thermoelectric generation potential. Unsupported machines show explicit messages. No claim of general IC2 replacement or all industrial mod support is made.

The 0.6 acceptance record contains 201 successful runtime assertion executions and 58 original game screenshots across ten accepted isolated runs. Both loaders cover inventory/holder/kits/slopes, multiplayer and saved-world reload; real IE is NeoForge only. 65 JVM tests passed. Two clean offline production builds produced byte-identical runtime/source jars. [Exact scope and excluded failures](../docs/QA-V6.md).

These are isolated development-client and dedicated-server results, not a claim of testing every launcher, modpack or server size. Seven failed/incomplete QA attempts are retained and excluded from passing totals, including the old tick-driven full IE replay. Test fixtures and QA protection are absent from release jars.

## Remaining work

Kit assembler, original recipe balance, alarms/thermal monitors, timers/counters/lamps, advanced fluid selectors, arbitrary field layouts, legacy font/color/touch/web upgrades, many specialized integrations and full localization remain unfinished. Sloped collisions use conservative 16-step approximations per active axis. Very large groups, long-running performance and conflicting simultaneous edits are not comprehensively accepted. See the linked matrices before installing.

## Source, credit and publication

Full upstream history, license and copyright notices are preserved. Original `src/`, root README files and LICENSE remain unchanged; new code is in `common/`, `fabric/` and `neoforge/`. The original 1.20 Forge baseline is `76a533bbf62a266ee4ca89469c6feda0b66ade86`. Complete history/source archives and original screenshot evidence accompany the GitHub release in parts smaller than 20 MB.

The new port runtime and documentation were substantially authored with Codex assistance. Textures originate from the upstream GPL project; screenshots are actual unmodified game captures. Modrinth publication has **not** occurred: its current AI-content eligibility rules and authenticated submission remain unresolved. [Publication record](../docs/MODRINTH-PUBLICATION.md).

Older milestone documents and the embedded PORT-README preserve their historical local-only status; this README and the release record describe the subsequent authorized GitHub publication.
