# Minecraft 1.21.1 Architectury port — work in progress

Independent GPL-3.0 port of Zuxelus/Energy-Control; not an upstream release.
Original copyrights, LICENSE and all upstream branches/tags/history are retained.

## Provenance

Full non-shallow clone: https://github.com/Zuxelus/Energy-Control

- Baseline: `1.20-forge`, `76a533bbf62a266ee4ca89469c6feda0b66ade86`.
- Local branch: `port/1.21.1-architectury`.
- Reference: `26.3-neoforge`, `b263e409e3e3ec43870add7e195b31892082db11`.
- Reference: `1.18-fabric`; original `master` is 1.12.2, not the port baseline.
- Initial clone: 13 remote branches, 234 reachable commits; `git fsck --full` clean.
- No repository published or remote refs modified.

## Actual source differences and module boundaries

Baseline: 200 Java files, ForgeGradle, Java 17, Minecraft 1.20.1, Forge 47.4.23.
New NeoForge branch: 373 changed files, 3,625 insertions, 6,285 deletions.
It removes AE2, Mekanism, Bigger Reactors and ComputerCraft implementations;
using that branch wholesale would silently lose features.

| Upstream code | Required migration |
| --- | --- |
| `api/ItemStackHelper` mutable `ItemStack.getTag()` | Copy/update `DataComponents.CUSTOM_DATA`; dimension-aware targets |
| `init/ModItems`, `ModTileEntityTypes` | Architectury deferred registries |
| `network/ChannelHandler`, `zlib/network/PacketTileEntity` | 1.21 payloads; vanilla menu actions and BE updates where sufficient |
| `tileentities/ScreenManager` | Preserve bounded coplanar rectangular growth; never force-load chunks |
| `containers/ContainerInfoPanel`, advanced panel | Vanilla containers, server validation and common menu registration |
| `renderers/TileEntityInfoPanelRenderer` | 1.21.1 vertex/font APIs; client-only registration |
| `crossmod/CrossModLoader` Forge capabilities | Read-only energy probe contract and loader adapters |
| `1.18-fabric` | Reference lifecycle/energy concepts; Yarn-era names cannot be copied into Mojmap |

`common`: state, cards, grouping, block entities, menus and client renderer.
`fabric`: Fabric lifecycle and energy lookup.
`neoforge`: NeoForge lifecycle and FE capabilities.
Original `src/` is retained as readable baseline, outside new source sets.
Only explicitly ported resources are included in new jars.

## Acceptance gates

Do not describe the port as usable until both loaders compile and run in isolated
development environments and rendering/menu/card interaction is verified.
Pure Java tests do not replace in-game verification.
No user instance, save or mod directory may be touched; no game process started
or stopped under the current user constraint.

Unavailable upstream integrations and unimplemented adapters must be listed
separately. Unverified release availability must not be described as absence.
