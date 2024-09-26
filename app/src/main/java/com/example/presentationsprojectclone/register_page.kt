import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.presentationsprojectclone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.HashMap

class register_page : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var regEmailInput: EditText
    private lateinit var regNameInput: EditText
    private lateinit var regPasswordInput: EditText
    private lateinit var regLanguageInput: EditText
    private lateinit var regCountryInput: EditText
    private lateinit var registerSubmitBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_page)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize your EditTexts and Button
        regEmailInput = findViewById(R.id.reg_email_input)
        regNameInput = findViewById(R.id.reg_name_input)
        regPasswordInput = findViewById(R.id.reg_password_input)
        regLanguageInput = findViewById(R.id.reg_language_input)
        regCountryInput = findViewById(R.id.reg_country_input)
        registerSubmitBtn = findViewById(R.id.register_submit_btn)

        registerSubmitBtn.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val email = regEmailInput.text.toString()
        val name = regNameInput.text.toString()
        val password = regPasswordInput.text.toString()
        val language = regLanguageInput.text.toString()
        val country = regCountryInput.text.toString()

        if (email.isEmpty() || name.isEmpty() || password.isEmpty() || language.isEmpty() || country.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Create user with email and password
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // User registration successful, now save data to Firestore
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                    saveUserData(userId, email, name, language, country)
                } else {
                    // If registration fails, display a message to the user.
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserData(userId: String, email: String, name: String, language: String, country: String) {
        val user = HashMap<String, Any>()
        user["email"] = email
        user["name"] = name
        user["language"] = language
        user["country"] = country

        // Save user data to Firestore
        firestore.collection("users").document(userId)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                // Redirect to home or login page
                // startActivity(Intent(this, HomePage::class.java))
                // finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving user data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
