plugins {
    // Apply the Android Application plugin to build Android apps
    alias(libs.plugins.android.application)

    // Apply the Kotlin Android plugin for Kotlin support in the project
    alias(libs.plugins.kotlin.android)

    // Apply Google Services plugin for Firebase and other Google services integration
    id("com.google.gms.google-services")
}

android {
    // The namespace uniquely identifies the app within Android
    namespace = "com.example.studyhelpermdapp"
    compileSdk = 35 // Target SDK version: 35 (latest supported API level)

    defaultConfig {
        applicationId = "com.example.studyhelpermdapp" // Unique identifier for the app
        minSdk = 24     // Minimum Android version that the app supports
        targetSdk = 35  // Android version the app targets
        versionCode = 1 // Internal version number (increment for updates)
        versionName = "1.0" // User-visible version string

        // Test instrumentation runner for Android UI tests
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // Disable code minification for release builds
            isMinifyEnabled = false

            // ProGuard rules for optimizing and obfuscating the code
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        // Java compatibility options
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        // Target JVM version for Kotlin
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true // Enable Jetpack Compose for modern UI development
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3" // Compatible version for Kotlin 1.9.10
    }
}

dependencies {
    // Core Android and Material libraries
    implementation(libs.androidx.core.ktx) // Kotlin extensions for Android
    implementation(libs.material) // Material Design library

    // Kotlin standard library with a specific version
    implementation(kotlin("stdlib", version = "1.9.10"))

    // Jetpack Compose BOM (Bill of Materials) to manage Compose versions
    implementation(platform(libs.composeBom))
    implementation("androidx.compose.material3:material3:1.2.1") // Material 3 library for Compose
    implementation(libs.androidx.compose.runtime) // Compose runtime for managing state
    implementation(libs.androidx.compose.ui.tooling.preview) // Tooling support for previews
    implementation(libs.androidx.lifecycle.runtime.compose) // Lifecycle integration for Compose
    implementation("androidx.compose.foundation:foundation:1.5.3") // Compose foundation library
    implementation("androidx.activity:activity-compose:1.7.2") // Integration with Activity
    implementation("androidx.compose.material:material-icons-extended:1.5.3") // Extended Material icons for Compose

    // Google Places API for location-based services
    implementation("com.google.android.libraries.places:places:2.6.0")

    // Firebase dependencies using the BOM (manages compatible versions)
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-auth-ktx") // Firebase Authentication
    implementation("com.google.firebase:firebase-analytics") // Firebase Analytics
    implementation("com.google.firebase:firebase-database-ktx") // Firebase Realtime Database

    // Google Maps dependencies
    implementation("com.google.maps.android:maps-compose:2.11.1") // Compose support for Google Maps
    implementation("com.google.android.gms:play-services-maps:18.0.2") // Google Maps services
    implementation("com.google.android.gms:play-services-location:21.0.1") // Location services

    // Testing libraries
    testImplementation(libs.junit) // Unit testing with JUnit
    androidTestImplementation(libs.androidx.junit) // AndroidX test support for JUnit
    androidTestImplementation(libs.androidx.espresso.core) // Espresso for UI testing

    // Debug dependencies for Compose tools
    debugImplementation(libs.androidx.compose.ui.tooling) // Debug UI tools for Compose
    debugImplementation(libs.androidx.compose.ui.test.manifest) // Manifest file for UI testing
}
