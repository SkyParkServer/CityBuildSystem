import groovy.json.JsonSlurper
import java.net.URI

plugins {
    java
    alias(libs.plugins.freefair.lombok)
    alias(libs.plugins.run.paper)
}

group = "de.skypark"
version = "1.0.6-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withSourcesJar()
}

dependencies {
    compileOnly(libs.paper.api)
    compileOnly(libs.luckperms.api)
    compileOnly(libs.vault.api)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.processResources {
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand("version" to project.version)
    }
}

tasks.runServer {
    minecraftVersion(libs.versions.minecraft.get())
    runDirectory.set(layout.projectDirectory.dir("server"))
    jvmArgs("-Dcom.mojang.eula.agree=true")
    dependsOn("prepareServerPlugins")
}

val prepareServerPlugins by tasks.registering {
    group = "Run Paper"
    description = "Downloads required test plugins into server/plugins on first setup."

    doLast {
        val pluginsDir = layout.projectDirectory.dir("server/plugins").asFile
        pluginsDir.mkdirs()

        fun downloadIfMissing(fileName: String, url: String) {
            val target = pluginsDir.resolve(fileName)
            if (target.exists()) {
                logger.lifecycle("Plugin already present: ${target.name}")
                return
            }

            logger.lifecycle("Downloading ${target.name}...")
            URI.create(url).toURL().openStream().use { input ->
                target.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            logger.lifecycle("Downloaded ${target.name}")
        }

        fun modrinthVersionUrl(slug: String): String {
            val requested = libs.versions.minecraft.get()
            val fallbackVersions = listOf(requested, "1.21.10", "1.21.9", "1.21.8", "1.21.7", "1.21.6", "1.21.5", "1.21.4", "1.21.3", "1.21.2", "1.21.1", "1.21")

            for (gameVersion in fallbackVersions.distinct()) {
                val url = "https://api.modrinth.com/v2/project/$slug/version?loaders=%5B%22paper%22%5D&game_versions=%5B%22$gameVersion%22%5D"
                val response = URI.create(url).toURL().readText()
                val versions = JsonSlurper().parseText(response) as List<*>
                if (versions.isEmpty()) {
                    continue
                }

                val version = versions.first() as Map<*, *>
                val files = version["files"] as List<*>
                if (files.isEmpty()) {
                    continue
                }

                val primary = files.firstOrNull {
                    (it as? Map<*, *>)?.get("primary") == true
                } as? Map<*, *>
                val chosen = primary ?: files.first() as Map<*, *>
                logger.lifecycle("Using Modrinth $slug version for Minecraft $gameVersion")
                return chosen["url"] as String
            }

            error("Keine Modrinth-Version fuer '$slug' gefunden (inkl. Fallback-Versionen).")
        }

        downloadIfMissing("LuckPerms.jar", modrinthVersionUrl("luckperms"))
        downloadIfMissing("Vault.jar", "https://github.com/MilkBowl/Vault/releases/download/1.7.3/Vault.jar")
        downloadIfMissing("FastAsyncWorldEdit.jar", modrinthVersionUrl("fastasyncworldedit"))
        downloadIfMissing("Multiverse-Core.jar", modrinthVersionUrl("multiverse-core"))

        val paidPluginNotice = pluginsDir.resolve("PLACE_PLOTSQUARED_HERE.txt")
        if (!paidPluginNotice.exists()) {
            paidPluginNotice.writeText(
                "PlotSquared ist ein Paid-Plugin.\n" +
                    "Bitte die PlotSquared-v7-JAR manuell in diesen Ordner legen: server/plugins\n"
            )
        }
    }
}
