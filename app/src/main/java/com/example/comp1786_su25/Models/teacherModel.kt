package com.example.comp1786_su25.Models

import java.util.Date

class teacherModel(
    var name: String,
    var gender: String,
    var dateOfBirth: Date,
    var classType: String
) {
    override fun toString(): String {
        return "Teacher(name='$name', gender='$gender', dateOfBirth=$dateOfBirth, classType='$classType')"
    }
}
