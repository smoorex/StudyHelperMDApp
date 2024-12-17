

/**
 * plugins block:
 * This is where we define the Gradle plugins that can be applied to all sub-projects/modules.
 * Each plugin serves a specific purpose, like building Android apps, enabling Kotlin features,
 * or integrating Google services (e.g., Firebase).
 */
plugins {
    // Android Application Plugin
    // This plugin is needed to build Android applications.
    alias(libs.plugins.android.application) apply false

    // Kotlin Android Plugin
    // This plugin enables Kotlin support for Android development.
    alias(libs.plugins.kotlin.android) apply false

    // Google Services Plugin
    // Required for integrating Google services like Firebase into your project.
    // Version "4.4.2" is used here.
    id("com.google.gms.google-services") version "4.4.2" apply false
}
