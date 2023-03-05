plugins {
    `maven-publish`
    signing
}

val secretPropsFile = project.rootProject.file("local.properties")
if (secretPropsFile.exists()) {
    secretPropsFile.reader().use {

        java.util.Properties().apply {
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

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

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
        artifact(javadocJar.get())
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
    //useInMemoryPgpKeys(getExtraString("signing.keyId"), getExtraString("signing.password"), getExtraString("signing.secretKey"))
    sign(publishing.publications)
}