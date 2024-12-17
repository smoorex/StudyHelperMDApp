package com.example.studyhelpermdapp

import android.app.DatePickerDialog
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.* // Layout components
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

/**
 * TaskManagementActivity:
 * This activity allows users to create and save tasks with details such as name, description,
 * category, priority, and date. Tasks are stored in Firebase Realtime Database.
 */
class TaskManagementActivity : ComponentActivity() {

    // Firebase database and authentication instances
    private val database: FirebaseDatabase =
        FirebaseDatabase.getInstance("https://studyhelper-e0d01-default-rtdb.europe-west1.firebasedatabase.app/")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ensure the user is logged in; otherwise, redirect to LoginActivity
        val currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Set up the UI using Jetpack Compose
        setContent {
            MaterialTheme {
                EnhancedTaskManagementScreen { taskName, description, category, priority, date ->
                    // Pass the task data to be added to Firebase
                    addTaskToFirebase(currentUser.uid, taskName, description, category, priority, date)
                }
            }
        }
    }

    /**
     * addTaskToFirebase:
     * Saves the task to Firebase Realtime Database under the current user's ID.
     * Displays a success message on completion or an error message if it fails.
     *
     * @param userId User ID for associating the task with the user.
     * @param taskName The name of the task.
     * @param description Task description.
     * @param category Task category.
     * @param priority Task priority level (Low, Medium, High).
     * @param date Task due date in the format "yyyy-MM-dd".
     */
    private fun addTaskToFirebase(
        userId: String,
        taskName: String,
        description: String,
        category: String,
        priority: String,
        date: String
    ) {
        // Validate inputs to ensure no field is left blank
        if (taskName.isBlank() || description.isBlank() || category.isBlank() || priority.isBlank() || date.isBlank()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Reference to the "tasks" node in Firebase under the current user's ID
        val taskRef = database.reference.child("tasks").child(userId).push()

        // Data to save in Firebase
        val taskData = mapOf(
            "taskName" to taskName,
            "description" to description,
            "category" to category,
            "priority" to priority,
            "date" to date
        )

        // Save the task data to Firebase
        taskRef.setValue(taskData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Show success message and navigate to HomeActivity
                Toast.makeText(this, "Task added successfully!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                // Show error message if saving fails
                Toast.makeText(this, "Failed to add task.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

/**
 * EnhancedTaskManagementScreen:
 * A composable function that provides the UI for adding a new task.
 *
 * @param onAddTask A lambda function to handle the task submission.
 */
@Composable
fun EnhancedTaskManagementScreen(onAddTask: (String, String, String, String, String) -> Unit) {
    // State variables for task input fields
    var taskName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    // Context for showing the DatePickerDialog
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Layout for the task input form
    Column(modifier = Modifier.padding(16.dp)) {
        // Task Name Input Field
        OutlinedTextField(
            value = taskName,
            onValueChange = { taskName = it },
            label = { Text("Task Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Description Input Field
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Category Input Field
        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Priority Input Field
        OutlinedTextField(
            value = priority,
            onValueChange = { priority = it },
            label = { Text("Priority (Low, Medium, High)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Task Date Picker Field
        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Task Date") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true, // Prevent manual editing
            trailingIcon = {
                // Calendar icon to show DatePickerDialog
                IconButton(onClick = {
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
                    Icon(Icons.Default.CalendarToday, contentDescription = "Pick a date")
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Save Task Button
        Button(
            onClick = { onAddTask(taskName, description, category, priority, date) },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(12.dp)
        ) {
            Icon(Icons.Default.Save, contentDescription = "Save Task") // Save Icon
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save Task") // Button text
        }
    }
}
