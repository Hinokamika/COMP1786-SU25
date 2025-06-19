package com.example.comp1786_su25.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.comp1786_su25.MVC.teacherDatabase
import com.example.comp1786_su25.Models.teacherModel
import com.example.comp1786_su25.repository.TeacherRepository
import kotlinx.coroutines.launch

class TeacherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TeacherRepository

    // LiveData to observe teachers from the database
    private val _teachers = MutableLiveData<List<teacherModel>>()
    val teachers: LiveData<List<teacherModel>> = _teachers

    // LiveData for loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData for error messages
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        val teacherDb = teacherDatabase(application)
        repository = TeacherRepository(teacherDb)
        loadTeachersFromLocalDb()
    }

    // Load teachers from local database
    private fun loadTeachersFromLocalDb() {
        viewModelScope.launch {
            try {
                val localTeachers = repository.getTeachersFromLocalDb()
                _teachers.value = localTeachers
            } catch (e: Exception) {
                _errorMessage.value = "Error loading local data: ${e.message}"
            }
        }
    }

    // Refresh teachers from API and update local database
    fun refreshTeachersFromApi() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.refreshTeachers()
                // After refreshing, load the updated data from local database
                loadTeachersFromLocalDb()
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error refreshing data: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    // Get teachers by class type
    fun getTeachersByClassType(classType: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val filteredTeachers = repository.getTeachersByClassTypeFromApi(classType)
                _teachers.value = filteredTeachers
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error loading teachers by class type: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    // Get teacher by ID
    fun getTeacherById(id: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val teacher = repository.getTeacherByIdFromApi(id)
                teacher?.let {
                    _teachers.value = listOf(it)
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error loading teacher details: ${e.message}"
                _isLoading.value = false
            }
        }
    }
}
