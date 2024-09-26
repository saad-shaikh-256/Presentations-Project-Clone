package com.example.presentationsprojectclone

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class login_page : AppCompatActivity() {

    private val emailInputField by lazy { findViewById<TextInputLayout>(R.id.log_email_input_field) }
    private val emailInput by lazy { findViewById<TextInputEditText>(R.id.log_email_input) }
    private val passwordInputField by lazy { findViewById<TextInputLayout>(R.id.log_password_input_field) }
    private val passwordInput by lazy { findViewById<TextInputEditText>(R.id.log_password_input) }
    private val continueWithEmail by lazy { findViewById<Button>(R.id.login_submit_btn) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        val backBtn = findViewById<ImageView>(R.id.back_btn)
        backBtn.setOnClickListener {
            val backIntent = Intent(this, login_with_email_page::class.java)
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
            } else if (email == "new@gmail.com" || email == "demo@gmail.com") {
                emailInputField.error = null
                // Proceed with the intent to the register page
                startActivity(Intent(this, MainActivity::class.java))

            } else {
                emailInputField.error = "Please enter a valid email"
                isValid = false
            }

            // Validate password
            if (password.isEmpty()) {
                passwordInputField.error = "Password is required"
                isValid = false
            } else {
                passwordInputField.error = null
            }

            // Check if all inputs are valid
            if (isValid) {
                // Proceed to the next activity
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
        emailInput.setText("demo@gmail.com")
    }

    private fun addTextWatchers() {
        emailInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Clear error as soon as the user starts typing
                emailInputField.error = null

                // Provide real-time validation
                if (s.toString() == "new@gmail.com" || s.toString() == "demo@gmail.com") {
                    emailInputField.error = null
                } else if (s.toString().isNotEmpty()) {
                    emailInputField.error = "Please enter a valid email"
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        passwordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Clear error as soon as the user starts typing
                passwordInputField.error =
                    if (s.toString() != "123456") "Please enter valid password" else null
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}
