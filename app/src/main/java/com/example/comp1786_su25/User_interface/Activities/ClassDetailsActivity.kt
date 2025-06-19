package com.example.comp1786_su25.User_interface.Activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.comp1786_su25.MVC.teacherDatabase
import com.example.comp1786_su25.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ClassDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_details)

        // Set up toolbar with back button
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Get data from intent
        val classType = intent.getStringExtra("CLASS_TYPE") ?: "Unknown Class"
        val classDate = Date(intent.getLongExtra("CLASS_DATE", System.currentTimeMillis()))
        val classDuration = intent.getIntExtra("CLASS_DURATION", 0)
        val classPrice = intent.getDoubleExtra("CLASS_PRICE", 0.0)
        val classCapacity = intent.getIntExtra("CLASS_CAPACITY", 0)
        val classDescription = intent.getStringExtra("CLASS_DESCRIPTION") ?: "No description available."
        var teacherName = intent.getStringExtra("TEACHER_NAME")

        // If teacher name wasn't passed, try to find it using the class type
        if (teacherName == null) {
            val teacherDb = teacherDatabase(this)
            val teachers = teacherDb.getAllTeachers()
            // Find a teacher that teaches this class type
            val teacher = teachers.find { it.classType == classType }
            teacherName = teacher?.name ?: "Unknown Teacher"
        }

        // Initialize views
        val classNameTextView: TextView = findViewById(R.id.classNameTextView)
        val classDateTextView: TextView = findViewById(R.id.classDateTextView)
        val classTimeTextView: TextView = findViewById(R.id.classTimeTextView)
        val classCapacityTextView: TextView = findViewById(R.id.classCapacityTextView)
        val classPriceTextView: TextView = findViewById(R.id.classPriceTextView)
        val classDescriptionTextView: TextView = findViewById(R.id.classDescriptionTextView)
        val teacherNameTextView: TextView = findViewById(R.id.teacherNameTextView)

        // Set data to views
        classNameTextView.text = classType

        teacherNameTextView.text = teacherName

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
