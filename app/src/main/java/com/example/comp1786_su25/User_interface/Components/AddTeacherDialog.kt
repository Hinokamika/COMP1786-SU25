package com.example.comp1786_su25.User_interface.Components

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.comp1786_su25.Models.teacherModel
import com.example.comp1786_su25.Models.classModel
import com.example.comp1786_su25.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.app.DatePickerDialog
import android.app.AlertDialog

class AddTeacherDialog(private val existingClass: classModel?) : DialogFragment() {

    // Form UI elements
    private lateinit var editTeacherName: EditText
    private lateinit var radioGroupGender: RadioGroup
    private lateinit var editDateOfBirth: EditText
    private lateinit var spinnerTeacherClassType: Spinner
    private lateinit var btnTeacherSubmit: Button
    private lateinit var btnTeacherCancel: Button
    private lateinit var btnPickDateOfBirth: Button
    private lateinit var popupView: View

    // Calendar for date selection
    private val calendar = Calendar.getInstance()

    // Interface for communicating with parent
    interface TeacherDialogListener {
        fun onTeacherAdded(updatedClass: classModel)
    }

    private var listener: TeacherDialogListener? = null

    fun setTeacherDialogListener(listener: TeacherDialogListener) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Inflate the layout
        val inflater = LayoutInflater.from(requireContext())
        popupView = inflater.inflate(R.layout.dialog_add_teacher, null)

        // Initialize UI elements
        initializeUI()
        setupListeners()

        // Create the dialog
        return AlertDialog.Builder(requireContext())
            .setView(popupView)
            .create()
    }

    private fun initializeUI() {
        // Find views
        editTeacherName = popupView.findViewById(R.id.editTeacherName)
        radioGroupGender = popupView.findViewById(R.id.radioGroupGender)
        editDateOfBirth = popupView.findViewById(R.id.editDateOfBirth)
        spinnerTeacherClassType = popupView.findViewById(R.id.spinnerTeacherClassType)
        btnTeacherSubmit = popupView.findViewById(R.id.btnTeacherSubmit)
        btnTeacherCancel = popupView.findViewById(R.id.btnTeacherCancel)
        btnPickDateOfBirth = popupView.findViewById(R.id.btnPickDateOfBirth)

        // Setup spinner with class types
        val classTypes = arrayOf("Flow Yoga", "Aerial Yoga", "Family Yoga")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            classTypes
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTeacherClassType.adapter = adapter

        // If the class has a specific type, select it as default
        existingClass?.let {
            val classTypeIndex = classTypes.indexOf(it.class_type)
            if (classTypeIndex >= 0) {
                spinnerTeacherClassType.setSelection(classTypeIndex)
            }
        }
    }

    private fun setupListeners() {
        // Date picker button
        btnPickDateOfBirth.setOnClickListener {
            showDatePickerDialog()
        }

        // Submit button
        btnTeacherSubmit.setOnClickListener {
            if (validateInputs()) {
                addTeacherToClass()
            }
        }

        // Cancel button
        btnTeacherCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun showDatePickerDialog() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                updateDateDisplay()
            },
            calendar.get(Calendar.YEAR) - 30, // Default to 30 years ago
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateDisplay() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        editDateOfBirth.setText(dateFormat.format(calendar.time))
    }

    private fun validateInputs(): Boolean {
        // Check if name is entered
        if (editTeacherName.text.isNullOrEmpty()) {
            Toast.makeText(context, "Please enter teacher name", Toast.LENGTH_SHORT).show()
            return false
        }

        // Check if gender is selected
        if (radioGroupGender.checkedRadioButtonId == -1) {
            Toast.makeText(context, "Please select gender", Toast.LENGTH_SHORT).show()
            return false
        }

        // Check if date of birth is entered
        if (editDateOfBirth.text.isNullOrEmpty()) {
            Toast.makeText(context, "Please select date of birth", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun addTeacherToClass() {
        try {
            // Get gender from radio buttons
            val gender = when (radioGroupGender.checkedRadioButtonId) {
                R.id.radioMale -> "Male"
                R.id.radioFemale -> "Female"
                else -> ""
            }

            // Create teacher object
            val teacher = teacherModel(
                name = editTeacherName.text.toString(),
                gender = gender,
                dateOfBirth = calendar.time,
                classType = spinnerTeacherClassType.selectedItem.toString()
            )

            // Create a temporary class model if existingClass is null
            val resultClass = existingClass ?: classModel(
                day_of_week = java.sql.Date(System.currentTimeMillis()),
                time_of_course = 0,
                capacity = 0,
                duration = 0,
                price = 0.0,
                class_type = spinnerTeacherClassType.selectedItem.toString(),
                description = "",
                teacher = null
            )

            // Assign teacher to the class
            resultClass.teacher = teacher

            // Notify listener
            listener?.onTeacherAdded(resultClass)

            // Close dialog
            dismiss()
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Error adding teacher: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        const val TAG = "AddTeacherDialog"

        fun newInstance(classModel: classModel?): AddTeacherDialog {
            return AddTeacherDialog(classModel)
        }
    }
}
