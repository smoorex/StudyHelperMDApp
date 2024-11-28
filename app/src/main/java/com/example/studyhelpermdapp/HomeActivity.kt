package com.example.studyhelpermdapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://studyhelper-e0d01-default-rtdb.europe-west1.firebasedatabase.app/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if the user is logged in
        val currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContent {
            HomeScreen(
                userId = currentUser.uid,
                onAddTask = { startActivity(Intent(this, TaskManagementActivity::class.java)) },
                onSettings = { startActivity(Intent(this, SettingsActivity::class.java)) }
            )
        }
    }
}

@Composable
fun HomeScreen(userId: String, onAddTask: () -> Unit, onSettings: () -> Unit) {
    val tasks = remember { mutableStateListOf<Task>() }
    val isLoading = remember { mutableStateOf(true) }
    val database = FirebaseDatabase.getInstance("https://studyhelper-e0d01-default-rtdb.europe-west1.firebasedatabase.app/")
    val taskRef = database.reference.child("tasks").child(userId)

    LaunchedEffect(Unit) {
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
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Upcoming Tasks", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading.value) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(tasks) { task -> TaskItem(task) }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onAddTask, modifier = Modifier.fillMaxWidth()) {
            Text("Add New Task")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onSettings, modifier = Modifier.fillMaxWidth()) {
            Text("Settings")
        }
    }
}

@Composable
fun TaskItem(task: Task) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Text("Task: ${task.taskName}", style = MaterialTheme.typography.bodyLarge)
        Text("Description: ${task.description}", style = MaterialTheme.typography.bodyMedium)
        Text("Category: ${task.category}", style = MaterialTheme.typography.bodyMedium)
        Text("Priority: ${task.priority}", style = MaterialTheme.typography.bodyMedium)
        Text("Date: ${task.date}", style = MaterialTheme.typography.bodySmall)
    }
}
