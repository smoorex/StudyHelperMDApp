package com.example.studyhelpermdapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EnhancedSettingsScreen(
                onSave = { /* Handle Save Changes */ },
                onReset = { /* Handle Reset Settings */ },
                onLogout = { /* Handle Logout */ }
            )
        }
    }
}

@Composable
fun EnhancedSettingsScreen(onSave: () -> Unit, onReset: () -> Unit, onLogout: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
            Text("Save Changes")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onReset, modifier = Modifier.fillMaxWidth()) {
            Text("Reset Settings")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
            Text("Logout")
        }
    }
}
