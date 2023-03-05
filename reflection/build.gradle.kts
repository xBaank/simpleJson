plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka") version "1.7.20"
    `maven-publish`
}

val kotlin_version: String by project
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
    sourceSets {
        val commonMain by getting {
            dependencies {
                //okio
                implementation("com.squareup.okio:okio:$okio_version")
                implementation("io.arrow-kt:arrow-core:$arrow_version")
                implementation(kotlin("reflect:$kotlin_version"))
                implementation(project(":core"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

//publishing
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
        }
    }
}


