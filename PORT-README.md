# Energy Control Enhanced — Minecraft1.21.1 beta

**Incomplete independent GPLv3 port. Core gameplay verified in isolated Fabric
and NeoForge development clients/servers; not a full upstream replacement.** Original upstream: https://github.com/Zuxelus/Energy-Control.

Full upstream Git history is preserved; this work starts from `1.20-forge` at
`76a533bbf62a266ee4ca89469c6feda0b66ade86`. Original `README.md`, `README.txt`,
LICENSE and `src/` remain intact. New modules: `common`, `fabric`, `neoforge`.

- [Implementation and missing features](docs/STATUS.md)
- [Prioritized feature acceptance matrix](docs/FEATURE-MATRIX.md)
- [New storage/portable and normal multiplayer acceptance](docs/QA-V3.md)
- [Upstream source-by-source inventory](docs/UPSTREAM-FEATURE-MATRIX.md)
- [Real gameplay evidence and repeatable scenarios](docs/QA.md)
- [Source comparison and architecture](docs/PORTING.md)
- [Build and verification record](docs/BUILD.md)
- [Legacy integration availability](docs/COMPATIBILITY.md)
- [Dependency sources and licenses](docs/DEPENDENCIES.md)

Current interaction design: open a panel to insert cards. Use Slot to choose the
text card, enter text and Save. Sneak-use an energy/redstone card on its target
face before insertion. Place matching extenders in the same plane and facing.
Advanced panels currently add card slots; the complete upstream advanced GUI
and other machines remain pending. The core GUI, live display and persistence have real game acceptance; see QA.md
for the exact fixture scope and untested cases.

Only task-owned isolated development games were started/stopped; no existing user game was started/stopped, no existing instance was changed, and no public
repository or release was created. See STATUS.md before considering installation.

0.3 continuation adds fluid and energy arrays, range/capacity/precision upgrades,
portable screens and basic grouped holographic screens. Sneak-use a card on a
target face; array cards add targets, repeat the same face to remove, or use a
different face to update. Place extenders by sneaking when clicking a panel.
Left-click in creative breaks blocks normally; right-click opens configuration.
Installed upgrade counts0..3 give range64/128/256/512, targets4/8/12/16 and
precision0/1/2/3. Portable panels have one card slot and the same three upgrades.


0.4 adds manual/automatic pages and holographic pitch/yaw/plane-depth controls
under the Layout button; settings survive server restart on both loaders.
Real IE LV capacitor/tank acceptance is NeoForge-only. See [QA-V4](docs/QA-V4.md)
for36 actual IE checks and19 display checks per loader; broader integrations and
full upstream parity remain pending. This port never bundles IE.

0.5 adds per-card horizontal storage bars and physical advanced-case thickness
1..16 in Layout, with matching extender geometry and collision. Portable bars use
typed server rows; matching 0.5 clients and servers are required. Both loaders
have real two-client edit/break/place/restart acceptance. See docs/QA-V5.md.

The 0.5 Machine Data Card supports IE12.4.2-194 current-transformer average FE/t
and thermoelectric generation potential on NeoForge. Sneak-use it on the machine,
then insert it into a panel. These are separately accepted actual-machine reads;
Fabric shows unsupported machine data until an appropriate adapter exists.

0.6 adds real sided inventory cards with five field switches,54-slot card holders,
five target-detecting sensor kits, main/offhand item locking and grouped solid
slopes. Layout controls adjust the solid plane independently from holograms.
Both loaders have normal interaction/multiplayer/save acceptance; see QA-V6.md.
This is still incomplete beta software. Modrinth publication is authorized but
has not occurred; actual blockers and a truthful page draft are in docs/
MODRINTH-PUBLICATION.md and MODRINTH-DESCRIPTION.md.
