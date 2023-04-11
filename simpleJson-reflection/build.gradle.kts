plugins {
    kotlin("multiplatform")
    id("publish-simpleJson")
}

version = "2.0.2"

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

