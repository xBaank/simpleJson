rootProject.name = "simpleJson"
include(":simpleJson-core")

pluginManagement {
    val kotlin_version: String by settings
    plugins {
        kotlin("multiplatform") version kotlin_version
    }
}



