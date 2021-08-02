import org.jetbrains.kotlin.gradle.dsl.*
import java.time.*
import java.time.format.*

plugins {
    kotlin("jvm") version "1.5.21"
    id("net.minecraftforge.gradle") version "5.1.+"
    id("net.researchgate.release") version "2.8.1"
    `maven-publish`
    signing
}

val modId: String by project
val modName: String by project
val archivesBaseName: String by project
val isRelease = !version.toString().endsWith("-SNAPSHOT")

val apiSourceSet = sourceSets.create("api")
sourceSets {
    main {
        compileClasspath += apiSourceSet.output
        runtimeClasspath += apiSourceSet.output
        resources.srcDir("src/generated/resources")
    }
}

val library: Configuration by configurations.creating
configurations {
    get(apiSourceSet.implementationConfigurationName).extendsFrom(implementation.get())
    get(apiSourceSet.runtimeOnlyConfigurationName).extendsFrom(runtimeOnly.get())

    implementation {
        extendsFrom(library)
    }
}

kotlin.sourceSets.main {
    kotlin.srcDir("src/main/generated")
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(16))
configureKotlinJvmOptions(jvmTarget = "16")

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
                create(modId) {
                    sources(sourceSets.main.get(), apiSourceSet)
                }
            }
        }

        create("server") {
            workingDirectory(project.file("run/server"))

            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "DEBUG")

            mods {
                create(modId) {
                    sources(sourceSets.main.get(), apiSourceSet)
                }
            }
        }

        create("data") {
            workingDirectory(project.file("run/data"))

            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "DEBUG")

            args("--mod", modId, "--all", "--output", file("src/generated/resources/"), "--existing", file("src/main/resources/"))

            mods {
                create(modId) {
                    sources(sourceSets.main.get(), apiSourceSet)
                }
            }
        }
    }
}

repositories {
    mavenCentral()
    maven("https://maven.minecraftforge.net")
    maven("https://maven.masterzach32.net/artifactory/minecraft/")
    maven("https://thedarkcolour.github.io/KotlinForForge/")
//    mavenLocal() // needed for local library-loading fix
}

// temporary fix for missing libs
minecraft.runs.all {
    lazyToken("minecraft_classpath") {
        library.copyRecursive().resolve().joinToString(File.pathSeparator) { it.absolutePath }
    }
}

dependencies {
    val mcVersion: String by project
    val forgeVersion: String by project
    minecraft("net.minecraftforge:forge:$mcVersion-$forgeVersion")

    compileOnly(fg.deobf("com.spicymemes:spicycore-1.17.1:2.1.1-SNAPSHOT:api"))
    runtimeOnly(fg.deobf("com.spicymemes:spicycore-1.17.1:2.1.1-SNAPSHOT"))

    library(kotlin("stdlib"))
}

val generateModInfo by tasks.registering {
    description = "Generates the ModInfo.kt source file."
    doLast {
        mkdir("src/main/generated")
        file("src/main/generated/ModInfo.kt").writeText("""
            package com.spicymemes.veinminer

            const val MOD_ID = "$modId"
            const val MOD_NAME = "$modName"
            const val MOD_VERSION = "$version"
        """.trimIndent())
    }
}

tasks.compileKotlin {
    dependsOn(generateModInfo)
    kotlinOptions {
        freeCompilerArgs = listOf("-Xopt-in=kotlin.contracts.ExperimentalContracts")
    }
}

val updateModsToml by tasks.registering(Copy::class) {
    outputs.upToDateWhen { false }

    val mcVersionRange: String by project
    val forgeVersionRange: String by project
    val loaderVersionRange: String by project
    val spicyCoreVersionRange: String by project
    from(sourceSets.main.get().resources) {
        include("META-INF/mods.toml")
        expand(
            "modName" to modName,
            "version" to version,
            "mcVersionRange" to mcVersionRange,
            "forgeVersionRange" to forgeVersionRange,
            "loaderVersionRange" to loaderVersionRange,
            "spicyCoreVersionRange" to spicyCoreVersionRange
        )
    }
    into("$buildDir/resources/main")
}

tasks.processResources {
    exclude("META-INF/mods.toml")
    finalizedBy(updateModsToml)
}

tasks.classes {
    dependsOn(updateModsToml)
}

tasks.jar {
    archiveBaseName.set(archivesBaseName)
    from(sourceSets.main.get().output)
    from(apiSourceSet.output)
    manifest()
    finalizedBy("reobfJar")
}

val apiJar by tasks.registering(Jar::class) {
    archiveBaseName.set(archivesBaseName)
    archiveClassifier.set("api")
    from(apiSourceSet.output)
    manifest()
    finalizedBy("reobfApiJar")
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveBaseName.set(archivesBaseName)
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
    from(apiSourceSet.allSource)
}

val deobfJar by tasks.registering(Jar::class) {
    archiveBaseName.set(archivesBaseName)
    archiveClassifier.set("deobf")
    from(sourceSets.main.get().output)
    from(apiSourceSet.output)
    manifest()
}

tasks.assemble {
    dependsOn(apiJar, sourcesJar, deobfJar)
}

reobf {
    create("apiJar") {
        classpath.from(sourceSets["api"].compileClasspath)
    }
    create("jar") {
        classpath.from(sourceSets.main.get().compileClasspath)
    }
}

publishing {
    publications {
        create<MavenPublication>("minecraft") {
            artifactId = archivesBaseName
            artifact(tasks.jar)
            artifact(apiJar)
            artifact(sourcesJar)
            artifact(deobfJar)
        }
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
            "Specification-Title"     to modName,
            "Specification-Vendor"    to "spicymemes",
            "Specification-Version"   to "1", // We are version 1 of ourselves
            "Implementation-Title"    to project.name,
            "Implementation-Version"  to project.version,
            "Implementation-Vendor"   to "spicymemes",
            "Implementation-Timestamp" to LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        )
    }
}

fun configureKotlinJvmOptions(jvmTarget: String) {
    tasks.compileKotlin {
        kotlinOptions.jvmTarget = jvmTarget
    }

    val compileApiKotlin by tasks.existing(KotlinJvmCompile::class) {
        kotlinOptions.jvmTarget = jvmTarget
    }

    tasks.compileTestKotlin {
        kotlinOptions.jvmTarget = jvmTarget
    }
}
