# Inventory, sensor kits and card holder acceptance (active)

Production paths use Fabric ItemStorage.SIDED, NeoForge ItemHandler.BLOCK and a
vanilla Container/WorldlyContainer fallback. Reads never insert/extract or force
chunks. All visible slots contribute to totals, with first6 details and explicit
partial-result labeling above4096 views. BigDecimal prevents aggregate overflow.
Original1.12 fields name/items/slots/sided/details are saved as five bit flags.
Both panel and portable menus expose them and the existing typed occupied bar.

Kit behavior follows original successful-use consumption and zero-delay card drop.
Five kits use existing energy/fluid/inventory/redstone/machine probes. Unsupported
targets do not consume. Fabric early block interaction and NeoForge's item-first
hook must prevent vanilla container opening on success. Original54-slot holder
accepts cards only, rejects nested holders, locks its held inventory slot, and
stores contents in the item component. Offhand holder and portable runtime tests passed on both loaders.

Run -Pqa -PqaStage=inventory, editor and -PqaObserver, then inventory-reload after
normal stop. Fabric127.0.0.1:25582 and NeoForge25583, under each loader/run/*inventory*.
These are task-owned offline development instances. Existing user games untouched.
Original PNGs and logs are kept in evidence/runtime-v6; no new external mod loaded.

Actual vanilla chest contains items in slots0,6,26; dynamic changes in slot26 must
change total even though it is outside six-slot preview. Real furnace top/side
have distinct visible inventory. Both clients open menus and observe field edits.
Editor uses kits in SURVIVAL, then real card drops/pickup; energy/fluid kit tests
use existing self-compiled platform fixtures, not external machines. The holder
uses actual shift-click, rejects ordinary/nested items and held-parent number-key
swap, closes/reopens, and survives player disk restart. Portable fields use real
menu packets and restart. No test provider or fake value is in production code.

First Fabric attempt: server player remained on empty hand when kit action was
attempted; root cause later confirmed: the QA server assigned selected slot7 after login without sending its normal slot-selection packet. New clients remained on slot0 and correctly suppressed a redundant selection0 packet. QA now sends ClientboundSetCarriedItemPacket(7); production logic was unchanged. Next diagnostic run proved correct consume/
card-drop behavior, but an immediate second click after portable Fields failed
because visibility updated on the next tick. Portable Fields now updates its
widgets immediately. QA selects its kit immediately before use. Both interrupted
attempts are retained and excluded from complete acceptance; counts belong in the
final summary only after all required completion markers and clean saves.

The remaining list in CONTINUATION-REMAINING.md stays active. This is not a
stage-ending report or a claim of complete upstream coverage.

Accepted runtime-v6 runs (all complete, no FAIL):
- Fabric inventory: server11/editor18/observer4=33 assertions,9 original PNGs.
- Fabric disk reload: server10/client3=13 assertions,3 original PNGs.
- Fabric offhand+reload: server10/client9=19 assertions,5 original PNGs.
- NeoForge inventory: server11/editor18/observer4=33 assertions,9 original PNGs.
- NeoForge offhand+reload: server10/client9=19 assertions,5 original PNGs.
Total117 passing assertion executions (includes repeated reload coverage),31 PNGs.
Seven clients completed with Gradle exit0; five servers saved all dimensions before
ending their remaining development watcher. Failed diagnostic runs remain separate.
The successful main runs used SURVIVAL; no click protection was installed. Offhand
runs used normal swap/open/click packets and restored each item to its original slot.
