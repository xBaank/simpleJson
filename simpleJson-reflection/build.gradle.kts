plugins {
    kotlin("multiplatform")
    id("publish-simpleJson")
}

version = "3.0.0"

val kotlin_version: String by project
val arrow_version: String by project
val okio_version: String by project


//repositories
repositories {
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = JavaVersion.VERSION_1_8.toString()
            }
            java {
                sourceCompatibility = JavaVersion.VERSION_1_8
                targetCompatibility = JavaVersion.VERSION_1_8
            }
        }
    }
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

