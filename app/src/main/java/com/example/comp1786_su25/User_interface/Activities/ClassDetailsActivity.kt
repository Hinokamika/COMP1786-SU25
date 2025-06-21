package com.example.comp1786_su25.User_interface.Activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.comp1786_su25.MVC.teacherDatabase
import com.example.comp1786_su25.R
import com.example.comp1786_su25.User_interface.Components.Update.UpdateClassDialog
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

        // Get class ID from intent (new approach)
        val classId = intent.getIntExtra("CLASS_ID", -1)

        // Keep the position for backward compatibility
        val classPosition = intent.getIntExtra("CLASS_POSITION", -1)

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

        // Set up the update button if available
        val updateClassButton: Button? = findViewById(R.id.updateClassButton)
        updateClassButton?.setOnClickListener {
            val dialog = UpdateClassDialog()
            val bundle = Bundle()

            // Use class ID if available, otherwise fall back to position
            val idToPass = if (classId != -1) classId else classPosition
            bundle.putInt("CLASS_ID", idToPass)

            dialog.arguments = bundle
            dialog.show(supportFragmentManager, "UpdateClassDialog")
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
