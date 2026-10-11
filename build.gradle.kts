import org.jetbrains.gradle.ext.Application
import org.jetbrains.gradle.ext.Gradle
import org.jetbrains.gradle.ext.RunConfigurationContainer

plugins {
  id("java-library")
  id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.8"
  id("eclipse")
  id("com.gtnewhorizons.retrofuturagradle") version "2.0.2"
}

group = "com.zuxelus.energycontrol"
version = "1.7.10-0.3.7d"

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(8))
  }
  withSourcesJar()
}

minecraft {
  mcVersion.set("1.7.10")

  username.set("Developer")
  injectedTags.put("VERSION", project.version)

  // Enable assertions in the mod's package when running the client or server
  extraRunJvmArguments.add("-ea:${project.group}")

  groupsToExcludeFromAutoReobfMapping.addAll("com.diffplug", "com.diffplug.durian", "net.industrial-craft")
}

tasks.injectTags.configure {
  outputClassName.set("${project.group}.Tags")
}

// Put the version from gradle into mcmod.info
tasks.processResources.configure {
  val projVersion = project.version.toString() // Needed for configuration cache to work
  val projMcVersion = minecraft.mcVersion.get()
  inputs.property("version", projVersion)
  inputs.property("mcversion", projMcVersion)

  filesMatching("mcmod.info") {
    expand(mapOf("version" to projVersion, "mcversion" to projMcVersion))
  }
}

// EnergyControl ships an FML core plugin (ASM hooks), so the jar needs the coremod manifest entries
tasks.jar.configure {
  manifest {
    attributes(
      mapOf(
        "FMLCorePlugin" to "com.zuxelus.energycontrol.hooks.ECHookLoader",
        "FMLCorePluginContainsFMLMod" to "true"
      )
    )
  }
}

// Create a new dependency type for runtime-only dependencies that don't get included in the maven publication
val runtimeOnlyNonPublishable: Configuration by configurations.creating {
  description = "Runtime only dependencies that are not published alongside the jar"
  isCanBeConsumed = false
  isCanBeResolved = false
}
listOf(configurations.runtimeClasspath, configurations.testRuntimeClasspath).forEach {
  it.configure {
    extendsFrom(
      runtimeOnlyNonPublishable
    )
  }
}

// Dependencies
repositories {
  maven {
    name = "OvermindDL1 Maven"
    url = uri("https://gregtech.overminddl1.com/")
    content { includeGroup("com.gregoriust.gregtech") }
  }
  maven {
    name = "GTNH Maven"
    url = uri("https://nexus.gtnewhorizons.com/repository/public/")
  }
  maven {
    name = "CurseMaven"
    url = uri("https://cursemaven.com")
    content { includeGroup("curse.maven") }
  }
  maven {
    // ChickenBones: CodeChickenLib / NotEnoughItems. Serves HTTPS, so no insecure-protocol flag.
    name = "ChickenBones Maven"
    url = uri("https://chickenbones.net/maven/")
    content { includeGroup("codechicken") }
  }
  maven {
    name = "NTM Releases"
    url = uri("https://maven.ntmr.dev/releases")
    content { includeGroup("com.hbm") }
  }
}

dependencies {
  // Cross-mod integration targets. These are compileOnly on purpose: the set contains
  // mutually exclusive versions (two GregTech, two AE2), so putting them on the runtime
  // classpath would break runClient/runServer.

  // --- CurseForge, via CurseMaven. Pinned by <projectId>:<fileId> from the file's
  // --- CurseForge download URL. rfg.deobf() remaps the obfuscated release jars to MCP names.
  compileOnly(rfg.deobf("curse.maven:applied-energistics-2-223794:2296430")) // rv3-beta-6
  compileOnly(rfg.deobf("curse.maven:big-reactors-241994:2282591")) // 0.4.3A
  compileOnly(rfg.deobf("curse.maven:computercraft-67504:2269339")) // 1.75
  compileOnly(rfg.deobf("curse.maven:minetweaker3-239197:2838720")) // 1.7.10-3.1.0
  compileOnly(rfg.deobf("curse.maven:draconic-evolution-223565:2313451")) // 1.7.10-1.0.2h
  compileOnly(rfg.deobf("curse.maven:endercore-231868:4671288")) // 1.7.10-0.2.0.40_beta
  compileOnly(rfg.deobf("curse.maven:ender-io-64578:4671445")) // 1.7.10-2.3.0.430_beta
  compileOnly(rfg.deobf("curse.maven:nuclearcraft-226254:2568943")) // 1.9g
  compileOnly(rfg.deobf("curse.maven:opencomputers-223008:8582723")) // 1.8.10
  compileOnly(rfg.deobf("curse.maven:industrialcraft-2-242638:2353971")) // 2.2.827-experimental
  // Not referenced by any source file today; kept for future integrations.
  compileOnly(rfg.deobf("curse.maven:mekanism-268560:2475797")) // 9.1.1
  compileOnly(rfg.deobf("curse.maven:rotarycraft-235596:4721191")) // V33a

  // --- GTNH Nexus. The :dev classifier is already MCP-named, so no deobf step is needed.
  compileOnly("com.github.GTNewHorizons:Applied-Energistics-2-Unofficial:rv3-beta-1082-GTNH:dev")
  // Bundles both Galacticraft Core and Planets, replacing the two separate artifacts.
  // isTransitive = false on both: their POMs pull IC2 2.2.828, Thaumcraft and JourneyMap,
  // which aren't in our repos (and we already supply IC2 2.2.827 from CurseMaven).
  compileOnly("com.github.GTNewHorizons:Galacticraft:3.3.13-GTNH:dev") { isTransitive = false }
  compileOnly("com.github.GTNewHorizons:GT5-Unofficial:5.09.54.221:dev") { isTransitive = false }
  compileOnly("com.github.GTNewHorizons:GTNHLib:0.6.40:dev")
  compileOnly("com.github.GTNewHorizons:StructureLib:1.4.23:dev")
  compileOnly("com.github.GTNewHorizons:ModularUI:1.2.20:dev")
  compileOnly("com.github.GTNewHorizons:ModularUI2:2.2.18-1.7.10:dev")
  compileOnly("com.github.GTNewHorizons:Railcraft:9.17.33:dev")

  // --- ChickenBones Maven. :dev is MCP-named, so no deobf step is needed.
  compileOnly("codechicken:CodeChickenLib:1.7.10-1.1.3.140:dev")
  compileOnly("codechicken:NotEnoughItems:1.7.10-1.0.5.120:dev")

  // --- NTM Releases. :dev is MCP-named. Keep in sync with the minimum build number
  // --- gated in CrossModLoader.loadCrossMods().
  compileOnly("com.hbm:HBM-NTM:1.0.27_X5844:dev")

  // --- OvermindDL1 Maven: GregTech 6 (gregapi.* / gregtech.tileentity.*).
  // --- Distinct from the GTNH GregTech 5 jar below, which provides gregtech.api.* / gregtech.common.*
  // isTransitive = false: its POM pulls Waila, which isn't published to any repo we use.
  compileOnly("com.gregoriust.gregtech:gregtech_1.7.10:6.17.06:dev") { isTransitive = false }
}

// IDE Settings
eclipse {
  classpath {
    isDownloadSources = true
    isDownloadJavadoc = true
  }
}

idea {
  module {
    isDownloadJavadoc = true
    isDownloadSources = true
    inheritOutputDirs = true // Fix resources in IJ-Native runs
  }
  project {
    this.withGroovyBuilder {
      "settings" {
        "runConfigurations" {
          val self = this.delegate as RunConfigurationContainer
          self.add(Gradle("1. Run Client").apply {
            setProperty("taskNames", listOf("runClient"))
          })
          self.add(Gradle("2. Run Server").apply {
            setProperty("taskNames", listOf("runServer"))
          })
          self.add(Gradle("3. Run Obfuscated Client").apply {
            setProperty("taskNames", listOf("runObfClient"))
          })
          self.add(Gradle("4. Run Obfuscated Server").apply {
            setProperty("taskNames", listOf("runObfServer"))
          })
          /*
          These require extra configuration in IntelliJ, so are not enabled by default
          self.add(Application("Run Client (IJ Native, Deprecated)", project).apply {
            mainClass = "GradleStart"
            moduleName = project.name + ".ideVirtualMain"
            afterEvaluate {
              val runClient = tasks.runClient.get()
              workingDirectory = runClient.workingDir.absolutePath
              programParameters = runClient.calculateArgs(project).map { '"' + it + '"' }.joinToString(" ")
              jvmArgs = runClient.calculateJvmArgs(project).map { '"' + it + '"' }.joinToString(" ") +
                ' ' + runClient.systemProperties.map { "\"-D" + it.key + '=' + it.value.toString() + '"' }
                .joinToString(" ")
            }
          })
          self.add(Application("Run Server (IJ Native, Deprecated)", project).apply {
            mainClass = "GradleStartServer"
            moduleName = project.name + ".ideVirtualMain"
            afterEvaluate {
              val runServer = tasks.runServer.get()
              workingDirectory = runServer.workingDir.absolutePath
              programParameters = runServer.calculateArgs(project).map { '"' + it + '"' }.joinToString(" ")
              jvmArgs = runServer.calculateJvmArgs(project).map { '"' + it + '"' }.joinToString(" ") +
                ' ' + runServer.systemProperties.map { "\"-D" + it.key + '=' + it.value.toString() + '"' }
                .joinToString(" ")
            }
          })
          */
        }
        "compiler" {
          val self = this.delegate as org.jetbrains.gradle.ext.IdeaCompilerConfiguration
          afterEvaluate {
            self.javac.moduleJavacAdditionalOptions = mapOf(
              (project.name + ".main") to
                tasks.compileJava.get().options.compilerArgs.map { '"' + it + '"' }.joinToString(" ")
            )
          }
        }
      }
    }
  }
}

tasks.processIdeaSettings.configure {
  dependsOn(tasks.injectTags)
}
