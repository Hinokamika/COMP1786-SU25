package com.example.comp1786_su25.repository

import com.example.comp1786_su25.Models.teacherModel
import com.example.comp1786_su25.MVC.teacherDatabase
import com.example.comp1786_su25.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository that handles teacher data operations.
 * It provides a clean API to the rest of the app for app data.
 */
class TeacherRepository(private val teacherDb: teacherDatabase) {

    // Get all teachers from API and save to local database
    suspend fun refreshTeachers() {
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.getAllTeachers()

                if (response.isSuccessful) {
                    response.body()?.let { apiTeachers ->
                        // Convert API models to local database models and save them
                        for (apiTeacher in apiTeachers) {
                            teacherDb.insertTeacher(apiTeacher.toTeacherModel())
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle errors (log, retry policy, etc.)
                e.printStackTrace()
            }
        }
    }

    // Get teacher by ID from API
    suspend fun getTeacherByIdFromApi(id: Int): teacherModel? {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.getTeacherById(id)
                if (response.isSuccessful) {
                    return@withContext response.body()?.toTeacherModel()
                }
                return@withContext null
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext null
            }
        }
    }

    // Get teachers by class type from API
    suspend fun getTeachersByClassTypeFromApi(classType: String): List<teacherModel> {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.getTeachersByClassType(classType)
                if (response.isSuccessful) {
                    return@withContext response.body()?.map { it.toTeacherModel() } ?: emptyList()
                }
                return@withContext emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext emptyList()
            }
        }
    }

    // Get all teachers from local database
    suspend fun getTeachersFromLocalDb(): List<teacherModel> {
        return withContext(Dispatchers.IO) {
            teacherDb.getAllTeachers()
        }
    }

    // Get teacher by ID from local database
    suspend fun getTeacherByIdFromLocalDb(id: Int): teacherModel? {
        return withContext(Dispatchers.IO) {
            teacherDb.getTeacherById(id)
        }
    }
}
