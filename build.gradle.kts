import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.22"
    `maven-publish`
}

group = "org.bank"
version = "7.0.0"


repositories {
    mavenCentral()
}

dependencies {
    //arrow core
    implementation("io.arrow-kt:arrow-core:1.1.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
}

//publish
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}