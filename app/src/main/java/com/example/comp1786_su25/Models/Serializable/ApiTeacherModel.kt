package com.example.comp1786_su25.Models.Serializable

import com.example.comp1786_su25.Models.teacherModel
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * API-specific model for teacher data
 * This model is designed specifically for API communication and serialization
 */
@Serializable
data class ApiTeacherModel(
    val id: Int? = null,
    val name: String,
    val gender: String,
    val dateOfBirthString: String, // Store date as string for serialization
    val classType: String,
    val specializations: List<String> = emptyList(),
    val experience: Int
) {
    /**
     * Convert API model to local database model
     */
    fun toTeacherModel(): teacherModel {
        // Parse date from string
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateOfBirth = try {
            dateFormat.parse(dateOfBirthString) ?: Date()
        } catch (e: Exception) {
            Date()
        }

        return teacherModel(
            name = name,
            gender = gender,
            dateOfBirth = dateOfBirth,
            classType = classType,
            specializations = specializations,
            experience = experience
        )
    }

    companion object {
        /**
         * Convert local database model to API model
         */
        fun fromTeacherModel(teacher: teacherModel): ApiTeacherModel {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateOfBirthString = dateFormat.format(teacher.dateOfBirth)

            return ApiTeacherModel(
                name = teacher.name,
                gender = teacher.gender,
                dateOfBirthString = dateOfBirthString,
                classType = teacher.classType,
                specializations = teacher.specializations,
                experience = teacher.experience
            )
        }
    }
}
