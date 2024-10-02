package com.example.presentationsprojectclone

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class login_page : AppCompatActivity() {

    private val emailInputField by lazy { findViewById<TextInputLayout>(R.id.log_email_input_field) }
    private val emailInput by lazy { findViewById<TextInputEditText>(R.id.log_email_input) }
    private val passwordInputField by lazy { findViewById<TextInputLayout>(R.id.log_password_input_field) }
    private val passwordInput by lazy { findViewById<TextInputEditText>(R.id.log_password_input) }
    private val continueWithEmail by lazy { findViewById<Button>(R.id.login_submit_btn) }

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth // Firebase Auth instance
    private val TAG = "login_page"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        // Initialize Firebase instances
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Handle the back button click
        val backBtn = findViewById<ImageView>(R.id.back_btn)
        backBtn.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }

        val forgot = findViewById<TextView>(R.id.log_forgot_password)
        forgot.setOnClickListener {
            val forgotIntent = Intent(this, Forgot_Password_page::class.java)
            startActivity(forgotIntent)
        }

        // Add TextWatchers to clear errors when typing
        addTextWatchers()

        // Handle login button click
        continueWithEmail.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            var isValid = true

            // Validate email
            if (email.isEmpty()) {
                emailInputField.error = "Email address is required"
                isValid = false
            } else {
                emailInputField.error = null
            }

            // Validate password
            if (password.isEmpty()) {
                passwordInputField.error = "Password is required"
                isValid = false
            } else {
                passwordInputField.error = null
            }

            // Proceed if inputs are valid
            if (isValid) {
                loginUserWithEmailAndPassword(email, password)
            }
        }
    }

    private fun addTextWatchers() {
        emailInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                emailInputField.error = null
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        passwordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                passwordInputField.error = null
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // Login user using Firebase Authentication
    private fun loginUserWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Authentication successful, get additional data from Firestore
                    getUserDataFromFirestore(email)
                } else {
                    // Handle authentication failure
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Retrieve user data from Firestore after successful authentication
    private fun getUserDataFromFirestore(email: String) {
        firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Get the user document
                    val userDocument = documents.documents[0]

                    // Fetch and display additional data if needed (like language and country)
                    val language = userDocument.getString("language")
                    val country = userDocument.getString("country")

                    // Proceed to the Home page or wherever you need
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // No user data found in Firestore
                    Toast.makeText(this, "No user data found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
            }
    }
}
