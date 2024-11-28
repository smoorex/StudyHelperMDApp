package com.example.studyhelpermdapp

import android.app.DatePickerDialog
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class TaskManagementActivity : ComponentActivity() {

    // Initialize Firebase Database once and reuse
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://studyhelper-e0d01-default-rtdb.europe-west1.firebasedatabase.app/")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if the user is logged in
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // Redirect to login activity if the user is not logged in
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Set the Compose UI content for Enhanced Task Management
        setContent {
            EnhancedTaskManagementScreen { taskName, description, category, priority, date ->
                addTaskToFirebase(currentUser.uid, taskName, description, category, priority, date)
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    private fun addTaskToFirebase(userId: String, taskName: String, description: String, category: String, priority: String, date: String) {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No network connection. Cannot add task.", Toast.LENGTH_SHORT).show()
            return
        }

        if (taskName.isBlank() || description.isBlank() || category.isBlank() || priority.isBlank() || date.isBlank()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val taskRef = database.reference.child("tasks").child(userId).push()
        val taskData = mapOf(
            "taskName" to taskName,
            "description" to description,
            "category" to category,
            "priority" to priority,
            "date" to date
        )

        taskRef.setValue(taskData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Task added successfully!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Failed to add task: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Task write failed: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun EnhancedTaskManagementScreen(onAddTask: (String, String, String, String, String) -> Unit) {
    var taskName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = taskName,
            onValueChange = { taskName = it },
            label = { Text("Task Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = priority,
            onValueChange = { priority = it },
            label = { Text("Priority (e.g., Low, Medium, High)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Task Date") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }) {
            Text("Select Date")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onAddTask(taskName, description, category, priority, date) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Task")
        }
    }
}
