# Feature acceptance matrix — 0.5 bars, case and IE power continuation

Both loaders are Minecraft 1.21.1. Implemented does not mean full upstream parity.
The source-by-source inventory has 126 rows across upstream 1.12.2, 1.20 Forge,
and 26.3 NeoForge: [UPSTREAM-FEATURE-MATRIX.md](UPSTREAM-FEATURE-MATRIX.md).
S = production source, U = JVM tests, G = actual isolated game acceptance.

| Priority | Feature | Fabric | NeoForge | Exact scope / remaining work |
|---|---|---|---|---|
| P0 | Client + dedicated server separation | S/G pass | S/G pass | Both boot/join; portable S2C registration now separates server codec from client receiver |
| P0 | Basic/advanced/extender screens | S/U/G partial | S/U/G partial | 0.2 six-face 2x2/3x2; 0.3 normal creative detach/place/rebuild. Survival drops not independently tested |
| P0 | Dynamic glyphs, font, facing, scale | S/G partial | S/G partial | Real changing text; six faces; default/uniform font; 50–200% fit; physical thickness 1..16 implemented; slopes pending |
| P0 | Card component data and editor | S/U/G pass | S/U/G pass | Actual GUI Save, copied component isolation, bounded packets, title/percent settings |
| P0 | Concurrent players | S/G pass | S/G pass | Two clients simultaneously viewing; edit updates other player's menu item and world text; break/place updates both |
| P0 | No-protection normal gameplay | S/G pass | S/G pass | Server starts WITHOUT -Pqa; no break hook remains even in QA source; actual creative break and sneak-place accepted |
| P0 | Save/quit/reopen | S/G pass | S/G pass | 0.2 world/style; 0.3 target/upgrade/portable storage reload; normal-server multiplayer edit and player-item disk reload recorded separately |
| P1 | Per-face energy | S/U/G protocol pass | S/U/G protocol pass | Fabric Team Reborn E: self-compiled provider acceptance. NeoForge FE: actual IE LV capacitors accepted in 0.4; other machines pending |
| P1 | Per-face fluid | S/U/G protocol pass | S/U/G protocol pass | Fabric SIDED StorageView 81,000 units/bucket -> mB; NeoForge IFluidHandler 1,000 mB/bucket. All visible tanks (bounded 256); variant components separated. NeoForge actual IE sheetmetal tanks accepted in 0.4 |
| P1 | Energy array | S/U/G pass | S/U/G pass | Sneak-bind/remove/update side, dedup by dimension/position, max16; overflow-safe totals; no E/FE mixing |
| P1 | Fluid array | S/U/G pass | S/U/G pass | Water/lava never combined; totals per variant; actual dynamic network readings; unavailable targets reported |
| P1 | Range upgrade | S/U/G pass | S/U/G pass | 0..3 -> 64/128/256/512 blocks; no cross-dimension access or chunk forcing. Gameplay exercises64->128; all tiers unit-tested |
| P1 | Capacity upgrade | S/U/G pass | S/U/G pass | New port feature, 0..3 ->4/8/12/16 active array targets. Inactive targets retained and reported |
| P1 | Precision upgrade | S/U/G pass | S/U/G pass | New port feature,0..3 decimal places; mB conversion/rounding unit-tested; game exercises2 decimals |
| P1 | Portable screen | S/U/G pass | S/U/G pass | One card + three upgrade slots, actual held-item open/edit/live update, locked parent item, close/reopen and disk reload |
| P1 | Holographic panel/extender | S/G partial | S/G partial | Transparent 2x2; six face directions with pitch/yaw and visual depth controls; GUI/network/disk reload accepted in 0.4. Original circuit projection height pending; solid thickness has separate 0.5 acceptance |
| P1 | Card labels/percent/each-target | S partial | S partial | Stored and server-evaluated; title/percent game-tested; Each button not independently accepted in game |
| P1 | Text/time/redstone | S/G partial | S/G partial | Text ten lines/512chars, @ formatting; time displayed. Dedicated changing-redstone scenario pending |
| P1 | Refresh/redstone power modes | S/G pass0.2 | S/G pass0.2 | Always-on/signal/inverted/off,40ticks and persisted settings |
| P1 | Inventory card | Pending | Pending | Vanilla/container inventory totals and filters |
| P1 | Upstream advanced liquid selectors | Partial | Partial | Generic fluid card enumerates tanks; original advanced-field selectors not ported |
| P1 | Bars/pages/field layout | Partial | Partial | Manual/automatic paging (4/8/16/32 lines; up to256 source lines) and disk reload accepted in 0.4 on both loaders. Typed horizontal energy/fluid bars implemented, GUI/multiplayer/portable/reload accepted in 0.5; arbitrary field layout and other bar styles pending |
| P1 | Color/touch/web upgrades | Pending | Pending | Current fixed GUI colors are not upstream upgrade parity |
| P1 | Modern industrial targets | External machines pending | IE partial accepted | IE12.4.2-194 LV capacitor + formed tank accepted (36 actual assertions). No Fabric IE claim; 0.5 current-transformer average FE/t and thermoelectric potential accepted (36 more actual assertions); other machines/tiers/mods pending |
| P1 | Solid case thickness | S/U/G partial | S/U/G partial | Advanced core/extenders 1..16 with matching rendering/collision; six actual 4/16 facings, 8/16 GUI/sync/break/place/restart. Slopes/rotation pending |
| P2 | Card holder | Pending | Pending | Internal inventory and nesting rules |
| P2 | Kits/assembler | Pending | Pending | Conversion recipes, machine GUI, inventory, balancing |
| P2 | Howlers/alarms/thermal monitors | Pending | Pending | Sound/temperature/limits/redstone |
| P2 | Timers/triggers/counters/lamps | Pending | Pending | Stateful block behavior and persistence |
| P2 | Touch/toggle actions | Pending | Pending | Server-authorized target control |
| P3 | Websocket | Pending | Pending | New protocol requires separate scope approval |
| P3 | Specialized reactor/network APIs | Pending or original API unavailable | Pending or original API unavailable | COMPATIBILITY.md lists only officially checked absent targets; other entries remain unknown, not N/A |
| P3 | JEI/REI/WTHIT | Pending | Pending | Optional class isolation and target compatibility |
| P3 | Old world/card migration | Pending | Pending | No user worlds touched; no migration claim |

Evidence: [QA-V5.md](QA-V5.md), [QA-V4.md](QA-V4.md) and [QA-V3.md](QA-V3.md), unedited PNGs and run logs under
`evidence/runtime-v5`, `evidence/runtime-v4` and `evidence/runtime-v3`. New source has no dependency on QA providers.
This remains an incomplete port; no public repository/release was created.
