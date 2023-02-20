import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
    id("org.jetbrains.dokka") version "1.7.20"
    `maven-publish`
}

group = "org.bank"
version = "9.0.0"


repositories {
    mavenCentral()
}

dependencies {
    //arrow core
    implementation("io.arrow-kt:arrow-core:1.1.5")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
    implementation(kotlin("reflect:1.8.0"))
    implementation(project(":core"))
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

tasks.withType<JavaCompile> {
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
}