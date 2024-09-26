package com.example.presentationsprojectclone

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText

class login_with_email_page : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var emailInput: TextInputEditText

    companion object {
        private const val RC_SIGN_IN = 1001
        const val EXTRA_EMAIL = "EXTRA_EMAIL"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_with_email_page)

        emailInput = findViewById(R.id.email_input)

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        findViewById<RelativeLayout>(R.id.continue_with_google).setOnClickListener {
            signInWithGoogle()
        }

        findViewById<Button>(R.id.email_submit_btn).setOnClickListener {
            val email = emailInput.text.toString()
            if (email.isNotEmpty()) {
                // Redirect to the register page with the entered email
                val intent = Intent(this, register_page::class.java)
                intent.putExtra(EXTRA_EMAIL, email)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully
            val email = account?.email ?: ""
            Toast.makeText(this, "Signed in as $email", Toast.LENGTH_SHORT).show()

            // Directly redirect to the home page
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish() // Close the login page

        } catch (e: ApiException) {
            Log.w("GoogleSignIn", "signInResult:failed code=" + e.statusCode)
            Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show()
        }
    }
}
