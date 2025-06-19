package com.example.comp1786_su25.api

import com.example.comp1786_su25.Models.Serializable.ApiTeacherModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Get all teachers
    @GET("teachers")
    suspend fun getAllTeachers(): Response<List<ApiTeacherModel>>

    // Get teacher by ID
    @GET("teachers/{id}")
    suspend fun getTeacherById(@Path("id") id: Int): Response<ApiTeacherModel>

    // Get teachers by class type
    @GET("teachers")
    suspend fun getTeachersByClassType(@Query("classType") classType: String): Response<List<ApiTeacherModel>>
}
