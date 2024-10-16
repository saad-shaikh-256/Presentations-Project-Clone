package com.example.presentationsprojectclone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Forgot_Password_page : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password_page)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Handle the back button click
        val backBtn = findViewById<ImageView>(R.id.back_btn)
        backBtn.setOnClickListener {
            val backIntent = Intent(this, login_page::class.java)
            startActivity(backIntent)
        }

        val emailEditText = findViewById<TextInputEditText>(R.id.for_email_input)

        // Set up Reset Password button click listener
        val resetPasswordButton = findViewById<Button>(R.id.reset_btn)
        resetPasswordButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            if (email.isNotEmpty()) {
                checkUserExistsAndSendEmail(email)
            } else {
                Toast.makeText(this, "Please enter an email address", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to check if the user exists in Firestore and send password reset email
    private fun checkUserExistsAndSendEmail(email: String) {
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show()
                } else {
                    // If user exists, send the password reset email
                    sendPasswordResetEmail(email)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Error checking user: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // Function to send the password reset email using Firebase Authentication
    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showAlertDialog()
                } else {
                    Toast.makeText(
                        this,
                        "Failed to send reset email: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    // Function to display the confirmation dialog
    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("We've swent an email to your specified address. It should arrive within the next few minutes! If you are having trouble locating the email, please check your junk folder.")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, id ->
                dialog.dismiss()
                // Redirect to login page
                val intent = Intent(this, login_page::class.java)
                startActivity(intent)
                finish()
            }

        val alert = builder.create()
        alert.setTitle("Password Reset")
        alert.show()
    }
}
