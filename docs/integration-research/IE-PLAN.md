# Modern integration target: Immersive Engineering

The user identified the modern industrial ecosystem as the intended target.
Use optional NeoForge support for IE; do not treat legacy IC2/EU APIs as a
replacement for current machines. Fabric keeps its own platform interfaces.

Official source: https://github.com/BluSunrize/ImmersiveEngineering/tree/1.21.1
Official version config: https://raw.githubusercontent.com/BluSunrize/ImmersiveEngineering/1.21.1/gradle.properties
Official license: https://raw.githubusercontent.com/BluSunrize/ImmersiveEngineering/1.21.1/LICENSE
Distribution repository is named in the official build: https://maven.blamejared.com/

Concrete inspected release: 1.21.1-12.4.2-194, NeoForge >=21.1.164,
14,180,259 bytes, SHA-256 in IE-artifact.json. The distribution includes
BlockModelSplitter 2.0.1 and DualCodecs 0.1.2; JEI is optional and will not be
installed just for this test. The upstream branch currently reports a newer
pre-release development version; that is not substituted for the release.

The IE license is custom, not GPL. Its distribution is inspected privately
under .tools and excluded from our source/JAR/bundle delivery. No IE assets or
implementation code are copied into the port. Requested scope: only this
project's isolated NeoForge instance, capacitor FE and tank fluid capability
acceptance first, then additional public machine APIs if needed. Runtime
permission for the external distribution was requested separately in chat;
not yet granted at the time of this record. No IE code has been executed.

Generic FE/FluidHandler implementation is present, but real IE compatibility
must remain UNVERIFIED until the external-mod gameplay run passes. Fabric is
not claimed to load this NeoForge distribution. Other modern industrial mods
require their own exact artifact/platform/permission and actual acceptance.
