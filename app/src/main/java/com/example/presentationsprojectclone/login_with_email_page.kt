package com.example.presentationsprojectclone

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class login_with_email_page : AppCompatActivity() {

    private val emailInputField by lazy { findViewById<TextInputLayout>(R.id.email_input_field) }
    private val emailInput by lazy { findViewById<TextInputEditText>(R.id.email_input) }
    private val continueWithEmail by lazy { findViewById<android.widget.Button>(R.id.email_submit_btn) }
    private val googleSignInButton by lazy { findViewById<RelativeLayout>(R.id.continue_with_google) }
    private val facebookSignInButton by lazy { findViewById<RelativeLayout>(R.id.continue_with_facebook) }

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firestore: FirebaseFirestore

    private val RC_SIGN_IN = 9001
    private val TAG = "login_with_email_page"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_with_email_page)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id)) // Replace with your actual web client ID
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }

        facebookSignInButton.setOnClickListener {
            // TODO: Implement Facebook login functionality
            Toast.makeText(this, "Facebook login not implemented yet", Toast.LENGTH_SHORT).show()
        }

        // TextWatcher to validate email input
        emailInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                emailInputField.error =
                    if (isValidEmail(s.toString())) null else "Please enter a valid email"
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        continueWithEmail.setOnClickListener {
            val email = emailInput.text.toString()
            when {
                email.isEmpty() -> emailInputField.error = "Email address is required"
                isValidEmail(email) -> checkEmailInFirestore(email)
                else -> emailInputField.error = "Please enter a valid email"
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

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            account?.let {
                val idToken = it.idToken
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                firebaseAuthWithGoogle(credential)
            }
        } catch (e: ApiException) {
            Log.e(TAG, "Google sign in failed", e)
            Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(credential: AuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    // Redirect to home page
                    startActivity(Intent(this, Home::class.java))
                    finish()
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun checkEmailInFirestore(email: String) {
        firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Email exists in Firestore
                    Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, login_page::class.java))
                    finish()
                } else {
                    // Email does not exist in Firestore
                    Toast.makeText(this, "Email not found. Please register.", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(this, register_page::class.java))
                    finish()
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error checking email in Firestore", exception)
                Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
