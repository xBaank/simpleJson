import java.util.*

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka") version "1.7.20"
    `maven-publish`
    signing
}

val arrow_version: String by project
val okio_version: String by project
val usr: String by project
val pwd: String by project


//repositories
repositories {
    gradlePluginPortal()
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
                tasks.withType<AbstractPublishToMaven>()
                    .matching { it.publication == targetPublication }
                    .configureEach { onlyIf { findProperty("isMainHost") == "true" } }
            }
        }
    }
}




ext["signing.keyId"] = null
ext["signing.password"] = null
ext["signing.secretKeyRingFile"] = null
ext["ossrhUsername"] = null
ext["ossrhPassword"] = null

val secretPropsFile = project.rootProject.file("local.properties")
if (secretPropsFile.exists()) {
    secretPropsFile.reader().use {
        Properties().apply {
            load(it)
        }
    }.onEach { (name, value) ->
        ext[name.toString()] = value
    }
} else {
    ext["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
    ext["signing.password"] = System.getenv("SIGNING_PASSWORD")
    ext["signing.secretKeyRingFile"] = System.getenv("SIGNING_SECRET_KEY_RING_FILE")
    ext["ossrhUsername"] = System.getenv("OSSRH_USERNAME")
    ext["ossrhPassword"] = System.getenv("OSSRH_PASSWORD")
}

fun getExtraString(name: String) = ext[name]?.toString()
/*val javadoc = tasks.register("javadoc", Javadoc::class.java) {
    archivesName.set("javadoc")
}*/
publishing {
    repositories {
        maven {
            credentials {
                username = getExtraString("ossrhUsername")
                password = getExtraString("ossrhPassword")
            }

            val snapshotUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            val releaseUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            url = snapshotUrl
        }
    }

    publications.withType<MavenPublication> {

        // Add javadoc to the publication
        //artifact(javadoc)

        // Provide artifacts information requited by Maven Central
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
}

signing {
    sign(publishing.publications)
}

