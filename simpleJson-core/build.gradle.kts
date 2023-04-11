plugins {
    kotlin("multiplatform")
    id("publish-simpleJson")
}

version = "2.0.0"

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
    watchos()
    ios()
    tvos()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.arrow-kt:arrow-core:$arrow_version")
                implementation("com.squareup.okio:okio:$okio_version")
                implementation("io.github.reactivecircus.cache4k:cache4k:0.9.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
