package com.example.comp1786_su25.User_interface.Adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.comp1786_su25.Models.teacherModel
import com.example.comp1786_su25.R
import com.example.comp1786_su25.User_interface.Activities.TeacherDetailsDialog
import java.text.SimpleDateFormat
import java.util.Locale

class TeacherAdapter(private var teachers: List<teacherModel>) :
        RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder>() {

    class TeacherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val teacherCard: CardView = itemView.findViewById(R.id.teacherItemCard)
        val teacherName: TextView = itemView.findViewById(R.id.teacherNameTextView)
        val gender: TextView = itemView.findViewById(R.id.genderTextView)
        val specialization: TextView = itemView.findViewById(R.id.specializationTextView)
        val rating: TextView = itemView.findViewById(R.id.ratingTextView)
        val experience: TextView = itemView.findViewById(R.id.experienceTextView)
        val profileImage: ImageView = itemView.findViewById(R.id.teacherProfileImage)
        val dob: TextView = itemView.findViewById(R.id.dobTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_teacher_adapter, parent, false)
        return TeacherViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeacherViewHolder, position: Int) {
        val teacherItem = teachers[position]

        // Name
        holder.teacherName.text = teacherItem.name
        // Gender
        holder.gender.text = teacherItem.gender
        // Specialization (show all as comma separated if list, fallback to classType if empty)
        val specializationText = if (teacherItem.specializations.isNullOrEmpty()) {
            teacherItem.classType
        } else {
            teacherItem.specializations.joinToString(", ")
        }
        holder.specialization.text = specializationText
        // Date of Birth (formatted)
        val year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) - SimpleDateFormat("yyyy", Locale.getDefault()).format(teacherItem.dateOfBirth).toInt()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        holder.dob.text = "Date of Birth: ${dateFormat.format(teacherItem.dateOfBirth)} (${year} years old)"
        // Experience (example: years since dateOfBirth)
        holder.experience.text = "${teacherItem.experience} years experience"
        // Rating (if available, else show N/A)
        holder.rating.text = "N/A"
        // Profile image is static for now

        // Set click listener to show teacher details dialog
        holder.teacherCard.setOnClickListener {
            val context = holder.itemView.context
            if (context is AppCompatActivity) {
                val dialog = TeacherDetailsDialog.newInstance(teacherItem)
                dialog.show(context.supportFragmentManager, "TeacherDetailsDialog")
            }
        }
    }

    override fun getItemCount(): Int = teachers.size

    fun updateData(newTeachers: List<teacherModel>) {
        teachers = newTeachers
        notifyDataSetChanged()
    }
}
