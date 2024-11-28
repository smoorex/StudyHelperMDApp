package com.example.studyhelpermdapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.maps.android.compose.*

class CreateGroupActivity : ComponentActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://studyhelper-e0d01-default-rtdb.europe-west1.firebasedatabase.app/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                CreateGroupScreen(onCreateGroup = { groupName, selectedLocation ->
                    if (selectedLocation != null) {
                        createStudyGroup(groupName, selectedLocation)
                    } else {
                        Toast.makeText(this, "Please select a location on the map.", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    private fun createStudyGroup(groupName: String, location: LatLng) {
        if (groupName.isBlank()) {
            Toast.makeText(this, "Group name cannot be empty.", Toast.LENGTH_SHORT).show()
            return
        }

        val groupId = System.currentTimeMillis().toString()
        val group = mapOf(
            "id" to groupId,
            "name" to groupName,
            "location" to "${location.latitude},${location.longitude}",
            "members" to listOf(auth.currentUser?.uid ?: "Unknown")
        )

        database.reference.child("groups").child(groupId).setValue(group).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Group created successfully!", Toast.LENGTH_SHORT).show()

                // Navigate back to HomeActivity
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Failed to create group: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun CreateGroupScreen(onCreateGroup: (String, LatLng?) -> Unit) {
    var groupName by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    val defaultLocation = LatLng(-33.852, 151.211) // Default location (Sydney, Australia)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 10f)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = groupName,
            onValueChange = { groupName = it },
            label = { Text("Group Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    selectedLocation = latLng // Store the selected location
                }
            ) {
                // Add a marker if a location is selected
                selectedLocation?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Selected Location",
                        snippet = "Lat: ${it.latitude}, Lng: ${it.longitude}"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onCreateGroup(groupName, selectedLocation) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Group")
        }
    }
}
