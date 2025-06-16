package com.example.comp1786_su25.User_interface.Activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.comp1786_su25.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ClassDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_details)

        // Set up action bar with back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Class Details"

        // Get data from intent
        val classType = intent.getStringExtra("CLASS_TYPE") ?: "Unknown Class"
        val classDate = Date(intent.getLongExtra("CLASS_DATE", System.currentTimeMillis()))
        val classDuration = intent.getIntExtra("CLASS_DURATION", 0)
        val classPrice = intent.getDoubleExtra("CLASS_PRICE", 0.0)
        val classCapacity = intent.getIntExtra("CLASS_CAPACITY", 0)
        val classDescription = intent.getStringExtra("CLASS_DESCRIPTION") ?: "No description available."

        // Initialize views
        val classNameTextView: TextView = findViewById(R.id.classNameTextView)
        val classDateTextView: TextView = findViewById(R.id.classDateTextView)
        val classTimeTextView: TextView = findViewById(R.id.classTimeTextView)
        val classCapacityTextView: TextView = findViewById(R.id.classCapacityTextView)
        val classPriceTextView: TextView = findViewById(R.id.classPriceTextView)
        val classDescriptionTextView: TextView = findViewById(R.id.classDescriptionTextView)
        val bookClassButton: Button = findViewById(R.id.bookClassButton)

        // Set data to views
        classNameTextView.text = classType

        // Format date
        val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        classDateTextView.text = dateFormat.format(classDate)

        // Format time
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val timeText = "${timeFormat.format(classDate)} ($classDuration minutes)"
        classTimeTextView.text = timeText

        // Set capacity
        classCapacityTextView.text = "$classCapacity spots available"

        // Set price
        classPriceTextView.text = "$$classPrice"

        // Set description
        classDescriptionTextView.text = classDescription

        // Book button click listener
        bookClassButton.setOnClickListener {
            Toast.makeText(this, "Class booked successfully!", Toast.LENGTH_SHORT).show()
            // You would typically add code here to update database, decrease available spots, etc.

            // Close the activity after booking
            finish()
        }
    }

    // Handle back button in action bar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
