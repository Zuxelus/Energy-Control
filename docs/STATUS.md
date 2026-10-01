# Energy Control Enhanced - incomplete0.6 beta

This independent GPLv3 port continues the full-history branch from263f6ae via
19d8dac. Original200 Java sources, README files and LICENSE remain intact outside
active source sets. See UPSTREAM-FEATURE-MATRIX.md (126 source rows plus crosscutting
features) and FEATURE-MATRIX.md. This is not a full upstream replacement.

Implemented on both loaders: basic/advanced/extender groups, six facings, dynamic
text/font/scale/style, card GUI/components/network sync, real per-face E/FE/fluid
interfaces, variant-aware arrays, effective range/capacity/precision upgrades,
portable screen inventory and live packets, transparent grouped holograms.
New0.4: manual/automatic paging retains up to256 source lines with an explicit
limit notice; page sizes4/8/16/32 and auto off/40/100/200ticks. Hologram pitch/yaw
-56..56 degrees and plane depth1..16 sixteenths are saved and synchronized.
Rotation is about the whole group center. Hologram plane depth remains independent.
New 0.5 adds typed horizontal energy/fluid bars on world and portable screens,
saved per card, and solid advanced-case thickness 1..16 with matched models,
selection/collision and text depth on core/extenders in all six facings.
Real two-client GUI edits, ordinary creative break/sneak-place and disk reload
passed on both loaders (37 assertions each). Matching 0.5 client/server required.

Basic panels have one card; advanced/holographic eight; portable one. All have
three upgrade slots. Arrays retain16 unique positions; active4/8/12/16. Range
64/128/256/512 blocks; precision0..3 decimals. No forced chunk or cross-dimension
read. E and FE are not converted. BigDecimal totals preserve fluid variants.

NeoForge acceptance now includes real Immersive Engineering12.4.2-194 LV
capacitors and formed sheetmetal tanks: actual changing FE/mB and arrays,
normal capacitor break/place, tank disassembly/reform, chunk unload/reload and
clean server restart (36 assertions). The exact official IE jar and two embedded
libraries were explicitly authorized, loaded only in this project, and excluded
from all deliveries. This does not prove every IE machine or Fabric compatibility.
0.5 additionally accepts actual IE current-transformer wire average FE/t and
thermoelectric generation potential, stop/resume, two-client menus and disk
restart (36 additional assertions). This is read-only; no meter samples are set.
Fabric external-machine acceptance remains pending; its actual platform interfaces
and self-compiled providers have the separate0.3 protocol acceptance.

Right-click opens configuration. Creative left-click breaks normally; sneak-place
builds against panels. No break protection remains. Both loaders already have0.3
normal-server, two-client edit/detach/rebuild and disk-restart acceptance.0.4 adds
19 real display assertions per loader including six tilted facings and reload.
New code is shared; all QA classes/dependencies are absent from release jars.
No existing user game or world was changed or started/stopped.

Remaining work by value:
1. More real modern industrial targets/machines and machine-specific fields;
   other IE tiers/machines, reactor/gas/induction statistics. A new read-only
   Machine Data Card implements IE current-transformer average FE/t and thermal
   generation potential; the exact specialized acceptance is in QA-V5.md.
   External artifacts beyond the approved IE distribution require specific scope.
2. Per-field layout, vertical/color/threshold bars, advanced fluid selectors ;
   complete localization and original color/touch upgrades.
3. Original holo circuit height and complete field layout. Solid grouped slopes are implemented and game-accepted in0.6.
4. Kit assembler, original recipe balance; alarms/thermal monitors,
   timers/triggers/counters/lamps and specialized integrations.
5. Authorized touch/control, web upgrade/protocol, overlays, legacy save migration.

Twenty-five simplified recipes. Survival panel drops, large walls/long uptime,
conflicting simultaneous edits and every machine are not accepted. Main/offhand
portable and holder behavior are accepted in0.6.
QA.md/QA-V3.md/QA-V4.md/QA-V5.md distinguish historical and current actual results.
Old APIs are unavailable only where COMPATIBILITY.md cites official release checks.
Known toolchain warnings remain: Loom1.7 unsupported notice, Gradle9 deprecations,
NeoForge deprecated event-bus annotation. No public publication was performed.

0.6 adds complete-slot sided inventory readings, five field switches,54-slot card
holder, generic sensor kits and advanced grouped solid slopes. Existing text,
menus, network and persistence work on both loaders. Inventory/holder/kits have
117 accepted assertion executions; slopes/main+normal-reload add72 and the final
NeoForge actual IE kit adds12, totaling201. See QA-V6 for the deliberately excluded
failed IE replay attempts and the exact acceptance boundary. These counts describe scenarios, not proof
of every possible machine/state. Full original feature parity remains unfinished.
Modrinth publication was requested after this step2; unresolved current rules on
primarily AI-generated projects, missing authenticated upload capability and
public corresponding-source delivery are recorded in MODRINTH-PUBLICATION.md.
