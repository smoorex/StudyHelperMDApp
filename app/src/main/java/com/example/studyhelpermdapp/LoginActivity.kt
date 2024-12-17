package com.example.studyhelpermdapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

/**
 * LoginActivity:
 * This activity allows users to log in using their email and password.
 * It uses Firebase Authentication for user verification.
 */
class LoginActivity : ComponentActivity() {

    // Firebase Authentication instance
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance()

        // Set the UI content using Jetpack Compose
        setContent {
            MaterialTheme {
                LoginScreen(
                    onLogin = { email, password -> loginUser(email, password) },
                    onSignup = { navigateToSignup() }
                )
            }
        }
    }

    /**
     * loginUser:
     * Attempts to log the user in using the provided email and password.
     * Shows a success message and navigates to the Home screen on success,
     * or an error message on failure.
     */
    private fun loginUser(email: String, password: String) {
        // Check if fields are empty
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Firebase sign-in with email and password
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Navigate to HomeActivity if login is successful
                startActivity(Intent(this, HomeActivity::class.java))
                finish() // Close the LoginActivity
            } else {
                // Show error message if authentication fails
                Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * navigateToSignup:
     * Navigates the user to the SignupActivity to create a new account.
     */
    private fun navigateToSignup() {
        startActivity(Intent(this, SignupActivity::class.java))
    }
}

/**
 * LoginScreen:
 * A composable function that displays the login screen UI.
 *
 * @param onLogin A lambda function to handle login action.
 * @param onSignup A lambda function to navigate to the signup screen.
 */
@Composable
fun LoginScreen(onLogin: (String, String) -> Unit, onSignup: () -> Unit) {
    // State variables for email and password fields
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Column layout for login screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center, // Align content in the center vertically
        horizontalAlignment = Alignment.CenterHorizontally // Align content in the center horizontally
    ) {
        // App Logo
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with your app logo
            contentDescription = "App Logo",
            modifier = Modifier
                .size(100.dp)
                .padding(bottom = 32.dp)
        )

        // App Title
        Text(
            text = "Welcome Back!", // Title text
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Email Input Field
        OutlinedTextField(
            value = email, // Current value of the email field
            onValueChange = { email = it }, // Update the email state on change
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") }, // Icon for email
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email) // Set keyboard type to Email
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Password Input Field
        OutlinedTextField(
            value = password, // Current value of the password field
            onValueChange = { password = it }, // Update the password state on change
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") }, // Icon for password
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(), // Hide password text
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password) // Set keyboard type to Password
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Login Button
        Button(
            onClick = { onLogin(email, password) }, // Call the onLogin lambda with email and password
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp) // Add padding to button content
        ) {
            Text("Login", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Navigate to Signup Button
        TextButton(
            onClick = onSignup, // Call the onSignup lambda to navigate to signup screen
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Don't have an account? Sign Up")
        }
    }
}
