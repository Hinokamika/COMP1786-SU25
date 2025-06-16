package com.example.comp1786_su25.User_interface.Main_pages

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.comp1786_su25.MVC.Identifier
import com.example.comp1786_su25.R

class Login_page : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var rememberMeCheckBox: CheckBox
    private lateinit var databaseHelper: Identifier
    private lateinit var sharedPreferences: SharedPreferences

    // Constants for SharedPreferences
    private val PREF_NAME = "LoginPrefs"
    private val KEY_REMEMBER = "remember"
    private val KEY_USERNAME = "username"
    private val KEY_PASSWORD = "password"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        usernameEditText = findViewById(R.id.editTextText)
        passwordEditText = findViewById(R.id.editTextTextPassword)
        loginButton = findViewById(R.id.LogIn_Button)
        rememberMeCheckBox = findViewById(R.id.checkBox)
        databaseHelper = Identifier(this)
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        // Load saved credentials if "Remember Me" was checked
        loadSavedCredentials()

        // Register button click listener
        val registerTextView = findViewById<TextView>(R.id.locateRegister)
        registerTextView.setOnClickListener {
            // Navigate to RegisterScreen
            startActivity(Intent(this@Login_page, Register_page::class.java))
        }

        // Login button click listener
        loginButton.setOnClickListener {
            if (validateInputs()) {
                authenticateUser()
            }
        }
    }

    /**
     * Validates the username and password inputs
     * @return true if all validations pass, false otherwise
     */
    private fun validateInputs(): Boolean {
        val username = usernameEditText.text.toString().trim { it <= ' ' }
        val password = passwordEditText.text.toString().trim { it <= ' ' }

        // Check if username is empty
        if (TextUtils.isEmpty(username)) {
            usernameEditText.error = "Username cannot be empty"
            usernameEditText.requestFocus()
            return false
        }

        // Check username length
        if (username.length < 4) {
            usernameEditText.error = "Username must be at least 4 characters"
            usernameEditText.requestFocus()
            return false
        }

        // Check if password is empty
        if (TextUtils.isEmpty(password)) {
            passwordEditText.error = "Password cannot be empty"
            passwordEditText.requestFocus()
            return false
        }

        // Check password length
        if (password.length < 6) {
            passwordEditText.error = "Password must be at least 6 characters"
            passwordEditText.requestFocus()
            return false
        }

        return true // All validations passed
    }

    /**
     * Authenticates the user against the SQLite database
     * Checks if the provided username/email and password match a user in the database
     */
    private fun authenticateUser() {
        val identifier = usernameEditText.text.toString().trim { it <= ' ' }
        val password = passwordEditText.text.toString().trim { it <= ' ' }

        // Search for the user in the database
        val user = databaseHelper.getUserByUsernameOrEmail(identifier, password)

        if (user != null) {
            // Authentication successful
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

            // Save credentials if "Remember Me" is checked
            if (rememberMeCheckBox.isChecked) {
                saveCredentials(user.username, password)
            } else {
                // Clear saved credentials
                clearCredentials()
            }

            // Navigate to the main screen and pass user data
            val intent = Intent(this@Login_page, MainActivity::class.java)
            intent.putExtra("USERNAME", user.username)
            startActivity(intent)
            finish() // Close the login activity
        } else {
            // Authentication failed
            Toast.makeText(this, "Invalid username/email or password", Toast.LENGTH_SHORT)
                .show()
        }
    }

    /**
     * Saves the user's credentials in SharedPreferences
     */
    private fun saveCredentials(username: String, password: String) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_REMEMBER, true)
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_PASSWORD, password)
        editor.apply()
    }

    /**
     * Loads the saved credentials from SharedPreferences
     */
    private fun loadSavedCredentials() {
        val isRemembered = sharedPreferences.getBoolean(KEY_REMEMBER, false)
        if (isRemembered) {
            val username = sharedPreferences.getString(KEY_USERNAME, "")
            val password = sharedPreferences.getString(KEY_PASSWORD, "")
            usernameEditText.setText(username)
            passwordEditText.setText(password)
            rememberMeCheckBox.isChecked = true
        }
    }

    /**
     * Clears the saved credentials from SharedPreferences
     */
    private fun clearCredentials() {
        val editor = sharedPreferences.edit()
        editor.remove(KEY_USERNAME)
        editor.remove(KEY_PASSWORD)
        editor.putBoolean(KEY_REMEMBER, false)
        editor.apply()
    }
}