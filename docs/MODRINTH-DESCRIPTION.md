# Energy Control Enhanced

Independent GPLv3 fork of [Energy Control by Zuxelus and contributors](https://github.com/Zuxelus/Energy-Control), ported to Minecraft1.21.1 using an Architectury common layer with Fabric and NeoForge implementations. Not an official upstream release or a complete replacement for every legacy integration.

## What it does

Build information panels that show live energy, fluid, inventory, time and redstone data. Combine advanced panels with extenders, adjust text/font/scale, page through readings, display horizontal bars, and carry a single-card portable screen. Advanced solid screens have sixteen thickness steps and horizontal/vertical grouped slopes; holographic screens have separate projection controls.

Sensor cards bind by sneak-using a target face. Energy/fluid arrays retain up to16 targets; range, active-target capacity and precision upgrades control what is evaluated. Inventory cards count all visible slots and expose five display switches; only the first six slots appear in the detail preview. A54-slot card holder keeps cards together. Sensor kits consume one on a recognized target and drop a bound card; unsupported targets do not consume the kit.

## Modern industrial compatibility

- Fabric: real Team Reborn Energy and Fabric Transfer API probes. External industrial machines have not yet been independently accepted on this loader.
- NeoForge: real energy/fluid/item capabilities. Tested with Immersive Engineering1.21.1-12.4.2-194: LV capacitors, formed sheetmetal tanks, current-transformer average wire power and thermoelectric generation potential. This does not claim every IE machine or every mod is supported.
- An unsupported machine shows an explicit message. Old IC2 and other legacy APIs are not automatically replaced by modern mods.

## Installation

Install on both client and server, using the matching Fabric or NeoForge jar, Java21, Minecraft1.21.1 and Architectury API13.0.8. Fabric also requires Fabric API; tested versions are Fabric Loader0.16.14/API0.116.6+1.21.1 and NeoForge21.1.209. The small Team Reborn Energy API is included in the Fabric jar. Immersive Engineering is optional and is never bundled.

Do not install this together with another Energy Control build: both use the energycontrol mod ID. Legacy1.12/1.20 world migration is not supported. Back up a world before adopting a beta mod.

## Remaining limitations

This is an incomplete beta. Kit assembler, original recipe balancing, alarms/thermal monitors, timers/counters/lamps, arbitrary field layouts, advanced liquid selectors, original font/color/touch/web upgrades and many specialized integrations are not implemented. Fixed GUI colors and the new capacity/precision upgrades do not imply legacy upgrade parity. Full English/Chinese GUI localization remains incomplete. Sloped collisions use16-step conservative approximations per active axis, which are visible in selection outlines. Extremely large groups, long-running performance and conflicting simultaneous edits have not been comprehensively accepted.

## Source and credit

All original GPLv3 licensing and copyright notices are retained. Complete upstream commit history and corresponding buildable source are preserved in the local delivery. A public source/history download must be attached or linked before distributing binaries; the upstream link alone is not the fork's corresponding source.

## AI authorship disclosure and publication status

The new port runtime and this draft were substantially authored with Codex assistance. Textures derive from the upstream GPL project; screenshots are unmodified actual Minecraft frames, not AI-generated images. Modrinth's current restriction on projects primarily comprised of AI output must be resolved before public submission. This draft is not a published Modrinth page.
