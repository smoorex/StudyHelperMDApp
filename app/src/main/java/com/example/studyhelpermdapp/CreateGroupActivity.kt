package com.example.studyhelpermdapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.google.maps.android.compose.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

class CreateGroupActivity : ComponentActivity() {
    // Firebase authentication and database reference
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://studyhelper-e0d01-default-rtdb.europe-west1.firebasedatabase.app/")

    // API Key for Google Places (replace with your own API key)
    private val placesApiKey = "AIzaSyAc9IwSORGYGrI75QgwasyrsFBe3HykIes"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the Google Places API
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, placesApiKey)
        }

        // Set the content using Jetpack Compose
        setContent {
            MaterialTheme {
                GroupManagementScreen(
                    onCreateGroup = { groupName, selectedLocation ->
                        // Check if a location is selected, then create the group
                        if (selectedLocation != null) {
                            createStudyGroup(groupName, selectedLocation)
                        } else {
                            Toast.makeText(this, "Please select a location on the map.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onJoinGroup = { groupId ->
                        joinStudyGroup(groupId) // Function to join a study group
                    },
                    fetchGroups = { onGroupsFetched -> fetchStudyGroups(onGroupsFetched) }
                )
            }
        }
    }

    // Function to create a new study group
    private fun createStudyGroup(groupName: String, location: LatLng) {
        if (groupName.isBlank()) {
            Toast.makeText(this, "Group name cannot be empty.", Toast.LENGTH_SHORT).show()
            return
        }

        // Generate a unique group ID based on the current timestamp
        val groupId = System.currentTimeMillis().toString()

        // Group data to be saved to Firebase
        val group = mapOf(
            "id" to groupId,
            "name" to groupName,
            "location" to "${location.latitude},${location.longitude}",
            "members" to listOf(auth.currentUser?.uid ?: "") // Add the creator as the first member
        )

        // Save the group data to the "study_groups" node in Firebase
        database.reference.child("study_groups").child(groupId).setValue(group)
            .addOnSuccessListener {
                Toast.makeText(this, "Group created successfully!", Toast.LENGTH_SHORT).show()
                // Navigate back to the Home screen
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to create group. Try again.", Toast.LENGTH_SHORT).show()
            }
    }

    // Function to join an existing study group
    private fun joinStudyGroup(groupId: String) {
        val userId = auth.currentUser?.uid ?: return // Get the current user ID

        val groupRef = database.reference.child("study_groups").child(groupId)
        // Fetch the current members of the group
        groupRef.child("members").get().addOnSuccessListener { dataSnapshot ->
            val members = dataSnapshot.getValue<List<String>>()?.toMutableList() ?: mutableListOf()
            if (!members.contains(userId)) {
                // Add the user ID to the group's member list
                members.add(userId)
                groupRef.child("members").setValue(members)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Successfully joined the group!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to join group. Try again.", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "You are already a member of this group.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to fetch the list of study groups from Firebase
    private fun fetchStudyGroups(onGroupsFetched: (List<StudyGroup>) -> Unit) {
        val groupRef = database.reference.child("study_groups")
        groupRef.get().addOnSuccessListener { dataSnapshot ->
            val groups = dataSnapshot.children.mapNotNull { it.getValue<StudyGroup>() }
            onGroupsFetched(groups) // Pass the fetched groups to the UI
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to fetch study groups.", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun GroupManagementScreen(
    onCreateGroup: (String, LatLng?) -> Unit,
    onJoinGroup: (String) -> Unit,
    fetchGroups: ((List<StudyGroup>) -> Unit) -> Unit
) {
    val context = LocalContext.current

    // State variables for group name, location, and group list
    var groupName by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var studyGroups by remember { mutableStateOf(listOf<StudyGroup>()) }

    // Launcher for Google Places Autocomplete
    val autocompleteLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == ComponentActivity.RESULT_OK && result.data != null) {
            val place = Autocomplete.getPlaceFromIntent(result.data!!)
            selectedLocation = place.latLng // Set the selected location
            Toast.makeText(context, "Location Selected: ${place.name}", Toast.LENGTH_SHORT).show()
        } else if (result.resultCode == AutocompleteActivity.RESULT_ERROR) {
            val status = Autocomplete.getStatusFromIntent(result.data!!)
            Toast.makeText(context, "Error: ${status.statusMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    // Fetch study groups when the screen is first launched
    LaunchedEffect(Unit) {
        fetchGroups { groups -> studyGroups = groups }
    }

    // UI for managing study groups
    Column(modifier = Modifier.padding(16.dp)) {
        // TextField to enter the group name
        TextField(
            value = groupName,
            onValueChange = { groupName = it },
            label = { Text("Group Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Button to open the Google Places Autocomplete search
        Button(onClick = {
            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
            ).build(context)
            autocompleteLauncher.launch(intent)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Search Location")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Google Map to display the selected location
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            onMapClick = { latLng -> selectedLocation = latLng } // Allow map click to select location
        ) {
            selectedLocation?.let { location ->
                Marker(state = MarkerState(position = location))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to create a new study group
        Button(onClick = { onCreateGroup(groupName, selectedLocation) }) {
            Text("Create Group")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display the list of available study groups
        Text("Available Study Groups:", style = MaterialTheme.typography.titleMedium)

        LazyColumn(modifier = Modifier.fillMaxHeight().padding(8.dp)) {
            items(studyGroups) { group ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    onClick = { onJoinGroup(group.id) }
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Name: ${group.name}")
                        Text("Location: ${group.location}")
                        Text("Members: ${group.members.size}")
                    }
                }
            }
        }
    }
}
