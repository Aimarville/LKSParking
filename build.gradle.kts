// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.sonar)
}

sonar {
    properties {
        property("sonar.projektKey", "Aimarville_LKSParking")
        property("sonar.organization", "aimarville")
        property("sonar.host.url", "https://sonarcloud.io")

        property("sonar.sources", "app/src/main/java,app/src/main/kotlin")
        property("sonar.binaries", "app/build/intermediates/javac/debug/classes,app/build/tmp/kotlin-classes/debug")
        property("sonar.java.binaries", "app/build/intermediates/javac/debug/classes")
        property("sonar.junit.reportPaths", "app/build/test-results/testDebugUnitTest")
    }
}