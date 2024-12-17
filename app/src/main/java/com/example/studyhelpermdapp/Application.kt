package com.example.studyhelpermdapp

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

/**
 * MyApplication:
 * This class serves as the custom Application class for the app.
 * It is used to initialize Firebase when the app is launched.
 *
 * Note: The class must be declared in the AndroidManifest.xml file under the <application> tag.
 */
class MyApplication : Application() {

    /**
     * onCreate:
     * This method is called when the application is first created.
     * It initializes Firebase and handles any potential initialization errors.
     */
    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase App
        try {
            FirebaseApp.initializeApp(this) // Initialize the Firebase instance
            Log.d("MyApplication", "Firebase initialized successfully")
        } catch (e: Exception) {
            // Log an error message if Firebase fails to initialize
            Log.e("MyApplication", "Failed to initialize Firebase: ${e.message}")
        }
    }
}
