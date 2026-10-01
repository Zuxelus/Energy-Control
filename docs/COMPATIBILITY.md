# Integration migration inventory

Checked 2026-09-30 UTC against official release pages linked below. "Unavailable"
means the named original project has no listed 1.21.1 Fabric/NeoForge release;
unrelated rewrites are not binary-compatible substitutes. No content mods installed.

## No matching original-project release listed

| Legacy target | Official releases observed | Consequence |
| --- | --- | --- |
| [IC2 Experimental](https://www.curseforge.com/minecraft/mc-mods/industrial-craft) | Up to 1.12.2, Forge | Original EU/reactor API unavailable |
| [BuildCraft](https://www.curseforge.com/minecraft/mc-mods/buildcraft) | Up to 1.12.2, Forge | Original MJ/engine API unavailable |
| [OpenComputers](https://www.curseforge.com/minecraft/mc-mods/opencomputers) | Up to 1.12.2, Forge | Original component API unavailable |
| [Galacticraft Legacy](https://www.curseforge.com/minecraft/mc-mods/galacticraft-legacy) | 1.12.2 / 1.7.10 | Original core/planets API unavailable |
| [NuclearCraft original](https://www.curseforge.com/minecraft/mc-mods/nuclearcraft-mod) | Up to 1.12.2 | Original reactor API unavailable; Neoteric is separate |
| [Thermal Expansion](https://www.curseforge.com/minecraft/mc-mods/thermal-expansion) | Up to 1.20.1 | No listed 1.21.1 specialization target |
| [Bigger Reactors](https://www.curseforge.com/minecraft/mc-mods/biggerreactors) | Up to 1.20.1 | No listed 1.21.1 target; distinct from Extreme Reactors |
| [Advanced Generators](https://www.curseforge.com/minecraft/mc-mods/advanced-generators) | Older Forge; no 1.21.1 listed | Original turbine/syngas adapter unavailable |
| [Industrial Reborn](https://www.curseforge.com/minecraft/mc-mods/industrial-reborn) | Up to 1.19.2, Forge | Original adapter unavailable |

## Target releases exist; specialized adapters unfinished

| Target | Evidence for 1.21.1 | Port status |
| --- | --- | --- |
| [Mekanism](https://www.curseforge.com/minecraft/mc-mods/mekanism) | NeoForge 10.7.19.85 | Exposed FE only; reactor, gas, induction and Generators adapters not ported |
| [Extreme Reactors](https://www.curseforge.com/minecraft/mc-mods/extreme-reactors) | NeoForge 2.4.28 | Exposed FE only; specialized reactor/turbine cards not ported |
| [AE2](https://www.curseforge.com/minecraft/mc-mods/applied-energistics-2) | NeoForge 19.2.18 | Network/inventory/crafting adapters not ported; Fabric 1.21.1 not verified |
| [CC: Tweaked](https://www.curseforge.com/minecraft/mc-mods/cc-tweaked) | Both loaders listed | Peripheral integration not ported |
| [Immersive Engineering](https://www.curseforge.com/minecraft/mc-mods/immersive-engineering) | NeoForge 12.4.2-194 | Generic FE + FluidHandler source implemented; real IE12.4.2-194 acceptance pending. See integration-research/IE-PLAN.md |
| [Draconic Evolution](https://www.curseforge.com/minecraft/mc-mods/draconic-evolution) | NeoForge 3.1.4.633 beta | Exposed FE only; large native-energy API needs adapter |
| [Tech Reborn](https://www.curseforge.com/minecraft/mc-mods/techreborn/files/all?page=1&pageSize=20&version=1.21.1) | Fabric 5.11.19 for 1.21.1 listed | Team Reborn Energy protocol implemented; actual Tech Reborn compatibility not tested |

Generic FE/E support does not prove compatibility with every block. The provider
must expose storage on the bound face. FE integer capacity limits remain at the
provider boundary; common energy inputs use long and aggregated measurements use BigDecimal. E and FE are labeled separately,
never converted. JEI/REI/WTHIT overlays are not ported in this preview.
