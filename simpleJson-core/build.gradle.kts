plugins {
    kotlin("multiplatform")
    id("publish-simpleJson")
}

version = "2.1.1"

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
        commonMain {
            dependencies {
                implementation("io.arrow-kt:arrow-core:$arrow_version")
                implementation("com.squareup.okio:okio:$okio_version")
                implementation("io.github.reactivecircus.cache4k:cache4k:0.10.0")
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
