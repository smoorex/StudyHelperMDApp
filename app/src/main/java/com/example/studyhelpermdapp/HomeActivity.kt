package com.example.studyhelpermdapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

// Data class representing a Task with default values for Firebase deserialization
data class Task(
    val taskName: String = "",
    val description: String = "",
    val category: String = "",
    val priority: String = "",
    val date: String = ""
)

class HomeActivity : ComponentActivity() {

    // Firebase authentication and database references
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase =
        FirebaseDatabase.getInstance("https://studyhelper-e0d01-default-rtdb.europe-west1.firebasedatabase.app/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if the user is logged in; redirect to LoginActivity if not
        val currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Set up the UI using Jetpack Compose
        setContent {
            MaterialTheme {
                HomeScreen(
                    userId = currentUser.uid, // Pass user ID to the HomeScreen
                    onAddTask = {
                        // Open TaskManagementActivity when "Add New Task" is clicked
                        startActivity(Intent(this, TaskManagementActivity::class.java))
                    },
                    onCreateGroup = {
                        // Open CreateGroupActivity when "Create Study Group" is clicked
                        startActivity(Intent(this, CreateGroupActivity::class.java))
                    },
                    onSettings = {
                        // Open SettingsActivity when "Settings" is clicked
                        startActivity(Intent(this, SettingsActivity::class.java))
                    }
                )
            }
        }
    }
}

@Composable
fun HomeScreen(userId: String, onAddTask: () -> Unit, onCreateGroup: () -> Unit, onSettings: () -> Unit) {
    // State variables to hold tasks, study groups, and loading status
    val tasks = remember { mutableStateListOf<Task>() }
    val studyGroups = remember { mutableStateListOf<StudyGroup>() }
    val userEmails = remember { mutableStateMapOf<String, String>() }
    val isLoading = remember { mutableStateOf(true) }

    // Firebase references for tasks and study groups
    val database =
        FirebaseDatabase.getInstance("https://studyhelper-e0d01-default-rtdb.europe-west1.firebasedatabase.app/")
    val taskRef = database.reference.child("tasks").child(userId)
    val groupRef = database.reference.child("study_groups")

    // Fetch tasks and groups when the screen is launched
    LaunchedEffect(Unit) {
        // Fetch tasks from Firebase
        taskRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tasks.clear() // Clear previous task list
                for (taskSnapshot in snapshot.children) {
                    val task = taskSnapshot.getValue(Task::class.java)
                    if (task != null) {
                        tasks.add(task) // Add new task to the list
                    }
                }
                isLoading.value = false // Stop the loading indicator
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading.value = false // Stop loading on error
            }
        })

        // Fetch study groups from Firebase
        groupRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                studyGroups.clear() // Clear previous groups
                for (groupSnapshot in snapshot.children) {
                    val group = groupSnapshot.getValue(StudyGroup::class.java)
                    if (group != null && group.members.contains(userId)) {
                        studyGroups.add(group) // Add group if the user is a member
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // Layout for the home screen
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Welcome to Study Planner", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Show a loading indicator while fetching data
        if (isLoading.value) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            // Display tasks and study groups in a list
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(tasks) { task -> TaskItem(task) } // Show each task
                items(studyGroups) { group -> StudyGroupItem(group, userEmails) } // Show each study group
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to add a new task
        Button(onClick = onAddTask, modifier = Modifier.fillMaxWidth()) {
            Text("Add New Task")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Button to create a new study group
        Button(onClick = onCreateGroup, modifier = Modifier.fillMaxWidth()) {
            Text("Create Study Group")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Button to open settings
        Button(onClick = onSettings, modifier = Modifier.fillMaxWidth()) {
            Text("Settings")
        }
    }
}

@Composable
fun TaskItem(task: Task) {
    // Display a task inside a card with its name and date
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Task: ${task.taskName}", style = MaterialTheme.typography.bodyLarge)
            Text("Date: ${task.date}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun StudyGroupItem(group: StudyGroup, userEmails: MutableMap<String, String>) {
    // Toggle to show/hide group members
    val showMembers = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                showMembers.value = !showMembers.value
                if (showMembers.value) {
                    fetchEmailsForMembers(group.members, userEmails) // Fetch emails for members
                }
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Group: ${group.name}", style = MaterialTheme.typography.bodyLarge)
            Text("Location: ${group.location}", style = MaterialTheme.typography.bodySmall)

            // Display member list if showMembers is true
            if (showMembers.value) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Members:", style = MaterialTheme.typography.titleSmall)
                Column {
                    group.members.forEach { memberId ->
                        val email = userEmails[memberId] ?: "Fetching..." // Show email or placeholder
                        Text("- $email", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

// Fetch placeholder emails for group members (replace with actual fetching logic if needed)
fun fetchEmailsForMembers(
    memberIds: List<String>,
    userEmails: MutableMap<String, String>
) {
    memberIds.forEach { memberId ->
        if (!userEmails.containsKey(memberId)) {
            userEmails[memberId] = "email-for-$memberId@example.com" // Placeholder email
        }
    }
}
