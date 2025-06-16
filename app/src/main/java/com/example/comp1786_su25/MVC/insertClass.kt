package com.example.comp1786_su25.MVC

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import com.example.comp1786_su25.Models.classModel
import com.example.comp1786_su25.Models.teacherModel
import java.sql.Date

class insertClass(private val context: Context) : SQLiteOpenHelper(context, "classes.db", null, 1) {

    companion object {
        const val CLASSES = "classes"
        const val DAY_OF_WEEK = "day_of_week"
        const val TIME_OF_COURSE = "time_of_course"
        const val CAPACITY = "capacity"
        const val DURATION = "duration"
        const val PRICE = "price"
        const val CLASS_TYPE = "class_type"
        const val DESCRIPTION = "description"
        const val TEACHER_NAME = "teacher_name"
        const val ID = "id"
    }

    override fun onCreate(db: android.database.sqlite.SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE IF NOT EXISTS $CLASSES (" +
                "$ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$DAY_OF_WEEK TEXT NOT NULL, " +
                "$TIME_OF_COURSE INTEGER NOT NULL, " +
                "$CAPACITY INTEGER NOT NULL, " +
                "$DURATION INTEGER NOT NULL, " +
                "$PRICE REAL NOT NULL, " +
                "$CLASS_TYPE TEXT NOT NULL, " +
                "$DESCRIPTION TEXT, " +
                "$TEACHER_NAME TEXT" +
                ");"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: android.database.sqlite.SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades if needed
    }

    /**
     * Insert a new class into the database
     * @param classModel The class data to insert
     * @return The row ID of the newly inserted class, or -1 if an error occurred
     */
    fun insertClass(classModel: classModel): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(DAY_OF_WEEK, classModel.day_of_week.toString())
            put(TIME_OF_COURSE, classModel.time_of_course)
            put(CAPACITY, classModel.capacity)
            put(DURATION, classModel.duration)
            put(PRICE, classModel.price)
            put(CLASS_TYPE, classModel.class_type)
            put(DESCRIPTION, classModel.description)
            // Store teacher name as a reference if teacher exists
            classModel.teacher?.let { teacher ->
                put(TEACHER_NAME, teacher.name)
            }
        }

        val result = db.insert(CLASSES, null, contentValues)
        db.close()
        return result
    }

    /**
     * Get all classes from the database
     * @return List of all classes
     */
    fun getAllClasses(): List<classModel> {
        val classesList = ArrayList<classModel>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $CLASSES ORDER BY $DAY_OF_WEEK, $TIME_OF_COURSE"
        val teacherDb = teacherDatabase(context)

        val cursor: Cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val dateString = cursor.getString(cursor.getColumnIndexOrThrow(DAY_OF_WEEK))
                val date = Date.valueOf(dateString)

                // Check if the class has a teacher
                var teacher: teacherModel? = null
                val teacherNameIndex = cursor.getColumnIndex(TEACHER_NAME)
                if (teacherNameIndex != -1) {
                    val teacherName = cursor.getString(teacherNameIndex)
                    if (!teacherName.isNullOrEmpty()) {
                        // Get teacher from the teacher database
                        teacher = teacherDb.getTeacherByName(teacherName)
                    }
                }

                val classItem = classModel(
                    day_of_week = date,
                    time_of_course = cursor.getInt(cursor.getColumnIndexOrThrow(TIME_OF_COURSE)),
                    capacity = cursor.getInt(cursor.getColumnIndexOrThrow(CAPACITY)),
                    duration = cursor.getInt(cursor.getColumnIndexOrThrow(DURATION)),
                    price = cursor.getDouble(cursor.getColumnIndexOrThrow(PRICE)),
                    class_type = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_TYPE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)),
                    teacher = teacher
                )
                classesList.add(classItem)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return classesList
    }

    /**
     * Get a class by its ID
     * @param id The ID of the class to retrieve
     * @return The class with the specified ID, or null if not found
     */
    fun getClassById(id: Int): classModel? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $CLASSES WHERE $ID = ?"
        val cursor = db.rawQuery(query, arrayOf(id.toString()))
        var classItem: classModel? = null
        val teacherDb = teacherDatabase(context)

        if (cursor.moveToFirst()) {
            val dateString = cursor.getString(cursor.getColumnIndexOrThrow(DAY_OF_WEEK))
            val date = Date.valueOf(dateString)

            // Check if the class has a teacher
            var teacher: teacherModel? = null
            val teacherNameIndex = cursor.getColumnIndex(TEACHER_NAME)
            if (teacherNameIndex != -1) {
                val teacherName = cursor.getString(teacherNameIndex)
                if (!teacherName.isNullOrEmpty()) {
                    // Get teacher from the teacher database
                    teacher = teacherDb.getTeacherByName(teacherName)
                }
            }

            classItem = classModel(
                day_of_week = date,
                time_of_course = cursor.getInt(cursor.getColumnIndexOrThrow(TIME_OF_COURSE)),
                capacity = cursor.getInt(cursor.getColumnIndexOrThrow(CAPACITY)),
                duration = cursor.getInt(cursor.getColumnIndexOrThrow(DURATION)),
                price = cursor.getDouble(cursor.getColumnIndexOrThrow(PRICE)),
                class_type = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_TYPE)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)),
                teacher = teacher
            )
        }
        cursor.close()
        db.close()
        return classItem
    }

    /**
     * Update an existing class in the database
     * @param id The ID of the class to update
     * @param classModel The updated class data
     * @return true if the update was successful, false otherwise
     */
    fun updateClass(id: Int, classModel: classModel): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(DAY_OF_WEEK, classModel.day_of_week.toString())
            put(TIME_OF_COURSE, classModel.time_of_course)
            put(CAPACITY, classModel.capacity)
            put(DURATION, classModel.duration)
            put(PRICE, classModel.price)
            put(CLASS_TYPE, classModel.class_type)
            put(DESCRIPTION, classModel.description)
        }

        val result = db.update(CLASSES, contentValues, "$ID = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }

    /**
     * Delete a class from the database
     * @param id The ID of the class to delete
     * @return true if the deletion was successful, false otherwise
     */
    fun deleteClass(id: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete(CLASSES, "$ID = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }

    /**
     * Get classes for a specific day
     * @param date The date to filter by
     * @return List of classes on the specified date
     */
    fun getClassesByDate(date: Date): List<classModel> {
        val classesList = ArrayList<classModel>()
        val db = this.readableDatabase
        val dateString = date.toString()

        val query = "SELECT * FROM $CLASSES WHERE $DAY_OF_WEEK = ? ORDER BY $TIME_OF_COURSE"
        val cursor: Cursor = db.rawQuery(query, arrayOf(dateString))

        if (cursor.moveToFirst()) {
            do {
                val classItem = classModel(
                    day_of_week = date,
                    time_of_course = cursor.getInt(cursor.getColumnIndexOrThrow(TIME_OF_COURSE)),
                    capacity = cursor.getInt(cursor.getColumnIndexOrThrow(CAPACITY)),
                    duration = cursor.getInt(cursor.getColumnIndexOrThrow(DURATION)),
                    price = cursor.getDouble(cursor.getColumnIndexOrThrow(PRICE)),
                    class_type = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_TYPE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION))
                )
                classesList.add(classItem)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return classesList
    }

    /**
     * Get classes by type (Flow Yoga, Aerial Yoga, Family Yoga)
     * @param classType The type of class to filter by
     * @return List of classes of the specified type
     */
    fun getClassesByType(classType: String): List<classModel> {
        val classesList = ArrayList<classModel>()
        val db = this.readableDatabase

        val query = "SELECT * FROM $CLASSES WHERE $CLASS_TYPE = ? ORDER BY $DAY_OF_WEEK, $TIME_OF_COURSE"
        val cursor: Cursor = db.rawQuery(query, arrayOf(classType))

        if (cursor.moveToFirst()) {
            do {
                val dateString = cursor.getString(cursor.getColumnIndexOrThrow(DAY_OF_WEEK))
                val date = Date.valueOf(dateString)

                val classItem = classModel(
                    day_of_week = date,
                    time_of_course = cursor.getInt(cursor.getColumnIndexOrThrow(TIME_OF_COURSE)),
                    capacity = cursor.getInt(cursor.getColumnIndexOrThrow(CAPACITY)),
                    duration = cursor.getInt(cursor.getColumnIndexOrThrow(DURATION)),
                    price = cursor.getDouble(cursor.getColumnIndexOrThrow(PRICE)),
                    class_type = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_TYPE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION))
                )
                classesList.add(classItem)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return classesList
    }

    /**
     * Update a class with teacher information
     * @param classId The ID of the class to update
     * @param teacherName The name of the teacher to assign to the class
     * @return true if successful, false otherwise
     */
    fun updateClassTeacher(classId: Int, teacherName: String?): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(TEACHER_NAME, teacherName)
        }

        val result = db.update(CLASSES, contentValues, "$ID = ?", arrayOf(classId.toString()))
        db.close()
        return result > 0
    }
}