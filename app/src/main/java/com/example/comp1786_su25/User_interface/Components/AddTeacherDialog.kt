package com.example.comp1786_su25.User_interface.Components

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
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
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class AddTeacherDialog(private val existingClass: classModel?) : DialogFragment() {

    // Form UI elements
    private lateinit var editTeacherName: EditText
    private lateinit var radioGroupGender: RadioGroup
    private lateinit var editDateOfBirth: EditText
    private lateinit var txtSelectedSpecializations: TextView
    private lateinit var chipGroupSpecializations: ChipGroup
    private lateinit var btnTeacherSubmit: Button
    private lateinit var btnTeacherCancel: Button
    private lateinit var btnPickDateOfBirth: Button
    private lateinit var popupView: View
    private lateinit var editAdditionalInfo: EditText

    // Store selected specializations
    private val selectedSpecializations = mutableListOf<String>()
    private val availableSpecializations = arrayOf("Flow Yoga", "Aerial Yoga", "Family Yoga")

    // Calendar for date selection
    private val calendar = Calendar.getInstance()

    // Interface for communicating with parent
    interface DataRefreshListener {
        fun onDataChanged()
    }

    private var dataRefreshListener: DataRefreshListener? = null

    fun setDataRefreshListener(listener: DataRefreshListener) {
        this.dataRefreshListener = listener
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
        txtSelectedSpecializations = popupView.findViewById(R.id.txtSelectedSpecializations)
        chipGroupSpecializations = popupView.findViewById(R.id.chipGroupSpecializations)
        btnTeacherSubmit = popupView.findViewById(R.id.btnTeacherSubmit)
        btnTeacherCancel = popupView.findViewById(R.id.btnTeacherCancel)
        btnPickDateOfBirth = popupView.findViewById(R.id.btnPickDateOfBirth)

        // Initialize editAdditionalInfo - fix for UninitializedPropertyAccessException
        editAdditionalInfo = popupView.findViewById(R.id.editAdditionalInfo) ?: EditText(requireContext()).apply {
            setText("0") // Default value for experience
        }

        // Make the specialization text view clickable to show selection options
        txtSelectedSpecializations.setOnClickListener {
            showSpecializationSelectionDialog()
        }

        // Setup chips for specializations
        setupSpecializationChips()

        // If the class has a specific type, select it as default
        existingClass?.let {
            // Set teacher name
            editTeacherName.setText(it.teacher?.name)

            // Set gender
            val genderRadioButtonId = if (it.teacher?.gender == "Male") R.id.radioMale else R.id.radioFemale
            radioGroupGender.check(genderRadioButtonId)

            // Set date of birth
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            editDateOfBirth.setText(dateFormat.format(it.teacher?.dateOfBirth ?: System.currentTimeMillis()))

            // Set specializations if available
            it.teacher?.specializations?.let { specs ->
                selectedSpecializations.clear()
                selectedSpecializations.addAll(specs)
                updateSpecializationText()
                updateSpecializationChips()
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

    private fun showSpecializationSelectionDialog() {
        val checkedItems = BooleanArray(availableSpecializations.size) {
            selectedSpecializations.contains(availableSpecializations[it])
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Select Specializations")
            .setMultiChoiceItems(availableSpecializations, checkedItems) { _, which, isChecked ->
                val specialization = availableSpecializations[which]
                if (isChecked) {
                    if (!selectedSpecializations.contains(specialization)) {
                        selectedSpecializations.add(specialization)
                    }
                } else {
                    selectedSpecializations.remove(specialization)
                }
            }
            .setPositiveButton("OK") { _, _ ->
                updateSpecializationText()
                updateSpecializationChips()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupSpecializationChips() {
        // Clear existing chips
        chipGroupSpecializations.removeAllViews()

        // Create and add a chip for each selected specialization
        selectedSpecializations.forEach { specialization ->
            addChip(specialization)
        }
    }

    private fun updateSpecializationChips() {
        // Clear existing chips
        chipGroupSpecializations.removeAllViews()

        // Create and add a chip for each selected specialization
        selectedSpecializations.forEach { specialization ->
            addChip(specialization)
        }
    }

    private fun addChip(specialization: String) {
        val chip = Chip(requireContext()).apply {
            text = specialization
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                selectedSpecializations.remove(specialization)
                chipGroupSpecializations.removeView(this)
                updateSpecializationText()
            }
        }
        chipGroupSpecializations.addView(chip)
    }

    private fun updateSpecializationText() {
        if (selectedSpecializations.isEmpty()) {
            txtSelectedSpecializations.text = ""
            txtSelectedSpecializations.hint = "Select specializations"
        } else {
            txtSelectedSpecializations.text = selectedSpecializations.joinToString(", ")
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

        // Check if at least one specialization is selected
        if (selectedSpecializations.isEmpty()) {
            Toast.makeText(context, "Please select at least one specialization", Toast.LENGTH_SHORT).show()
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
                classType = selectedSpecializations.firstOrNull() ?: "",
                experience = editAdditionalInfo.text.toString().toIntOrNull() ?: 0,
            )

            // Insert teacher into database
            val db = com.example.comp1786_su25.MVC.teacherDatabase(requireContext())
            db.insertTeacher(teacher)

            // Create a temporary class model if existingClass is null
            val resultClass = existingClass ?: classModel(
                day_of_week = java.sql.Date(System.currentTimeMillis()),
                time_of_course = 0,
                capacity = 0,
                duration = 0,
                price = 0.0,
                class_type = selectedSpecializations.firstOrNull() ?: "",
                description = "",
                teacher = null
            )

            // Assign teacher to the class
            resultClass.teacher = teacher

            // Notify listener
            dataRefreshListener?.onDataChanged()

            // Close dialog
            dismiss()
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Error adding teacher: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
            android.util.Log.e("AddTeacherDialog", "Error adding teacher", e)
            e.printStackTrace() // This will print the full stack trace
        }
    }

    companion object {
        const val TAG = "AddTeacherDialog"

        fun newInstance(classModel: classModel?): AddTeacherDialog {
            return AddTeacherDialog(classModel)
        }
    }
}
