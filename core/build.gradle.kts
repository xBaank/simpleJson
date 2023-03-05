plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka") version "1.7.20"
    id("publish-simpleJson") apply true
}

val arrow_version: String by project
val okio_version: String by project

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
}
