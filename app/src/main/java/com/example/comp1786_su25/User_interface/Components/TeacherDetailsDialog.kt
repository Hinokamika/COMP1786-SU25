package com.example.comp1786_su25.User_interface.Components

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.comp1786_su25.Models.teacherModel
import com.example.comp1786_su25.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TeacherDetailsDialog : DialogFragment() {

    companion object {
        private const val ARG_TEACHER = "teacher_details"

        fun newInstance(teacher: teacherModel): TeacherDetailsDialog {
            val fragment = TeacherDetailsDialog()
            val args = Bundle().apply {
                putString("name", teacher.name)
                putString("gender", teacher.gender)
                putLong("dateOfBirth", teacher.dateOfBirth.time)
                putString("classType", teacher.classType)
                putInt("experience", teacher.experience)
                putStringArray("specializations", teacher.specializations.toTypedArray())
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_teacher_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get views
        val nameTextView: TextView = view.findViewById(R.id.detailsTeacherNameTextView)
        val genderTextView: TextView = view.findViewById(R.id.detailsGenderTextView)
        val specializationTextView: TextView = view.findViewById(R.id.detailsSpecializationTextView)
        val experienceTextView: TextView = view.findViewById(R.id.detailsExperienceTextView)
        val dobTextView: TextView = view.findViewById(R.id.detailsDobTextView)
        val profileImageView: ImageView = view.findViewById(R.id.detailsTeacherProfileImage)
        val closeButton: Button = view.findViewById(R.id.closeButton)

        // Get data from arguments
        arguments?.let { args ->
            val name = args.getString("name", "")
            val gender = args.getString("gender", "")
            val dateOfBirth = args.getLong("dateOfBirth", 0)
            val classType = args.getString("classType", "")
            val experience = args.getInt("experience", 0)
            val specializations = args.getStringArray("specializations") ?: arrayOf()

            // Set data to views
            nameTextView.text = name
            genderTextView.text = gender

            // Format specializations
            val specializationText = if (specializations.isEmpty()) {
                classType
            } else {
                specializations.joinToString(", ")
            }
            specializationTextView.text = specializationText

            // Format experience
            experienceTextView.text = "$experience years experience"

            // Format date of birth
            val dobDate = java.util.Date(dateOfBirth)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val year = Calendar.getInstance().get(Calendar.YEAR) -
                       SimpleDateFormat("yyyy", Locale.getDefault()).format(dobDate).toInt()
            dobTextView.text = "${dateFormat.format(dobDate)} ($year years old)"

            // Set profile image based on gender (you could use a proper image loading library in a real app)
            profileImageView.setImageResource(
                if (gender.equals("Male", ignoreCase = true))
                    android.R.drawable.ic_menu_camera
                else
                    android.R.drawable.ic_menu_myplaces
            )
        }

        // Set up close button
        closeButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        // Make dialog fill most of the screen width
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
