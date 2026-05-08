// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.sonar)
}

sonar {
    isSkipProject = false
    properties {
        property("sonar.projectKey", "TU_PROJECT_KEY")
        property("sonar.organization", "TU_ORGANIZATION_KEY")
        property("sonar.host.url", "https://sonarcloud.io")

        property("sonar.sources", "app/src/main/java,app/src/main/kotlin")
        property("sonar.binaries", "app/build/intermediates/javac/debug/classes,app/build/tmp/kotlin-classes/debug")
        property("sonar.java.binaries", "app/build/intermediates/javac/debug/classes")
        property("sonar.modules", "app")
    }
}