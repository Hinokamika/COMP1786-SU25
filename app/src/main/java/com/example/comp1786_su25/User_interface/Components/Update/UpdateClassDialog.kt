package com.example.comp1786_su25.User_interface.Components.Update

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.comp1786_su25.MVC.insertClass
import com.example.comp1786_su25.MVC.teacherDatabase
import com.example.comp1786_su25.Models.classModel
import com.example.comp1786_su25.R
import com.example.comp1786_su25.User_interface.Components.AddClassDialog
import com.example.comp1786_su25.User_interface.Components.AddClassDialog.DataRefreshListener
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UpdateClassDialog: DialogFragment() {

    private lateinit var spinnerDayOfWeek: Spinner
    private lateinit var editDateOfCourse: EditText
    private lateinit var editTimeOfCourse: EditText
    private lateinit var editCapacity: EditText
    private lateinit var editDuration: EditText
    private lateinit var editPrice: EditText
    private lateinit var spinnerClassType: Spinner
    private lateinit var spinnerTeacher: Spinner
    private lateinit var editDescription: EditText
    private lateinit var btnSubmit: Button
    private lateinit var btnCancel: Button
    private lateinit var btnPickDateTime: Button
    private lateinit var popupView: View

    private lateinit var dbHelper: insertClass
    private lateinit var teacherDb: teacherDatabase

    private val calendar = Calendar.getInstance()
    private var classId: Int = -1
    private var currentClass: classModel? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCanceledOnTouchOutside(false) // Prevent dismissal when touching outside
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_update_class_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the database helper
        dbHelper = insertClass(requireContext())

        // Get the class ID from arguments - now using CLASS_ID instead of CLASS_POSITION
        classId = arguments?.getInt("CLASS_ID", -1) ?: -1

        // Initialize the popup view
        popupView = view.findViewById(R.id.popup_window_view_with_border)

        // Initialize views
        initViews(view)

        // Load class types into spinner
        loadClassTypes()

        // Load days of week into spinner
        loadDaysOfWeek()

        // Set click listeners
        setupClickListeners()

        // Load class data after spinners are initialized
        loadClassData()

        // Animate popup appearance
        animatePopupAppearance()
    }

    private fun initViews(view: View) {
        spinnerDayOfWeek = view.findViewById(R.id.spinner_day_of_week)
        editDateOfCourse = view.findViewById(R.id.edit_date_of_course)
        editTimeOfCourse = view.findViewById(R.id.edit_time_of_course)
        editCapacity = view.findViewById(R.id.edit_capacity)
        editDuration = view.findViewById(R.id.edit_duration)
        editPrice = view.findViewById(R.id.edit_price)
        spinnerClassType = view.findViewById(R.id.spinner_class_type)
        spinnerTeacher = view.findViewById(R.id.spinner_teacher)
        editDescription = view.findViewById(R.id.edit_description)
        btnSubmit = view.findViewById(R.id.btn_submit)
        btnCancel = view.findViewById(R.id.btn_cancel)
        btnPickDateTime = view.findViewById(R.id.btn_pick_date_time)
        spinnerTeacher = view.findViewById(R.id.spinner_teacher)

        // Set current date and time in the fields
        updateDateInView()
        updateTimeInView()

        // Load teacher data into spinner
        loadTeacherData()
    }

    private fun loadClassData() {
        if (classId != -1) {
            // Add debug toast
            Toast.makeText(context, "Loading class with ID: $classId", Toast.LENGTH_SHORT).show()

            // Get the class data from the database using ID
            currentClass = dbHelper.getClassById(classId)

            // Debug if class was found
            if (currentClass == null) {
                Toast.makeText(context, "Error: Class not found with ID: $classId", Toast.LENGTH_SHORT).show()
                return
            }

            // Populate the form fields with the class data
            currentClass?.let { classData ->
                try {
                    // Set date
                    val date = classData.day_of_week
                    val cal = Calendar.getInstance()
                    cal.time = date
                    calendar.set(Calendar.YEAR, cal.get(Calendar.YEAR))
                    calendar.set(Calendar.MONTH, cal.get(Calendar.MONTH))
                    calendar.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH))
                    updateDateInView()

                    // Set time
                    val timeValue = classData.time_of_course
                    val hour = timeValue / 100
                    val minute = timeValue % 100
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    updateTimeInView()

                    // Set day of week spinner
                    updateDayOfWeekSpinner()

                    // Set other form fields
                    editCapacity.setText(classData.capacity.toString())
                    editDuration.setText(classData.duration.toString())
                    editPrice.setText(classData.price.toString())
                    editDescription.setText(classData.description)

                    // Ensure spinners are loaded before setting selection
                    // First verify class type is valid
                    val classTypes = resources.getStringArray(R.array.yoga_class_types)
                    val classTypePosition = classTypes.indexOf(classData.class_type)
                    if (classTypePosition != -1) {
                        spinnerClassType.post {
                            spinnerClassType.setSelection(classTypePosition)
                        }
                    } else {
                        Toast.makeText(context, "Warning: Class type '${classData.class_type}' not found in options", Toast.LENGTH_SHORT).show()
                    }

                    // Set teacher spinner if a teacher is associated
                    classData.teacher?.let { teacher ->
                        spinnerTeacher.post {
                            val adapter = spinnerTeacher.adapter as ArrayAdapter<*>
                            val teacherPosition = (0 until adapter.count).firstOrNull {
                                adapter.getItem(it).toString() == teacher.name
                            }
                            if (teacherPosition != null) {
                                spinnerTeacher.setSelection(teacherPosition)
                            } else {
                                Toast.makeText(context, "Warning: Teacher '${teacher.name}' not found in options", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    // Confirm data was loaded
                    Toast.makeText(context, "Class data loaded successfully", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Error loading class data: ${e.message}", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
        } else {
            Toast.makeText(context, "Error: Invalid class ID", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    private fun loadClassTypes() {
        // Load class types into spinner
        val classTypes = resources.getStringArray(R.array.yoga_class_types)
        val classTypeAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            classTypes
        )
        classTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerClassType.adapter = classTypeAdapter
    }

    private fun loadDaysOfWeek() {
        // Load day of week into spinner
        val daysOfWeek = resources.getStringArray(R.array.days_of_week)
        val dayOfWeekAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            daysOfWeek
        )
        dayOfWeekAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDayOfWeek.adapter = dayOfWeekAdapter
    }

    private fun loadTeacherData() {
        // Get all teacher names from the database
        teacherDb = teacherDatabase(requireContext())
        val teacherList = teacherDb.getAllTeachers()

        // Extract just the names for the spinner
        val teacherNames = teacherList.map { it.name }

        // Create adapter for teacher spinner
        val teacherAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            teacherNames
        )
        teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTeacher.adapter = teacherAdapter
    }

    private fun setupClickListeners() {
        // Date and time picker
        btnPickDateTime.setOnClickListener {
            showDatePicker()
        }

        // Cancel button
        btnCancel.setOnClickListener {
            dismiss()
        }

        // Submit button
        btnSubmit.setOnClickListener {
            if (validateForm()) {
                updateClass()
            }
        }
    }

    private fun showDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(Calendar.YEAR, selectedYear)
                calendar.set(Calendar.MONTH, selectedMonth)
                calendar.set(Calendar.DAY_OF_MONTH, selectedDay)
                updateDateInView()

                // After selecting date, show time picker
                showTimePicker()
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
                updateTimeInView()

                // Also update the day of week spinner based on selected date
                updateDayOfWeekSpinner()
            },
            hour, minute, false
        )
        timePickerDialog.show()
    }

    private fun updateDateInView() {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        editDateOfCourse.setText(sdf.format(calendar.time))
    }

    private fun updateTimeInView() {
        val myFormat = "HH:mm"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        editTimeOfCourse.setText(sdf.format(calendar.time))
    }

    private fun updateDayOfWeekSpinner() {
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        // Adjust to match spinner position (Calendar.SUNDAY=1, but our spinner has Monday at position 0)
        val spinnerPosition = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - 2
        spinnerDayOfWeek.setSelection(spinnerPosition)
    }

    private fun validateForm(): Boolean {
        // Validate date
        if (editDateOfCourse.text.toString().isEmpty()) {
            editDateOfCourse.error = "Date is required"
            return false
        }

        // Validate time
        if (editTimeOfCourse.text.toString().isEmpty()) {
            editTimeOfCourse.error = "Time is required"
            return false
        }

        // Validate capacity
        if (editCapacity.text.toString().isEmpty()) {
            editCapacity.error = "Capacity is required"
            return false
        }

        // Validate duration
        if (editDuration.text.toString().isEmpty()) {
            editDuration.error = "Duration is required"
            return false
        }

        // Validate price
        if (editPrice.text.toString().isEmpty()) {
            editPrice.error = "Price is required"
            return false
        }

        // Validate description
        if (editDescription.text.toString().isEmpty()) {
            editDescription.error = "Description is required"
            return false
        }

        return true
    }

    private fun updateClass() {
        try {
            // Get values from form
            val dayOfWeek = getDayOfWeekFromDateField()
            val timeOfCourse = getTimeAsInt()
            val capacity = editCapacity.text.toString().toInt()
            val duration = editDuration.text.toString().toInt()
            val price = editPrice.text.toString().toDouble()
            val classType = spinnerClassType.selectedItem.toString()
            val description = editDescription.text.toString()

            // Get selected teacher if any
            val teacherName = spinnerTeacher.selectedItem?.toString()
            var teacherModel: com.example.comp1786_su25.Models.teacherModel? = null

            // Get the teacher model object if a teacher was selected
            if (teacherName != null && teacherName != "No Teacher") {
                teacherDb = teacherDatabase(requireContext())
                teacherModel = teacherDb.getTeacherByName(teacherName)
            }

            // Create class model object with the existing ID
            val updatedClass = classModel(
                id = classId, // Keep the same ID for updating
                day_of_week = dayOfWeek,
                time_of_course = timeOfCourse,
                price = price,
                class_type = classType,
                description = description,
                capacity = capacity,
                duration = duration,
                teacher = teacherModel
            )

            // Update the class in the database
            val result = dbHelper.updateClass(classId, updatedClass)

            // Notify listener
            if (result) {
                Toast.makeText(context, "Class updated successfully!", Toast.LENGTH_SHORT).show()

                animateSuccessSubmission {
                    // Notify parent fragment to refresh data
                    (parentFragment as? DataRefreshListener)?.onDataChanged()
                    // Or use activity
                    (activity as? DataRefreshListener)?.onDataChanged()
                    dismiss()
                }
            } else {
                Toast.makeText(context, "Failed to update class", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun getDayOfWeekFromDateField(): Date {
        val dateString = editDateOfCourse.text.toString()
        return Date.valueOf(dateString)
    }

    private fun getTimeAsInt(): Int {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return hour * 100 + minute
    }

    private fun animatePopupAppearance() {
        // Initially make the popup invisible
        popupView.alpha = 0f

        // Create a fade-in animation
        val fadeIn = ObjectAnimator.ofFloat(popupView, "alpha", 0f, 1f)
        fadeIn.duration = 400
        val animatorSet = AnimatorSet()
        // Create a slight scale animation
        val scaleX = ObjectAnimator.ofFloat(popupView, "scaleX", 0.9f, 1f)
        val scaleY = ObjectAnimator.ofFloat(popupView, "scaleY", 0.9f, 1f)
        animatorSet.playTogether(scaleX, scaleY, fadeIn)
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.duration = 400
        animatorSet.start()
    }

    private fun animateSuccessSubmission(onEnd: () -> Unit) {
        // Get all form fields to animate
        val formFields = listOf(
            spinnerDayOfWeek,
            editDateOfCourse,
            editTimeOfCourse,
            editCapacity,
            editDuration,
            editPrice,
            spinnerClassType,
            editDescription,
            btnSubmit,
            btnCancel
        )

        // Create a fade-out animation for each field with slight delay between each
        var delay = 0L
        val animators = mutableListOf<ObjectAnimator>()

        formFields.forEach { view ->
            val fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)
            fadeOut.startDelay = delay
            fadeOut.duration = 100
            animators.add(fadeOut)
            delay += 50
        }

        // Final fade out for the popup itself
        val popupFadeOut = ObjectAnimator.ofFloat(popupView, "alpha", 1f, 0f)
        popupFadeOut.startDelay = delay + 200
        popupFadeOut.duration = 300

        val animSet = AnimatorSet()
        animSet.playSequentially(animators + popupFadeOut)

        // Execute onEnd when animation completes
        animSet.addListener(object : android.animation.Animator.AnimatorListener {
            override fun onAnimationStart(animation: android.animation.Animator) {}
            override fun onAnimationEnd(animation: android.animation.Animator) {
                onEnd()
            }
            override fun onAnimationCancel(animation: android.animation.Animator) {}
            override fun onAnimationRepeat(animation: android.animation.Animator) {}
        })

        animSet.start()
    }

    // Interface for notifying parent about data changes
    interface DataRefreshListener {
        fun onDataChanged()
    }

    companion object {
        const val TAG = "UpdateClassDialog"

        fun newInstance(classId: Int): UpdateClassDialog {
            val dialog = UpdateClassDialog()
            val args = Bundle()
            args.putInt("CLASS_ID", classId) // Changed from CLASS_POSITION to CLASS_ID
            dialog.arguments = args
            return dialog
        }
    }
}