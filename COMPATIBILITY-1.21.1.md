# Minecraft 1.21.1 NeoForge compatibility work

This local branch backports the existing modern upstream implementation from
`b263e409e3e3ec43870add7e195b31892082db11` (`26.3-neoforge`) to Minecraft 1.21.1,
NeoForge 21.1.209 and Java 21. It retains the upstream single-module architecture,
GPLv3 license, authorship/assets and full Git history. Original immediate-mode
renderers and GUI behavior from the upstream 1.20 lineage were reused where the
26.3 rendering APIs do not exist in 1.21.1. Adaptations and regression harnesses
were authored with Codex assistance.

This is a compatibility candidate, not an official release or full feature-parity
claim. The separate Architectury Fabric/NeoForge implementation is preserved on
its own branch; this branch does not build a Fabric jar. Industrial integrations
already disabled in the modern upstream baseline remain disabled. Native
NeoForge energy/fluid/item capability adapters are retained. Acceptance of a
native energy capability is not acceptance of every industrial mod integration.

## Build

Use Java 21 and the checked-in Gradle 9.2.1 wrapper:

```
./gradlew build --console=plain
```

NeoForge ModDevGradle is 2.0.147. Dependencies resolve from the configured official
NeoForge repositories and the JEI author's Maven. JEI 19.57.0.450 is compile-only.
The existing websocket implementation requires the Netty HTTP codec; the build
embeds 4.1.97.Final to match the Minecraft runtime Netty series. No connection is
enabled for verification. Existing dependency license obligations are unchanged.
The build has no built-in unit-test sources; `test NO-SOURCE` is not a test pass.

## Screen shutdown regression

The original removal callback also ran during chunk unload. It destroyed and
rebuilt grouped screens, accessing chunks that the server was unloading. A
production-only two-block reproducer hangs at shutdown on the unfixed candidate.
`onChunkUnloaded` now drops runtime manager references without accessing blocks;
`setRemoved` still rebuilds a group after ordinary block destruction. Saved
screen bounds and card data are preserved across unload/reload.

The removal/replacement regression also exposed an existing advanced-panel
neighbor-update bug: the inherited basic-panel handler forced redstone behavior
even in always-on/off/inverted modes. Advanced panels now recalculate their own
power mode on neighbor changes and scheduled ticks; normal block removal and
group rebuilding remain enabled.

The independent acceptance harness and raw evidence live alongside this checkout
in `../neoforge-1.21.1-game-qa` and `../review-neoforge-1.21.1`. They are excluded
from the published mod jar. Tests use fresh local directories, localhost-only
servers and self-authored test code. Production-only minimum repros load no QA
mod. The evidence report records exact run IDs, jar hashes, failures and limits.

No new PR or public release is implied by local commits or build success.
