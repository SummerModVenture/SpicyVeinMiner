import java.time.*
import java.time.format.*

plugins {
    kotlin("jvm") version "1.5.21"
    id("net.minecraftforge.gradle") version "5.1.+"
    id("net.researchgate.release") version "2.8.1"
    `maven-publish`
    signing
}

val modid: String by project
val modName: String by project
val archivesBaseName: String by project
val isRelease = !version.toString().endsWith("-SNAPSHOT")

java.toolchain.languageVersion.set(JavaLanguageVersion.of(16))

minecraft {
    val mappingsVersion: String by project
    mappings("official", mappingsVersion)

    runs {
        create("client") {
            workingDirectory(project.file("run/client"))

            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "DEBUG")

            val minecraftUUID: String? by project
            if (minecraftUUID != null)
                args("--uuid", minecraftUUID)
            val minecraftUsername: String? by project
            if (minecraftUsername != null)
                args("--username", minecraftUsername)
            val minecraftAccessToken: String? by project
            if (minecraftAccessToken != null)
                args("--accessToken", minecraftAccessToken)

            mods {
                create(modid) {
                    source(sourceSets.main.get())
                }
            }
        }

        create("server") {
            workingDirectory(project.file("run/server"))

            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "DEBUG")

            mods {
                create(modid) {
                    source(sourceSets.main.get())
                }
            }
        }

        create("data") {
            workingDirectory(project.file("run/data"))

            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "DEBUG")

            args("--mod", modid, "--all", "--output", file("src/generated/resources/"), "--existing", file("src/main/resources/"))

            mods {
                create(modid) {
                    source(sourceSets.main.get())
                }
            }
        }
    }
}

sourceSets {
    main {
        resources.srcDir("src/generated/resources")
    }
//
//    create("api")
}

kotlin {
    sourceSets {
        main {
            kotlin.srcDir("src/main/generated")
        }

//        create("api") {
//            kotlin.srcDir("src/api/kotlin")
//        }
    }
}

repositories {
    mavenCentral()
    maven("https://maven.minecraftforge.net")
    maven("https://maven.masterzach32.net/artifactory/minecraft/")
    maven("https://thedarkcolour.github.io/KotlinForForge/")
    mavenLocal() // needed for local library-loading fix
}

dependencies {
    val mcVersion: String by project
    val forgeVersion: String by project
    minecraft("net.minecraftforge:forge:1.17.1-36.1.90-fix-1.17.x-library-loading")

    implementation("com.spicymemes:spicycore-1.17.1:2.1.1-SNAPSHOT")
}

tasks {
    val generateModInfo by registering {
        description = "Generates the ModInfo.kt source file."
        doLast {
            mkdir("src/main/generated")
            file("src/main/generated/ModInfo.kt").writeText("""
                package com.spicymemes.veinminer

                const val MOD_ID = "$modid"
                const val MOD_NAME = "$modName"
                const val MOD_VERSION = "$version"
            """.trimIndent())
        }
    }

    compileKotlin {
        dependsOn(generateModInfo)
        kotlinOptions {
            jvmTarget = "16"
            freeCompilerArgs = listOf("-Xopt-in=kotlin.contracts.ExperimentalContracts")
        }
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "16"
    }
}

tasks.jar {
    archiveBaseName.set(archivesBaseName)
    manifest()
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveBaseName.set(archivesBaseName)
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val modJar by tasks.registering(Jar::class) {
    archiveBaseName.set(archivesBaseName)
    archiveClassifier.set("obf")
    from(sourceSets.main.get().output)
    manifest()
    finalizedBy("reobfJar")
}

//val apiJar by tasks.registering(Jar::class) {
//    archiveBaseName.set(archivesBaseName)
//    from(sourceSets["api"].output)
//    manifest()
//}
//
//val apiSourcesJar by tasks.registering(Jar::class) {
//    archiveBaseName.set(archivesBaseName)
//    archiveClassifier.set("sources")
//    from(sourceSets["api"].allSource)
//    manifest()
//}

tasks.assemble {
    dependsOn(modJar, sourcesJar)
//    dependsOn(apiJar, apiSourcesJar)
}

publishing {
    publications {
        create<MavenPublication>("minecraft") {
            artifactId = archivesBaseName
            artifact(tasks.jar)
            artifact(modJar)
            artifact(sourcesJar)
        }

//        create<MavenPublication>("api") {
//            artifactId = "$archivesBaseName-api"
//            artifact(apiJar)
//            artifact(apiSourcesJar)
//        }
    }

    repositories {
        val mavenUsername: String? by project
        val mavenPassword: String? by project
        if (mavenUsername != null && mavenPassword != null) {
            maven {
                if (isRelease) {
                    name = "Releases"
                    url = uri("https://maven.masterzach32.net/artifactory/minecraft-releases/")
                } else {
                    name = "Snapshots"
                    url = uri("https://maven.masterzach32.net/artifactory/minecraft-snapshots/")
                }
                credentials {
                    username = mavenUsername
                    password = mavenPassword
                }
            }
        }
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["minecraft"])
}

tasks.withType<Sign>().configureEach {
    onlyIf { isRelease }
}

release {
    preTagCommitMessage = "Release version"
    tagCommitMessage = "Release version"
    newVersionCommitMessage = "Next development version"
}

fun Jar.manifest() {
    manifest {
        attributes(
            "Specification-Title"     to modid,
            "Specification-Vendor"    to "Forge",
            "Specification-Version"   to "1", // We are version 1 of ourselves
            "Implementation-Title"    to project.name,
            "Implementation-Version"  to archiveVersion,
            "Implementation-Vendor"   to "spicymemes",
            "Implementation-Timestamp" to LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        )
    }
}
