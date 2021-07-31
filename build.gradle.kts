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
val archivesBaseName: String by project
val isRelease = !version.toString().endsWith("-SNAPSHOT")

java.toolchain.languageVersion.set(JavaLanguageVersion.of(16))

minecraft {
    val mappingsVersion: String by project
    mappings("official", mappingsVersion)

    runs {
        create("client") {
            workingDirectory(project.file("run"))

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
            workingDirectory(project.file("run"))

            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "DEBUG")

            mods {
                create(modid) {
                    source(sourceSets.main.get())
                }
            }
        }

        create("data") {
            workingDirectory(project.file("run"))

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

sourceSets.main {
    resources.srcDir("src/generated/resources")
}

repositories {
    mavenCentral()
    maven("https://maven.minecraftforge.net")
    maven("https://maven.masterzach32.net/artifactory/minecraft/")
    maven("https://thedarkcolour.github.io/KotlinForForge/")
}

dependencies {
    val mcVersion: String by project
    val forgeVersion: String by project
    minecraft("net.minecraftforge:forge:$mcVersion-$forgeVersion")

    implementation(fg.deobf("com.spicymemes:spicycore-1.17.1:2.1.1-SNAPSHOT"))
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "16"
            freeCompilerArgs = listOf("-Xopt-in=kotlin.contracts.ExperimentalContracts")
        }
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "16"
    }

    jar {
        archiveBaseName.set(archivesBaseName)
        manifest {
            attributes(
                "Specification-Title"     to modid,
                "Specification-Vendor"    to "${modid}sareus",
                "Specification-Version"   to "1", // We are version 1 of ourselves
                "Implementation-Title"    to project.name,
                "Implementation-Version"  to archiveVersion,
                "Implementation-Vendor"   to "${modid}sareus",
                "Implementation-Timestamp" to LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
            )
        }

        finalizedBy("reobfJar")
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveBaseName.set(archivesBaseName)
    archiveClassifier.set("sources")
    from(project.sourceSets["main"].allSource)
}

tasks.assemble {
    dependsOn(sourcesJar)
}

publishing {
    publications {
        create<MavenPublication>("minecraft") {
            artifact(tasks.jar)
            artifact(sourcesJar)
            artifactId = archivesBaseName
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
