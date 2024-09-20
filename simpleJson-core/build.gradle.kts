import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

object Meta {
    const val groupId = "io.github.xbaank"
    const val artifactId = "simpleJson-core"
    const val version = "3.0.3"
    const val name = "simpleJson"
    const val description = "simpleJson is a library for parsing and generating JSON in Kotlin Multiplatform"
    const val licenseName = "GNU General Public License v3.0"
    const val licenseUrl = "https://github.com/xBaank/simpleJson/blob/master/License"
    const val scmUrl = "https://github.com/xBaank/simpleJson"
    const val developerName = "xBaank"
    const val developerUrl = "https://github.com/xBaank"
}

plugins {
    kotlin("multiplatform")
    id("com.vanniktech.maven.publish") version "0.29.0"
}

version = Meta.version

val arrow_version: String by project
val okio_version: String by project

//repositories
repositories {
    mavenCentral()
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
        java.sourceCompatibility = JavaVersion.VERSION_1_8
        java.targetCompatibility = JavaVersion.VERSION_1_8
    }
}

kotlin {
    jvm()
    js {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        nodejs()
    }
    linuxX64()
    mingwX64()
    applyDefaultHierarchyTemplate()
    sourceSets {
        commonMain {
            dependencies {
                implementation("io.arrow-kt:arrow-core:$arrow_version")
                implementation("com.squareup.okio:okio:$okio_version")
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

mavenPublishing {
    coordinates(Meta.groupId, Meta.artifactId, Meta.version)
    publishToMavenCentral(SonatypeHost.S01, automaticRelease = true)
    signAllPublications()
    pom {
        name.set(Meta.name)
        description.set(Meta.description)
        url.set(Meta.scmUrl)

        licenses {
            license {
                name.set(Meta.licenseName)
                url.set(Meta.licenseUrl)
            }
        }
        scm {
            url.set(Meta.scmUrl)
        }
        developers {
            developer {
                id.set(Meta.developerName)
                name.set(Meta.developerName)
                url.set(Meta.developerUrl)
            }
        }
    }
}