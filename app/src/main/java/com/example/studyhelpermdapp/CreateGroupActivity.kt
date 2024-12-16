package com.example.studyhelpermdapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.google.maps.android.compose.*

class CreateGroupActivity : ComponentActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://studyhelper-e0d01-default-rtdb.europe-west1.firebasedatabase.app/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                GroupManagementScreen(
                    onCreateGroup = { groupName, selectedLocation ->
                        if (selectedLocation != null) {
                            createStudyGroup(groupName, selectedLocation)
                        } else {
                            Toast.makeText(this, "Please select a location on the map.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onJoinGroup = { groupId ->
                        joinStudyGroup(groupId)
                    },
                    fetchGroups = { onGroupsFetched -> fetchStudyGroups(onGroupsFetched) }
                )
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
            "members" to listOf(auth.currentUser?.uid ?: "") // Initialize with the creator as the first member
        )

        database.reference.child("study_groups").child(groupId).setValue(group)
            .addOnSuccessListener {
                Toast.makeText(this, "Group created successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to create group. Try again.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun joinStudyGroup(groupId: String) {
        val userId = auth.currentUser?.uid ?: return

        val groupRef = database.reference.child("study_groups").child(groupId)
        groupRef.child("members").get().addOnSuccessListener { dataSnapshot ->
            val members = dataSnapshot.getValue<List<String>>()?.toMutableList() ?: mutableListOf()
            if (!members.contains(userId)) {
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

    private fun fetchStudyGroups(onGroupsFetched: (List<StudyGroup>) -> Unit) {
        val groupRef = database.reference.child("study_groups")
        groupRef.get().addOnSuccessListener { dataSnapshot ->
            val groups = dataSnapshot.children.mapNotNull { it.getValue<StudyGroup>() }
            onGroupsFetched(groups)
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
    var groupName by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var studyGroups by remember { mutableStateOf(listOf<StudyGroup>()) }

    LaunchedEffect(Unit) {
        fetchGroups { groups ->
            studyGroups = groups
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = groupName,
            onValueChange = { groupName = it },
            label = { Text("Group Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            onMapClick = { latLng ->
                selectedLocation = latLng
            }
        ) {
            selectedLocation?.let { location ->
                Marker(
                    state = MarkerState(position = location)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onCreateGroup(groupName, selectedLocation) }) {
            Text("Create Group")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Available Study Groups:", style = MaterialTheme.typography.titleMedium)

        LazyColumn(modifier = Modifier.fillMaxHeight().padding(8.dp)) {
            items(studyGroups) { group ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(4.dp),
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
