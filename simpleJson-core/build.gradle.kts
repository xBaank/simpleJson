import com.vanniktech.maven.publish.SonatypeHost

object Meta {
    const val groupId = "io.github.xbaank"
    const val artifactId = "simpleJson-core"
    const val version = "3.0.1-SNAPSHOT1"
}

plugins {
    kotlin("multiplatform")
    id("com.vanniktech.maven.publish") version "0.28.0"
}

version = Meta.version

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
    publishToMavenCentral(SonatypeHost.S01)
    signAllPublications()
    pom {
        name.set("simpleJson")
        description.set("simpleJson is a library for parsing and generating JSON in Kotlin Multiplatform")
        url.set("https://github.com/xBaank/simpleJson")

        licenses {
            license {
                name.set("GNU General Public License v3.0")
                url.set("https://github.com/xBaank/simpleJson/blob/master/License")
            }
        }
        scm {
            url.set("https://github.com/xBaank/simpleJson")
        }
    }
}