package com.example.studyhelpermdapp

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase App
        try {
            FirebaseApp.initializeApp(this)
            Log.d("MyApplication", "Firebase initialized successfully")
        } catch (e: Exception) {
            Log.e("MyApplication", "Firebase initialization failed", e)
        }
    }
}
