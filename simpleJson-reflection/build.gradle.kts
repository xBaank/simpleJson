plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka") version "1.7.20"
    id("publish-simpleJson")
}

version = "2.0.0"

val kotlin_version: String by project
val arrow_version: String by project
val okio_version: String by project


//repositories
repositories {
    mavenCentral()
}

kotlin {
    jvm()
    sourceSets {
        val commonMain by getting {
            dependencies {
                //okio
                implementation("com.squareup.okio:okio:$okio_version")
                implementation("io.arrow-kt:arrow-core:$arrow_version")
                implementation(kotlin("reflect:$kotlin_version"))
                implementation(project(":simpleJson-core"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

