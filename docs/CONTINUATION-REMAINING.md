# Remaining implementation after d0dfa20 (active work, not a stop point)

The126-row UPSTREAM-FEATURE-MATRIX.md remains the source-by-source inventory.
This list distinguishes implementation work from actual authorization blockers.
Existing0.5 deliveries and the complete upstream history remain immutable.

## Can continue autonomously with existing Minecraft/platform/IE dependencies

1. Inventory sensor: name, complete visible item total, used/total slots, sided
   status, first six slot details, original1.12 five field switches. Read-only
   Fabric ItemStorage and NeoForge item capability; vanilla fallback. Correct the
   modern upstream defect that counts only the first six slots as the total.
2. Generic energy/fluid/inventory/redstone kits and approved IE machine kit:
   consume one only on a recognized target, create a bound card, preserve sided
   dimension/position data. Match successful-use/drop behavior across loaders.
3. Card holder:54 card-only slots, no nested holders, held-item locking and saved
   item components; move/close/reopen/offhand and disk-restart checks.
4. Solid advanced slopes: upstream16 thickness settings and two17-position slope
   controls. Upstream deforms front depth across a grouped screen, rather than
   rotating an entire rigid cuboid. Implement body/text/collision consistently,
   grouping and all six faces; do not call hologram rotation equivalent.
5. Advanced fluid selection, complete field layout/visibility, bar presentation,
   screen font/color upgrades, original holo circuit height and orientation.
6. Kit assembler, card conversion/array recipes and original recipe balance where
   ingredients exist. Specialized absent-mod recipes need explicit documented
   exceptions, not silently unrelated ingredients.
7. Howlers/alarms, thermal monitors with available vanilla/IE heat sources,
   timers/triggers/counters/lamps and server-authorized vanilla target toggling.
8. Existing approved IE machine fields/tier coverage; no new binary needed.
9. Chinese/English UI, tooltips, item names and meaningful errors; source-license
   preservation, survival drops, offhand behavior, larger groups/long runs,
   multiplayer conflicting edits and saved-world regression.
10. Legacy-format import can be implemented/tested against task-owned fixtures;
    original user worlds will not be changed or loaded.

## Real scope/authorization dependencies (do not block unrelated work)

- Additional third-party runtime acceptance: Tech Reborn on Fabric; Mekanism,
  AE2, Extreme Reactors, Draconic Evolution and CC:Tweaked special integrations;
  optional JEI/REI/WTHIT overlays. Exact official version/source/license and
  necessity must be reported before executing a newly requested mod. No new
  artifact has been selected or downloaded by this continuation.
- Original IC2 Experimental, BuildCraft, OpenComputers, Galacticraft Legacy,
  NuclearCraft original, Thermal Expansion and other checked absent1.21.1 APIs:
  see COMPATIBILITY.md. Binary-incompatible replacement projects require a
  separately identified target; these are compatibility exceptions, not claims
  that their historical features have been ported.
- WebSocket/web upgrade introduces a new protocol and external communication;
  requires a concrete protocol/security scope before runtime use.
- Public repository destination is unspecified: keep source/history local.
- Library official upload helper unavailable; do not repeat or bypass failure.

No decision is required for items1–10 above. Each implemented item needs both
loader gameplay/network/save acceptance before marking its row passed.

## Upstream behavior evidence inspected

1.12.2 master1620926: ItemCardInventory has five bit flags1/2/4/8/16;
ItemKitInventory uses a supported inventory probe. Modern1.20-forge76a533b:
ItemCardInventory outputs name/items/used-size/sided/first6; CrossModLoader's
inventory loop only counts first6 (intentional fix documented above).
ItemKitMain stacks16, consumes1 on successful server use and drops a bound card
with zero pickup delay. InventoryCardHolder has54 ItemCardMain-only slots.
GuiPanelSlope has16 thickness steps and horizontal/vertical offsets in steps7;
RotationOffset interpolates a wedge-shaped front across the group. Full original
source remains in src and Git; no original source file is edited.

## User-directed step2 release boundary

The user subsequently requested finishing step2 and publishing an enhanced fork
to Modrinth. Items1-4 are now implemented and accepted on both loaders; real IE
machine kit success is additionally accepted on NeoForge. Items5-10 are deferred
and explicitly listed as beta limitations. No extra mod dependency is needed to
complete the accepted step2. Platform publication blockers are separately recorded
in MODRINTH-PUBLICATION.md; they do not negate completed local implementation.
