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

data class Task(
    val taskName: String = "",
    val description: String = "",
    val category: String = "",
    val priority: String = "",
    val date: String = ""
)



class HomeActivity : ComponentActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase =
        FirebaseDatabase.getInstance("https://studyhelper-e0d01-default-rtdb.europe-west1.firebasedatabase.app/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContent {
            MaterialTheme {
                HomeScreen(
                    userId = currentUser.uid,
                    onAddTask = { startActivity(Intent(this, TaskManagementActivity::class.java)) },
                    onCreateGroup = { startActivity(Intent(this, CreateGroupActivity::class.java)) },
                    onSettings = { startActivity(Intent(this, SettingsActivity::class.java)) }
                )
            }
        }
    }
}

@Composable
fun HomeScreen(userId: String, onAddTask: () -> Unit, onCreateGroup: () -> Unit, onSettings: () -> Unit) {
    val tasks = remember { mutableStateListOf<Task>() }
    val studyGroups = remember { mutableStateListOf<StudyGroup>() }
    val userEmails = remember { mutableStateMapOf<String, String>() }
    val isLoading = remember { mutableStateOf(true) }
    val database =
        FirebaseDatabase.getInstance("https://studyhelper-e0d01-default-rtdb.europe-west1.firebasedatabase.app/")
    val taskRef = database.reference.child("tasks").child(userId)
    val groupRef = database.reference.child("study_groups")

    LaunchedEffect(Unit) {
        // Fetch tasks
        taskRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tasks.clear()
                for (taskSnapshot in snapshot.children) {
                    val task = taskSnapshot.getValue(Task::class.java)
                    if (task != null) {
                        tasks.add(task)
                    }
                }
                isLoading.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading.value = false
            }
        })

        // Fetch study groups
        groupRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                studyGroups.clear()
                for (groupSnapshot in snapshot.children) {
                    val group = groupSnapshot.getValue(StudyGroup::class.java)
                    if (group != null && group.members.contains(userId)) {
                        studyGroups.add(group)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Welcome to Study Planner", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading.value) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(tasks) { task -> TaskItem(task) }
                items(studyGroups) { group -> StudyGroupItem(group, userEmails) }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onAddTask, modifier = Modifier.fillMaxWidth()) {
            Text("Add New Task")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onCreateGroup, modifier = Modifier.fillMaxWidth()) {
            Text("Create Study Group")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onSettings, modifier = Modifier.fillMaxWidth()) {
            Text("Settings")
        }
    }
}

@Composable
fun TaskItem(task: Task) {
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
    val showMembers = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                showMembers.value = !showMembers.value
                if (showMembers.value) {
                    fetchEmailsForMembers(group.members, userEmails)
                }
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Group: ${group.name}", style = MaterialTheme.typography.bodyLarge)
            Text("Location: ${group.location}", style = MaterialTheme.typography.bodySmall)

            if (showMembers.value) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Members:", style = MaterialTheme.typography.titleSmall)
                Column {
                    group.members.forEach { memberId ->
                        val email = userEmails[memberId] ?: "Fetching..."
                        Text("- $email", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

fun fetchEmailsForMembers(
    memberIds: List<String>,
    userEmails: MutableMap<String, String>
) {
    memberIds.forEach { memberId ->
        if (!userEmails.containsKey(memberId)) {
            userEmails[memberId] = "email-for-$memberId@example.com" // Placeholder or implement email fetching logic
        }
    }
}
