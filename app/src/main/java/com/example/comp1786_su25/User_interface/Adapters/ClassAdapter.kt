package com.example.comp1786_su25.User_interface.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.comp1786_su25.R
import com.example.comp1786_su25.Models.classModel
import com.example.comp1786_su25.User_interface.Activities.ClassDetailsActivity
import com.example.comp1786_su25.User_interface.Components.Update.UpdateClassDialog
import java.text.SimpleDateFormat
import java.util.Locale

class ClassAdapter(private val classes: List<classModel>) :
    RecyclerView.Adapter<ClassAdapter.ClassViewHolder>() {

    class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val classCard: CardView = itemView.findViewById(R.id.classItemCard)
        val className: TextView = itemView.findViewById(R.id.classNameTextView)
        val classDifficulty: TextView = itemView.findViewById(R.id.classDifficultyTextView)
        val teacherName: TextView = itemView.findViewById(R.id.teacherNameTextView)
        val classTime: TextView = itemView.findViewById(R.id.classTimeTextView)
        val availability: TextView = itemView.findViewById(R.id.availabilityTextView)
        val price: TextView = itemView.findViewById(R.id.priceTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_class, parent, false)
        return ClassViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        val classItem = classes[position]

        // Bind data to the views
        // Using class_type as the name for display
        holder.className.text = classItem.class_type

        // For simplicity, we'll use a static difficulty or hide it
        holder.classDifficulty.visibility = View.GONE

        // We don't have teacher name in the new model, so we'll use a placeholder or hide it
        holder.teacherName.text = "Instructor"

        // Format and display time information
        val timeFormat = SimpleDateFormat("EEEE, MMMM d • h:mm a", Locale.getDefault())
        holder.classTime.text = timeFormat.format(classItem.day_of_week) +
                " (${classItem.duration} minutes)"

        // Show capacity as availability
        holder.availability.text = "${classItem.capacity} spots available"

        // Display price
        holder.price.text = "$${classItem.price}"

        // Set click listener to open class details
        holder.classCard.setOnClickListener {
            val intent = Intent(holder.itemView.context, ClassDetailsActivity::class.java)
            // Pass the class ID instead of position
            intent.putExtra("CLASS_ID", classItem.id)
            // Also pass the individual class data components as before
            intent.putExtra("CLASS_TYPE", classItem.class_type)
            intent.putExtra("CLASS_DATE", classItem.day_of_week.time)
            intent.putExtra("CLASS_DURATION", classItem.duration)
            intent.putExtra("CLASS_PRICE", classItem.price)
            intent.putExtra("CLASS_CAPACITY", classItem.capacity)
            intent.putExtra("CLASS_DESCRIPTION", classItem.description)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = classes.size
}
