package com.example.comp1786_su25.User_interface.Main_pages

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.comp1786_su25.MVC.Identifier
import com.example.comp1786_su25.Models.userModel
import com.example.comp1786_su25.R

class Register_page : AppCompatActivity() {
    private lateinit var registerButton: Button
    private lateinit var usernameEditText: EditText
    private lateinit var registerEmailEditText: EditText
    private lateinit var registerPasswordEditText: EditText
    private lateinit var registerConfirmPasswordEditText: EditText
    private lateinit var loginLinkTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        registerButton = findViewById(R.id.registerButton)
        usernameEditText = findViewById(R.id.registerUsernameEditText)
        registerEmailEditText = findViewById(R.id.registerEmailEditText)
        registerPasswordEditText = findViewById(R.id.registerPasswordEditText)
        registerConfirmPasswordEditText = findViewById(R.id.registerConfirmPasswordEditText)
        loginLinkTextView = findViewById(R.id.loginLinkTextView)

        registerButton.setOnClickListener {
            try {
                val username = usernameEditText.text.toString().trim { it <= ' ' }
                val email = registerEmailEditText.text.toString().trim { it <= ' ' }
                val password = registerPasswordEditText.text.toString().trim { it <= ' ' }
                val confirmPassword = registerConfirmPasswordEditText.text.toString().trim { it <= ' ' }

                require(!(username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty())) { "All fields must be filled out." }

                require(password == confirmPassword) { "Passwords do not match." }

                // Create userModel with correct parameters
                // Note: userModel constructor now expects (username, email, password, isLoggedIn)
                val userModel = userModel(username, email, password, false)
                val databaseHelper = Identifier(this@Register_page)
                val isInserted = databaseHelper.addOne(userModel)

                if (isInserted) {
                    // Navigate to Login_page on successful registration
                    startActivity(
                        Intent(
                            this@Register_page,
                            Login_page::class.java
                        )
                    )
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                    finish() // Close Register_page
                } else {
                    throw Exception("Registration failed. Please try again.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        loginLinkTextView.setOnClickListener {
            // Navigate to Login_page
            startActivity(Intent(this@Register_page, Login_page::class.java))
        }
    }
}