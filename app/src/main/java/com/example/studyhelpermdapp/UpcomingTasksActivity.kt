  /*

package com.example.studyhelpermdapp

import android.os.Bundle
import android.util.Log
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

data class Task(val taskName: String = "", val date: String = "")

class UpcomingTasksActivity : ComponentActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://studyhelper-e0d01-default-rtdb.europe-west1.firebasedatabase.app/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if the user is logged in
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // Redirect to login activity if the user is not logged in
            // Intent to LoginActivity (you need to create this)
            // startActivity(Intent(this, LoginActivity::class.java))
            return
        }

        setContent {
            UpcomingTasksScreen(currentUser.uid)
        }
    }
}

@Composable
fun UpcomingTasksScreen(userId: String) {
    val tasks = remember { mutableStateListOf<Task>() }
    val isLoading = remember { mutableStateOf(true) }
    val database = FirebaseDatabase.getInstance("https://studyhelper-e0d01-default-rtdb.europe-west1.firebasedatabase.app/")
    val taskRef = database.reference.child("tasks").child(userId)

    // Fetch tasks from Firebase for the specific user
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
                Log.d("UpcomingTasksActivity", "Tasks fetched: $tasks")
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading.value = false
                Log.e("UpcomingTasksActivity", "Error fetching tasks", error.toException())
            }
        })
    }

    if (isLoading.value) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Upcoming Tasks", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(tasks) { task ->
                    TaskItem(task)
                    Divider()
                }
            }
        }
    }
}

@Composable
fun TaskItem(task: Task) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Text("Task: ${task.taskName}", style = MaterialTheme.typography.bodyLarge)
        Text("Date: ${task.date}", style = MaterialTheme.typography.bodySmall)
    }
}
*/