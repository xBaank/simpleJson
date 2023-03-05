plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka") version "1.7.20"
    `maven-publish`

}

val arrow_version: String by project
val okio_version: String by project

group = "org.bank"
version = "9.0.0"

//repositories
repositories {
    mavenCentral()
}

kotlin {
    jvm()
    js(IR)
    macosX64()
    linuxX64()
    mingwX64()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.arrow-kt:arrow-core:$arrow_version")
                implementation("com.squareup.okio:okio:$okio_version")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
    val publicationsFromMainHost =
        listOf(jvm(), js(), mingwX64(), linuxX64(), macosX64()).map { it.name } + "kotlinMultiplatform"

    publishing {
        publications {
            matching { it.name in publicationsFromMainHost }.all {
                val targetPublication = this@all
                tasks.withType<PublishToMavenLocal>()
                    .matching { it.publication == targetPublication }
                    .configureEach { onlyIf { findProperty("isMainHost") == "true" } }
            }
        }
    }
}

