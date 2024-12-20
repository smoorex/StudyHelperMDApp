package com.example.studyhelpermdapp

import android.app.DatePickerDialog
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

data class Task(
    val id: String = "",
    val taskName: String = "",
    val description: String = "",
    val priority: String = "",
    val date: String = ""
)

class TaskManagementActivity : ComponentActivity() {

    private val database = FirebaseDatabase.getInstance("https://studyhelper-e0d01-default-rtdb.europe-west1.firebasedatabase.app/").reference
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContent {
            TaskScreen(
                onCreateTask = { taskName, description, priority, date ->
                    createTask(currentUser.uid, taskName, description, priority, date)
                },
                onDeleteTask = { task ->
                    deleteTask(currentUser.uid, task)
                }
            )
        }
    }

    @Composable
    fun TaskScreen(
        onCreateTask: (String, String, String, String) -> Unit,
        onDeleteTask: (Task) -> Unit
    ) {
        val context = LocalContext.current
        val userId = auth.currentUser?.uid ?: ""
        var tasks by remember { mutableStateOf(listOf<Task>()) }
        var taskName by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var selectedPriority by remember { mutableStateOf("Low") }
        var selectedDate by remember { mutableStateOf("") }

        LaunchedEffect(Unit) {
            database.child("tasks").child(userId).get().addOnSuccessListener { snapshot ->
                tasks = snapshot.children.mapNotNull { taskSnapshot ->
                    val task = taskSnapshot.getValue(Task::class.java)
                    task?.copy(id = taskSnapshot.key ?: "")
                }
            }
        }

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // Input fields for task creation
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

            // Dropdown for priority selection
            Text("Priority Level", style = MaterialTheme.typography.bodyLarge)
            var expanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = { expanded = true }) {
                    Text(selectedPriority)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Low") },
                            onClick = {
                                selectedPriority = "Low"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Medium") },
                            onClick = {
                                selectedPriority = "Medium"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("High") },
                            onClick = {
                                selectedPriority = "High"
                                expanded = false
                            }
                        )
                    }

                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Date picker for due date
            Text("Due Date: $selectedDate", style = MaterialTheme.typography.bodyMedium)
            Button(onClick = {
                val calendar = Calendar.getInstance()
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        calendar.set(year, month, day)
                        selectedDate = dateFormat.format(calendar.time)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }) {
                Text("Pick a Date")
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Create Task button
            Button(onClick = {
                if (taskName.isNotBlank() && description.isNotBlank() && selectedDate.isNotBlank()) {
                    onCreateTask(taskName, description, selectedPriority, selectedDate)
                    taskName = ""
                    description = ""
                    selectedPriority = "Low"
                    selectedDate = ""
                } else {
                    Toast.makeText(context, "Please complete all fields.", Toast.LENGTH_SHORT).show()
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Create Task")
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Display tasks in a LazyColumn
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                items(tasks) { task ->
                    TaskItem(task, onDeleteTask)
                }
            }
        }
    }

    @Composable
    fun TaskItem(task: Task, onDelete: (Task) -> Unit) {
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Task Name: ${task.taskName}", style = MaterialTheme.typography.bodyLarge)
                Text("Description: ${task.description}", style = MaterialTheme.typography.bodyMedium)
                Text("Priority: ${task.priority}", style = MaterialTheme.typography.bodyMedium)
                Text("Due Date: ${task.date}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { onDelete(task) }, colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)) {
                    Text(text = "Delete")
                }
            }
        }
    }

    private fun createTask(userId: String, taskName: String, description: String, priority: String, date: String) {
        if (taskName.isBlank() || description.isBlank() || date.isBlank()) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show()
            return
        }

        val taskId = System.currentTimeMillis().toString()
        val task = Task(id = taskId, taskName = taskName, description = description, priority = priority, date = date)

        database.child("tasks").child(userId).child(taskId).setValue(task)
            .addOnSuccessListener {
                Toast.makeText(this, "Task created successfully!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to create task.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteTask(userId: String, task: Task) {
        database.child("tasks").child(userId).child(task.id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Task deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to delete task", Toast.LENGTH_SHORT).show()
            }
    }
}
