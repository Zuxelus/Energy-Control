# Dependency sources and licenses

The mod and copied upstream textures/models retain GPLv3 LICENSE. Original authors:
Zuxelus, Shedar and upstream contributors. Port code is GPL-3.0-only. Changes are
listed in PORTING.md and STATUS.md; license snapshots are in `docs/licenses/`.

| Component | Pinned version | Official source | License / treatment |
| --- | --- | --- | --- |
| Existing JDK | Zulu 21.0.12.1+1-LTS, x86_64 | Installed `C:\Program Files\Zulu\zulu-21`; release file identifies Azul Systems | OpenJDK GPLv2 + Classpath Exception; not redistributed |
| Gradle | 8.10.2 | https://services.gradle.org/distributions/gradle-8.10.2-bin.zip | Apache-2.0; SHA-256 pinned in wrapper properties |
| Gradle wrapper | 8.10.2 | https://github.com/gradle/gradle/blob/v8.10.2/gradle/wrapper/gradle-wrapper.jar | Apache-2.0; independently verified against official checksum |
| Architectury plugin | 3.4.164 | https://github.com/architectury/architectury-plugin | MIT; official Architectury Maven |
| Architectury Loom | 1.7.435 | https://github.com/architectury/architectury-loom | MIT; emits old-version support warning; pinned for 1.21.1 |
| Architectury API | 13.0.8 | https://github.com/architectury/architectury-api/tree/1.21 | LGPL-3.0-or-later; external runtime dependency, not shaded |
| Fabric Loader | 0.16.14 | https://github.com/FabricMC/fabric-loader | Apache-2.0; official Fabric Maven |
| Fabric API | 0.116.6+1.21.1 | https://github.com/FabricMC/fabric-api/tree/1.21.1 | Apache-2.0; external runtime dependency |
| NeoForge | 21.1.209 | https://github.com/neoforged/NeoForge/tree/1.21.1 | LGPL-2.1; official NeoForge Maven |
| Team Reborn Energy | 4.1.0 | https://github.com/TechReborn/Energy | MIT; official Fabric Maven; nested in Fabric artifact |
| Shadow | 8.1.1 | https://github.com/GradleUp/shadow | Apache-2.0; Gradle Plugin Portal; packages this project's common classes |
| JUnit Jupiter | 5.11.4 | https://github.com/junit-team/junit5 | EPL-2.0; Maven Central; tests only |
| JUnit console | 1.11.4 | https://repo.maven.apache.org/maven2/org/junit/platform/junit-platform-console-standalone/1.11.4/ | EPL-2.0; optional core tests |
| Minecraft/Mojang mappings | 1.21.1 | Mojang metadata via Loom | Proprietary license/mapping terms; local development cache only |

Official Gradle distribution SHA-256:
`31c55713e40233a8303827ceb42ca48a47267a0ad4bab9177123121e71524c26`.

Official Gradle 8.10.2 wrapper SHA-256:
`2db75c40782f5e8ba1fc278a5574bab070adccb2d21ca5a6e5ed840888448046`.

The baseline wrapper was initially used for bootstrap, then replaced with the
verified official 8.10.2 wrapper. Original files remain in Git history.
No third-party machine/content mods were installed or executed. No remote shell
script, global installer or user instance modification was used. Declared Gradle
plugins and normal transitive build tools execute inside the build. All Gradle
caches are task-local. Dependency verification metadata records fetched checksums;
it is an integrity record, not an independent audit of every transitive dependency.
