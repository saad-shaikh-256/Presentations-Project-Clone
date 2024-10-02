package com.example.presentationsprojectclone

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import org.mindrot.jbcrypt.BCrypt // Import BCrypt

class login_page : AppCompatActivity() {

    private val emailInputField by lazy { findViewById<TextInputLayout>(R.id.log_email_input_field) }
    private val emailInput by lazy { findViewById<TextInputEditText>(R.id.log_email_input) }
    private val passwordInputField by lazy { findViewById<TextInputLayout>(R.id.log_password_input_field) }
    private val passwordInput by lazy { findViewById<TextInputEditText>(R.id.log_password_input) }
    private val continueWithEmail by lazy { findViewById<Button>(R.id.login_submit_btn) }

    private lateinit var firestore: FirebaseFirestore
    private val TAG = "login_page"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        firestore = FirebaseFirestore.getInstance() // Initialize Firestore instance

        // Handle the back button click
        val backBtn = findViewById<ImageView>(R.id.back_btn)
        backBtn.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }

        // Add TextWatchers to clear errors when typing
        addTextWatchers()

        continueWithEmail.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

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

            if (isValid) {
                checkUserInFirestore(email, password)
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

    private fun checkUserInFirestore(email: String, password: String) {
        firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Assuming only one document per user
                    val userDocument = documents.documents[0]
                    val storedPassword = userDocument.getString("password")

                    // Check if the entered password matches the stored hashed password
                    if (storedPassword != null && BCrypt.checkpw(password, storedPassword)) {
                        // Password matches
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Home::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Password does not match
                        Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // No matching account found
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error checking user in Firestore", exception)
                Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show()
            }
    }
}
