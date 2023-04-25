plugins {
    kotlin("multiplatform")
    id("publish-simpleJson")
}

version = "2.1.2"

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
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
