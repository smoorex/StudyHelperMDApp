package com.example.studyhelpermdapp

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

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                EnhancedSettingsScreen(
                    onSave = { /* Handle Save Changes */ },
                    onReset = { /* Handle Reset Settings */ },
                    onLogout = { /* Handle Logout */ }
                )
            }
        }
    }
}

@Composable
fun EnhancedSettingsScreen(onSave: () -> Unit, onReset: () -> Unit, onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Save Changes Button
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            Icon(Icons.Default.Save, contentDescription = "Save Icon")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save Changes", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Reset Settings Button
        Button(
            onClick = onReset,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Icon(Icons.Default.Restore, contentDescription = "Reset Icon")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Reset Settings", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Logout Button
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.Default.Logout, contentDescription = "Logout Icon")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout", fontSize = 18.sp)
        }
    }
}
