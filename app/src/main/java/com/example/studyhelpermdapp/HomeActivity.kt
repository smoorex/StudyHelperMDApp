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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*



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
            HomeScreen(
                userId = currentUser.uid,
                onAddTask = { startActivity(Intent(this, TaskManagementActivity::class.java)) },
                onCreateGroup = { startActivity(Intent(this, CreateGroupActivity::class.java)) },
                onSettings = { startActivity(Intent(this, SettingsActivity::class.java)) }
            )
        }
    }
}

@Composable
fun TaskItem(task: Task, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Task Name: ${task.taskName}", style = MaterialTheme.typography.bodyLarge)
            Text("Description: ${task.description}", style = MaterialTheme.typography.bodyMedium)
            Text("Priority: ${task.priority}", style = MaterialTheme.typography.bodyMedium)
            Text("Date: ${task.date}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onDelete, colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)) {
                Text("Delete")
            }
        }
    }
}

@Composable
fun HomeScreen(userId: String, onAddTask: () -> Unit, onCreateGroup: () -> Unit, onSettings: () -> Unit) {
    val tasks = remember { mutableStateListOf<Task>() }
    val isLoading = remember { mutableStateOf(true) }
    val database =
        FirebaseDatabase.getInstance("https://studyhelper-e0d01-default-rtdb.europe-west1.firebasedatabase.app/")
    val taskRef = database.reference.child("tasks").child(userId)

    // Fetch tasks from Firebase
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
        Text("Welcome to Study Planner", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading.value) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            val context = LocalContext.current

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(tasks) { task ->
                    TaskItem(
                        task = task,
                        onDelete = {
                            taskRef.child(task.id).removeValue()
                                .addOnSuccessListener {
                                    tasks.remove(task)
                                    Toast.makeText(
                                        context,
                                        "Task deleted successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        context,
                                        "Failed to delete task",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    )
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
}
