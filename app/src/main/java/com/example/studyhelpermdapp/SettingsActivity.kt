package com.example.studyhelpermdapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

/**
 * SettingsActivity:
 * This activity provides options for the user to:
 * 1. Save changes
 * 2. Reset settings
 * 3. Logout from the app
 */
class SettingsActivity : ComponentActivity() {

    // Firebase Authentication instance
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up the UI using Jetpack Compose
        setContent {
            MaterialTheme {
                EnhancedSettingsScreen(
                    onSave = { handleSaveChanges() }, // Handle saving changes
                    onReset = { handleResetSettings() }, // Handle resetting settings
                    onLogout = {
                        // Log out the user
                        auth.signOut()
                        // Navigate back to the LoginActivity
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish() // Close the current activity
                    }
                )
            }
        }
    }

    /**
     * handleSaveChanges:
     * Function to handle saving changes in the settings.
     * Expand this function to include logic for saving preferences.
     */
    private fun handleSaveChanges() {
        // TODO: Add logic to save changes, like updating preferences or user settings
    }

    /**
     * handleResetSettings:
     * Function to handle resetting settings to their default values.
     * Expand this function to include logic for resetting app settings.
     */
    private fun handleResetSettings() {
        // TODO: Add logic to reset settings, like clearing preferences
    }
}

/**
 * EnhancedSettingsScreen:
 * A composable function that displays the settings screen UI.
 *
 * @param onSave A lambda function triggered when the user clicks "Save Changes".
 * @param onReset A lambda function triggered when the user clicks "Reset Settings".
 * @param onLogout A lambda function triggered when the user clicks "Logout".
 */
@Composable
fun EnhancedSettingsScreen(onSave: () -> Unit, onReset: () -> Unit, onLogout: () -> Unit) {
    // Column layout for the settings screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center, // Center content vertically
        horizontalAlignment = Alignment.CenterHorizontally // Center content horizontally
    ) {
        // Title of the settings screen
        Text(
            text = "Settings", // Title text
            style = MaterialTheme.typography.headlineMedium,
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Save Changes Button
        Button(
            onClick = onSave, // Call the onSave lambda when clicked
            modifier = Modifier.fillMaxWidth(), // Make the button take full width
            contentPadding = PaddingValues(16.dp) // Add padding inside the button
        ) {
            Icon(Icons.Default.Save, contentDescription = "Save Icon") // Save icon
            Spacer(modifier = Modifier.width(8.dp)) // Add spacing between icon and text
            Text("Save Changes", fontSize = 18.sp) // Button text
        }
        Spacer(modifier = Modifier.height(16.dp)) // Add space below the button

        // Reset Settings Button
        Button(
            onClick = onReset, // Call the onReset lambda when clicked
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary) // Use a secondary color
        ) {
            Icon(Icons.Default.Restore, contentDescription = "Reset Icon") // Reset icon
            Spacer(modifier = Modifier.width(8.dp))
            Text("Reset Settings", fontSize = 18.sp) // Button text
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Logout Button
        Button(
            onClick = onLogout, // Call the onLogout lambda when clicked
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error) // Use an error color (red)
        ) {
            Icon(Icons.Default.Logout, contentDescription = "Logout Icon") // Logout icon
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout", fontSize = 18.sp) // Button text
        }
    }
}
