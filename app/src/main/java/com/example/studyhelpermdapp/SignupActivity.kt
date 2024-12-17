package com.example.studyhelpermdapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

/**
 * SignupActivity:
 * This activity allows users to create a new account using Firebase Authentication.
 */
class SignupActivity : ComponentActivity() {

    // Firebase Authentication instance
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance()

        // Set the UI content using Jetpack Compose
        setContent {
            MaterialTheme {
                SignupScreen(
                    onSignup = { email, password -> createUser(email, password) }
                )
            }
        }
    }

    /**
     * createUser:
     * Attempts to create a new user account with the provided email and password.
     * Displays a success message on success or an error message on failure.
     */
    private fun createUser(email: String, password: String) {
        // Check if email or password fields are empty
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Use Firebase Authentication to create a new user
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // User creation successful
                Toast.makeText(this, "Sign Up Successful!", Toast.LENGTH_SHORT).show()
                finish() // Close the SignupActivity
            } else {
                // Show an error message if user creation fails
                Toast.makeText(this, "Sign Up Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

/**
 * SignupScreen:
 * A composable function that provides the UI for the signup screen.
 *
 * @param onSignup A lambda function triggered when the "Sign Up" button is clicked.
 */
@Composable
fun SignupScreen(onSignup: (String, String) -> Unit) {
    // State variables to hold user input for email, password, and password visibility
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Column layout for the signup screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center, // Center content vertically
        horizontalAlignment = Alignment.CenterHorizontally // Center content horizontally
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with your app logo
            contentDescription = "App Logo",
            modifier = Modifier
                .size(100.dp)
                .padding(bottom = 32.dp)
        )

        // Title of the signup screen
        Text(
            text = "Create Your Account", // Title text
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Email Input Field
        OutlinedTextField(
            value = email, // Current value of the email field
            onValueChange = { email = it }, // Update the email state on change
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") }, // Email icon
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), // Set keyboard type to Email
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Password Input Field
        OutlinedTextField(
            value = password, // Current value of the password field
            onValueChange = { password = it }, // Update the password state on change
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") }, // Password icon
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(), // Toggle password visibility
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), // Set keyboard type to Password
            trailingIcon = {
                // Icon button to toggle password visibility
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Sign Up Button
        Button(
            onClick = { onSignup(email, password) }, // Trigger the onSignup lambda with email and password
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            Icon(Icons.Default.PersonAdd, contentDescription = "Sign Up Icon") // Sign Up icon
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign Up", fontSize = 18.sp) // Button text
        }
    }
}
