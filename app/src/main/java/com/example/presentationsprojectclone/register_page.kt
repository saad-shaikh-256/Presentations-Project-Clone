package com.example.presentationsprojectclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore

class register_page : AppCompatActivity() {

    private val regEmailInputField by lazy { findViewById<TextInputLayout>(R.id.reg_email_input_field) }
    private val regEmailInput by lazy { findViewById<TextInputEditText>(R.id.reg_email_input) }
    private val regNameInputField by lazy { findViewById<TextInputLayout>(R.id.reg_name_input_field) }
    private val regNameInput by lazy { findViewById<TextInputEditText>(R.id.reg_name_input) }
    private val regPasswordInputField by lazy { findViewById<TextInputLayout>(R.id.reg_password_input_field) }
    private val regPasswordInput by lazy { findViewById<TextInputEditText>(R.id.reg_password_input) }
    private val regLanguageInputField by lazy { findViewById<TextInputLayout>(R.id.reg_language_input_field) }
    private val regLanguageInput by lazy { findViewById<TextInputEditText>(R.id.reg_language_input) }
    private val regCountryInputField by lazy { findViewById<TextInputLayout>(R.id.reg_country_input_field) }
    private val regCountryInput by lazy { findViewById<TextInputEditText>(R.id.reg_country_input) }
    private val regSubmitBtn by lazy { findViewById<Button>(R.id.register_submit_btn) }

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_page)

        firestore = FirebaseFirestore.getInstance() // Initialize Firestore instance

        // Handle the back button click
        val backBtn = findViewById<ImageView>(R.id.back_btn)
        backBtn.setOnClickListener {
            val backIntent = Intent(this, login_with_email_page::class.java)
            startActivity(backIntent)
        }

        // Add TextWatchers to clear errors when typing
        addTextWatchers()

        regSubmitBtn.setOnClickListener {
            val email = regEmailInput.text.toString()
            val name = regNameInput.text.toString()
            val password = regPasswordInput.text.toString()
            val language = regLanguageInput.text.toString()
            val country = regCountryInput.text.toString()

            var isValid = true

            // Validate email
            if (email.isEmpty()) {
                regEmailInputField.error = "Email address is required"
                isValid = false
            } else {
                regEmailInputField.error = null
            }

            // Validate name
            if (name.isEmpty()) {
                regNameInputField.error = "Name is required"
                isValid = false
            } else {
                regNameInputField.error = null
            }

            // Validate password
            if (password.isEmpty()) {
                regPasswordInputField.error = "Password is required"
                isValid = false
            } else if (password.length < 6) {
                regPasswordInputField.error = "Password must be at least 6 characters"
                isValid = false
            } else {
                regPasswordInputField.error = null
            }

            // Validate language
            if (language.isEmpty()) {
                regLanguageInputField.error = "Language is required"
                isValid = false
            } else {
                regLanguageInputField.error = null
            }

            // Validate country
            if (country.isEmpty()) {
                regCountryInputField.error = "Country is required"
                isValid = false
            } else {
                regCountryInputField.error = null
            }

            if (isValid) {
                // Check if email already exists in Firestore
                checkEmailExists(email) { emailExists ->
                    if (emailExists) {
                        regEmailInputField.error = "Email already registered"
                        Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show()
                    } else {
                        // All fields are valid and email is not taken, proceed with registration
                        registerUser(email, name, password, language, country)
                    }
                }
            }
        }

        // Set default values
        regEmailInput.setText("new@gmail.com")
        regLanguageInput.setText("English")
        regCountryInput.setText("India")
    }

    private fun addTextWatchers() {
        regEmailInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                regEmailInputField.error = null
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        regNameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                regNameInputField.error = null
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        regPasswordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                regPasswordInputField.error = null
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        regLanguageInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                regLanguageInputField.error = null
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        regCountryInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                regCountryInputField.error = null
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun checkEmailExists(email: String, callback: (Boolean) -> Unit) {
        firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                callback(!documents.isEmpty)
            }
            .addOnFailureListener {
                callback(false) // Handle failure case
            }
    }

    private fun registerUser(email: String, name: String, password: String, language: String, country: String) {
        val userData = hashMapOf(
            "email" to email,
            "name" to name,
            "password" to password, // Remember to encrypt this password before saving
            "language" to language,
            "country" to country
        )

        firestore.collection("users")
            .add(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                val nextIntent = Intent(this, Home::class.java)
                startActivity(nextIntent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error registering user: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}