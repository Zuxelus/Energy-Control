# 1.21.1 migration acceptance matrix

Baseline: upstream 1.20-forge 76a533b; original 1.12.2 history retained. This is a
feature-level audit, not a claim that retained legacy files run in the new build.
S = source implemented, U = JVM test, G = real game acceptance, P = pending.

| Priority | Feature / source reference | Current status (both loaders unless qualified) | Required acceptance |
|---|---|---|---|
| P0 | Fabric client + dedicated server | S/U/G PASS | Real isolated client joins dedicated server; client classes do not prevent server boot |
| P0 | NeoForge client + dedicated server | S/U/G PASS | NeoForge menu registration failure fixed and independently rerun |
| P0 | Basic panel, original InfoPanel | S/G partial | Six basic 2x2 surfaces render; advanced right-click GUI tested. Manual survival drops/shift-click not independently accepted |
| P0 | Advanced panel + both extender tiers, ScreenManager | S/U/G PASS for fixtures | Advanced 3x2 and basic 2x2 in six facings, detach/rebuild; tier/ownership logic has JVM tests, no large-wall stress run |
| P0 | Dynamic renderer, TileEntityInfoPanelRenderer | S/G PASS | Actual glyphs and changing energy packets, six-face PNGs, default/uniform font screenshots |
| P0 | Card component data, ItemStackHelper | S/U/G PASS | GUI Save packet, disk reload; component copy isolation JVM-tested |
| P0 | Energy card and per-face providers | S/U/G protocol PASS | Actual platform API on self-compiled chest fixture; E/FE changing; boundary JVM tests. External machines untested |
| P0 | Text card: original ten lines, title/format codes | S/U/G partial | Ten displayed lines, 512 total chars, multiline Save, @ formatting normalization; Unicode/font support implemented but multilingual game rendering not exhaustively tested |
| P0 | Card GUI/settings, GuiPanelBase/GuiAdvancedInfoPanel | S/G partial | Sensor title + percentage mask tested; labels implemented. Full upstream pages/layout/field selector still P |
| P0 | World persistence and network update | S/G PASS for sequential clients | Both worlds restarted; text/bounds/style/fields retained; edit/settings packets and stale-menu rejection. Concurrent two-player edits P |
| P1 | Font, alignment, text/background color | S/G partial | Default/uniform, 50-200% fit, left/center/right, 3 colors/2 backgrounds; 125% centered uniform persisted and captured |
| P1 | Tick rate and power modes | S/G PASS | Signal/no-signal and inverted/off assertions; always-on live frames; 40-tick setting synchronized and reloaded |
| P1 | Screen thickness/slopes/rotation | P | Original geometric controls or clearly documented migration boundary |
| P1 | Display labels, bars and per-card field masks | Partial S/G | Title, labels and percent flags stored; title/percent game-tested. Bars and arbitrary field layouts P |
| P1 | Time and redstone cards | S/G partial | Time in live screen/logs; redstone card source implemented but its changing reading has no separate game acceptance |
| P1 | Inventory card | P | Real vanilla container totals, empty/full, reload |
| P1 | Fluid/basic/advanced/array cards | P | Loader-neutral fluid adapter, real providers |
| P1 | Energy array cards | P | Multiple bound targets, aggregate, range/error policy |
| P1 | Range/color/touch upgrades | P | Slot semantics, bounded range, configured controls |
| P2 | Card holder | P | Inventory persistence and nested card rules |
| P2 | Portable panel | P | Held-item menu, rendering and sync |
| P2 | Holographic panel/extender | P | Transparent surface, grouping and scale |
| P2 | Kit assembler and sensor kits | P | Recipes, actual conversion, GUI, inventory |
| P2 | Howler/industrial alarms | P | Redstone trigger, sound lifecycle, GUI/config |
| P2 | Thermal/remote monitors | P | Provider temperature contract, limits, redstone |
| P2 | Range trigger/timer/counters | P | Tick-accurate behavior and persistence |
| P2 | Lamps and miscellaneous components | P | Registration/recipe/state behavior |
| P2 | Touch/toggle actions | P | Authorized server-side target actions |
| P3 | Websocket/web upgrade | P | Separate protocol/security scope before execution |
| P3 | Specialized reactor/machine/network integrations | P | See COMPATIBILITY.md; no absent API impersonation |
| P3 | JEI/REI/WTHIT | P | Target versions and optional-class isolation |
| P3 | Legacy world/card migration | P | Explicit data migration fixtures; never test user worlds |

This continuation authorizes isolated task-owned development processes and the
official Minecraft EULA. The previous no-launch acceptance blocker is removed.
No existing instance or process may be modified or stopped. QA-only blocks/code
must be excluded from normal release builds. Final per-case results and unedited screenshots: `docs/evidence/runtime/{fabric,neoforge}-final/`. See QA.md for exact limitations. No claim of complete upstream parity.
