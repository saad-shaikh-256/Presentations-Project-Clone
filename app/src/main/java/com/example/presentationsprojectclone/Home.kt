package com.example.presentationsprojectclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser

class Home : AppCompatActivity() {

    private val logbtn by lazy { findViewById<Button>(R.id.logout) }
    private val deleteBtn by lazy { findViewById<Button>(R.id.delete) }

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val TAG = "Home"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id)) // Ensure this ID is present in strings.xml
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Set up the logout button listener
        logbtn.setOnClickListener {
            signOutBtn()
        }

        // Set up the delete account button listener
        deleteBtn.setOnClickListener {
            deleteAccount()
        }
    }

    private fun signOutBtn() {
        // Sign out from Firebase
        firebaseAuth.signOut()

        // Sign out from Google
        googleSignInClient.signOut().addOnCompleteListener(this) {
            // Show a toast message for successful logout
            Toast.makeText(this@Home, "Logged out successfully", Toast.LENGTH_SHORT).show()

            // Navigate back to the login screen
            val intent = Intent(this@Home, login_with_email_page::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun deleteAccount() {
        val user: FirebaseUser? = firebaseAuth.currentUser

        user?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Show a toast message for successful account deletion
                Toast.makeText(this@Home, "Account deleted successfully", Toast.LENGTH_SHORT).show()

                // Sign out from Google
                googleSignInClient.signOut()

                // Navigate back to the login screen
                val intent = Intent(this@Home, login_with_email_page::class.java)
                startActivity(intent)
                finish()
            } else {
                // Display error message
                Toast.makeText(this@Home, "Failed to delete account: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
