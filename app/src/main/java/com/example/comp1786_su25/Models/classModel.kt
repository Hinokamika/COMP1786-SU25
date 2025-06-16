package com.example.comp1786_su25.Models

import java.sql.Date

class classModel (
    var day_of_week : Date,
    var time_of_course : Int,
    var capacity : Int,
    var duration : Int,
    var price : Double,
    var class_type : String,
    var description: String,
    var teacher: teacherModel? = null
){
    override fun toString(): String {
        return "classModel{" +
                "day_of_week=$day_of_week" +
                ", time_of_course=$time_of_course" +
                ", capacity=$capacity" +
                ", duration=$duration" +
                ", price=$price" +
                ", class_type='$class_type'" +
                ", description='$description'" +
                ", teacher=$teacher" +
                "}"
    }
}
