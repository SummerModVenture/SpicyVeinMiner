import net.minecraftforge.gradle.common.util.*
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
    named(apiSourceSet.implementationConfigurationName) {
        extendsFrom(implementation.get())
    }
    named(apiSourceSet.runtimeOnlyConfigurationName) {
        extendsFrom(runtimeOnly.get())
    }
    implementation {
        extendsFrom(library)
    }
}

kotlin.sourceSets.main {
    kotlin.srcDir("src/main/generated")
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(16))
configureKotlin {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xopt-in=kotlin.contracts.ExperimentalContracts")
        jvmTarget = "16"
    }
}

minecraft {
    val mappingsVersion: String by project
    mappings("official", mappingsVersion)

    runs {
        val runConfig: RunConfig.() -> Unit = {
            workingDirectory(project.file("run/$name"))
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "DEBUG")

            mods {
                register(modId) {
                    sources(sourceSets.main.get(), apiSourceSet)
                }
            }
        }

        val client by registering(runConfig)
        register("server", runConfig)
        val data by registering(runConfig)

        client {
            val minecraftUUID: String? by project
            if (minecraftUUID != null)
                args("--uuid", minecraftUUID)
            val minecraftUsername: String? by project
            if (minecraftUsername != null)
                args("--username", minecraftUsername)
            val minecraftAccessToken: String? by project
            if (minecraftAccessToken != null)
                args("--accessToken", minecraftAccessToken)
        }

        data {
            args("--mod", modId, "--all", "--output", file("src/generated/resources/"), "--existing", file("src/main/resources/"))
        }
    }
}

// temporary fix for library-loading
minecraft.runs.all {
    lazyToken("minecraft_classpath") {
        library.copyRecursive().resolve().joinToString(File.pathSeparator) { it.absolutePath }
    }
}

repositories {
    mavenCentral()
    maven("https://maven.minecraftforge.net")
    maven("https://maven.masterzach32.net/artifactory/minecraft/")
}

dependencies {
    val mcVersion: String by project
    val forgeVersion: String by project
    minecraft("net.minecraftforge:forge:$mcVersion-$forgeVersion")

    val spicyCoreVersion: String by project
    compileOnly(fg.deobf("com.spicymemes:spicycore-1.17.1:$spicyCoreVersion:api"))
    runtimeOnly(fg.deobf("com.spicymemes:spicycore-1.17.1:$spicyCoreVersion"))

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
        """.trimIndent() + "\n")
    }
}

tasks.compileKotlin {
    dependsOn(generateModInfo)
    kotlinOptions {
        freeCompilerArgs = listOf("-Xopt-in=kotlin.contracts.ExperimentalContracts")
    }
}

tasks.commitNewVersion {
    dependsOn(generateModInfo)
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.FAIL
    val mcVersionRange: String by project
    val forgeVersionRange: String by project
    val loaderVersionRange: String by project
    val spicyCoreVersionRange: String by project
    val props = mapOf(
        "modName" to modName,
        "version" to project.version,
        "mcVersionRange" to mcVersionRange,
        "forgeVersionRange" to forgeVersionRange,
        "loaderVersionRange" to loaderVersionRange,
        "spicyCoreVersionRange" to spicyCoreVersionRange
    )
    inputs.properties(props)
    filesMatching("META-INF/mods.toml") {
        expand(props)
    }
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.FAIL
    archiveBaseName.set(archivesBaseName)
    from(sourceSets.main.get().output)
    from(apiSourceSet.output)
    manifest()
    finalizedBy("reobfJar")
}

val apiJar by tasks.registering(Jar::class) {
    duplicatesStrategy = DuplicatesStrategy.FAIL
    archiveBaseName.set(archivesBaseName)
    archiveClassifier.set("api")
    from(apiSourceSet.output)
    manifest()
    finalizedBy("reobfApiJar")
}

val sourcesJar by tasks.registering(Jar::class) {
    duplicatesStrategy = DuplicatesStrategy.FAIL
    archiveBaseName.set(archivesBaseName)
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
    from(apiSourceSet.allSource)
}

val deobfJar by tasks.registering(Jar::class) {
    duplicatesStrategy = DuplicatesStrategy.FAIL
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
        classpath.from(apiSourceSet.compileClasspath)
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

fun configureKotlin(config: KotlinJvmCompile.() -> Unit) {
    tasks.named("compileApiKotlin", KotlinJvmCompile::class, config)
    tasks.compileKotlin(config)
    tasks.compileTestKotlin(config)
}
