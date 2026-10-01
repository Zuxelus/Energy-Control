# Energy Control 1.21.1 — Architectury preview

**Incomplete independent GPLv3 port. Core gameplay verified in isolated Fabric
and NeoForge development clients/servers; not a full upstream replacement.** Original upstream: https://github.com/Zuxelus/Energy-Control.

Full upstream Git history is preserved; this work starts from `1.20-forge` at
`76a533bbf62a266ee4ca89469c6feda0b66ade86`. Original `README.md`, `README.txt`,
LICENSE and `src/` remain intact. New modules: `common`, `fabric`, `neoforge`.

- [Implementation and missing features](docs/STATUS.md)
- [Prioritized feature acceptance matrix](docs/FEATURE-MATRIX.md)
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
