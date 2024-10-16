package com.example.presentationsprojectclone

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.mindrot.jbcrypt.BCrypt

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

        // Initialize Firebase Auth instance
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Handle the back button click
        val backBtn = findViewById<ImageView>(R.id.back_btn)
        backBtn.setOnClickListener {
            val backIntent = Intent(this, login_with_email_page::class.java)
            startActivity(backIntent)
        }

        // Handle forgot password navigation
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
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInputField.error = "Invalid email format"
                isValid = false
            } else {
                emailInputField.error = null
            }

            // Validate password
            if (password.isEmpty()) {
                passwordInputField.error = "Password is required"
                isValid = false
            } else if (password.length < 6) {
                passwordInputField.error = "Password must be at least 6 characters"
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

    // Login user using Firebase Authentication and Firestore with BCrypt encrypted password
    private fun loginUserWithEmailAndPassword(email: String, password: String) {
        firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    val encryptedPassword = document.getString("password") // Get the encrypted password

                    // Compare the entered password with the encrypted password using BCrypt
                    if (encryptedPassword != null && BCrypt.checkpw(password, encryptedPassword)) {
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Home::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // If the passwords don't match
                        Toast.makeText(this, "Incorrect password", Toast.LENGTH_LONG).show()
                    }
                } else {
                    // If the email does not exist in Firestore
                    Toast.makeText(this, "Email not found", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                // Handle error case
                Toast.makeText(this, "Error retrieving user: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
