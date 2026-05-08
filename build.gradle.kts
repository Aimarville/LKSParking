// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.sonar) // Esto aplica el plugin de SonarQube
}

sonar {
    properties {
        property("sonar.projectKey", "Aimarville_LKSParking")
        property("sonar.organization", "aimarville")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.java.binaries", "**/classes")
    }
}