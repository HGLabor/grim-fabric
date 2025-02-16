import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.spotless)
    alias(libs.plugins.nexusPublish)
    alias(libs.plugins.fabricLoom)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.lombok)
    `maven-publish`
}

defaultTasks("clean", "build")

allprojects {
    group = "ac.grim"
    description = "Grim"

    apply(plugin = "fabric-loom")

    dependencies {
        "minecraft"(rootProject.libs.minecraft)
        "mappings"(variantOf(rootProject.libs.yarn.mappings) { classifier("v2") })
    }

    loom {
        runConfigs.configureEach {
            this.ideConfigGenerated(true)
        }
    }

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://nexus.scarsz.me/content/groups/public/")
        maven("https://repo.viaversion.com") // ViaVersion
        maven("https://repo.codemc.io/repository/maven-releases/") // PacketEvents
        maven("https://repo.codemc.io/repository/maven-snapshots/")

        // more stable replacement for jitpack
        maven("https://repository.derklaro.dev/releases/") {
            mavenContent {
                releasesOnly()
            }
        }
        maven("https://repository.derklaro.dev/snapshots/") {
            mavenContent {
                snapshotsOnly()
            }
        }

        exclusiveContent {
            forRepository {
                maven("https://api.modrinth.com/maven")
            }
            filter {
                includeGroup("maven.modrinth")
            }
        }

        // packetevents
        maven("https://repo.codemc.io/repository/maven-releases/") {
            mavenContent {
                includeGroup("com.github.retrooper")
            }
        }
    }
}

subprojects {
    // apply all plugins only to subprojects
    apply(plugin = "signing")
    //apply(plugin = "checkstyle")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "kotlin")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "io.freefair.lombok")

    //ich weiß das ist kriminell aber
    version = rootProject.libs.versions.minecraft.get() + "-" + when (name) {
        "grim-api" -> "2.3.71"
        "grim-mod" -> "2.3.71"
        else -> version
    }

    dependencies {
        "compileOnly"(rootProject.libs.annotations)
        "implementation"(rootProject.libs.serialization)
    }

    configurations.all {
        // unsure why but every project loves them, and they literally have an import for every letter I type - beware
        exclude("org.checkerframework", "checker-qual")
    }

    tasks.withType<Jar> {
        from(rootProject.file("license.txt"))
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            freeCompilerArgs =
                listOf("-Xcontext-receivers", "-Xjvm-default=all", "-Xopt-in=kotlin.RequiresOptIn")
        }
    }

    tasks.processResources {
        val properties = mapOf("version" to project.version)
        inputs.properties(properties)
        filesMatching("fabric.mod.json") { expand(properties) }
    }

    tasks.withType<JavaCompile>().configureEach {
        // options
        options.release.set(21)
        options.encoding = "UTF-8"
        options.isIncremental = true
        // we are aware that those are there, but we only do that if there is no other way we can use - so please keep the terminal clean!
        options.compilerArgs = mutableListOf("-Xlint:-deprecation,-unchecked")
    }

    extensions.configure<JavaPluginExtension> {
        disableAutoTargetJvm()
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

    /*tasks.withType<Checkstyle> {
      maxErrors = 0
      maxWarnings = 0
      configFile = rootProject.file("checkstyle.xml")
    }

    extensions.configure<CheckstyleExtension> {
      toolVersion = rootProject.libs.versions.checkstyleTools.get()
    }

    extensions.configure<SpotlessExtension> {
      java {
        licenseHeaderFile(rootProject.file("license_header.txt"))
      }
    }*/

    tasks.withType<Javadoc> {
        val options = options as? StandardJavadocDocletOptions ?: return@withType

        // options
        options.encoding = "UTF-8"
        options.memberLevel = JavadocMemberLevel.PRIVATE
        options.addStringOption("-html5")
        options.addBooleanOption("Xdoclint:-missing", true)
    }

    tasks.register<org.gradle.jvm.tasks.Jar>("javadocJar") {
        archiveClassifier.set("javadoc")
        from(tasks.getByName("javadoc"))
    }

    val sourceJar = tasks.register<org.gradle.jvm.tasks.Jar>("sourcesJar") {
        archiveClassifier.set("sources")
        from(project.the<SourceSetContainer>()["main"].allJava)
    }

    tasks.withType<PublishToMavenRepository>().configureEach {
        val predicate = provider {
            (repository == publishing.repositories["production"] &&
                    publication == publishing.publications["binary"]) ||
                    (repository == publishing.repositories["dev"] &&
                            publication == publishing.publications["binaryAndSources"])
        }
        onlyIf("publishing binary to the production repository, or binary and sources to the internal dev one") {
            predicate.get()
        }
    }

    tasks.withType<PublishToMavenLocal>().configureEach {
        val predicate = provider {
            publication == publishing.publications["binaryAndSources"]
        }
        onlyIf("publishing binary and sources") {
            predicate.get()
        }
    }

    extensions.configure<PublishingExtension> {
        publications {
            create<MavenPublication>("binary") {
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()
                from(components["java"])
            }
            create<MavenPublication>("binaryAndSources") {
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()
                from(components["java"])
                artifact(sourceJar)
            }
        }
        repositories {
            fun MavenArtifactRepository.applyCredentials() = credentials {
                username =
                    (System.getenv("NORISK_NEXUS_USERNAME")
                        ?: project.findProperty("noriskMavenUsername")).toString()
                password =
                    (System.getenv("NORISK_NEXUS_PASSWORD")
                        ?: project.findProperty("noriskMavenPassword")).toString()
            }
            maven {
                name = "production"
                url = uri("https://maven.norisk.gg/repository/norisk-production/")
                applyCredentials()
            }
            maven {
                name = "dev"
                // this could also be a maven repo on the dev server
                // e.g. maven-staging.norisk.gg
                url = uri("https://maven.norisk.gg/repository/maven-releases/")
                applyCredentials()
            }
        }
    }
}
