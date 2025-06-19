package com.example.comp1786_su25.MVC

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.comp1786_su25.Models.teacherModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class teacherDatabase(context: Context) : SQLiteOpenHelper(context, "teachers.db", null, 1) {

    companion object {
        // Table name
        private const val TABLE_TEACHERS = "teachers"

        // Column names
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_GENDER = "gender"
        private const val COLUMN_DOB = "date_of_birth"
        private const val COLUMN_CLASS_TYPE = "class_type"
        private const val COLUMN_EXPERIENCE = "experience"

        // Date format for storing dates in the database
        private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create teachers table
        val createTeacherTable = "CREATE TABLE $TABLE_TEACHERS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_NAME TEXT, " +
                "$COLUMN_GENDER TEXT, " +
                "$COLUMN_DOB TEXT, " +
                "$COLUMN_CLASS_TYPE TEXT, " +
                "$COLUMN_EXPERIENCE INTEGER DEFAULT 0" +
                ")"
        db.execSQL(createTeacherTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop table if exists and recreate
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TEACHERS")
        onCreate(db)
    }

    /**
     * Check if the 'experience' column exists in the teachers table, and add it if it doesn't
     */
    private fun checkAndUpdateSchema() {
        val db = this.writableDatabase

        // Check if experience column exists
        val cursor = db.rawQuery("PRAGMA table_info($TABLE_TEACHERS)", null)
        var experienceColumnExists = false

        if (cursor.moveToFirst()) {
            do {
                val columnNameIndex = cursor.getColumnIndex("name")
                if (columnNameIndex != -1) {
                    val columnName = cursor.getString(columnNameIndex)
                    if (columnName == COLUMN_EXPERIENCE) {
                        experienceColumnExists = true
                        break
                    }
                }
            } while (cursor.moveToNext())
        }
        cursor.close()

        // Add experience column if it doesn't exist
        if (!experienceColumnExists) {
            try {
                db.execSQL("ALTER TABLE $TABLE_TEACHERS ADD COLUMN $COLUMN_EXPERIENCE INTEGER DEFAULT 0")
            } catch (e: Exception) {
                android.util.Log.e("teacherDatabase", "Error adding experience column", e)
            }
        }
        db.close()
    }

    /**
     * Insert a new teacher into the database
     */
    fun insertTeacher(teacher: teacherModel): Long {
        // Check and update the database schema to ensure experience column exists
        checkAndUpdateSchema()

        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COLUMN_NAME, teacher.name)
        values.put(COLUMN_GENDER, teacher.gender)
        values.put(COLUMN_DOB, DATE_FORMAT.format(teacher.dateOfBirth))
        values.put(COLUMN_CLASS_TYPE, teacher.classType)
        values.put(COLUMN_EXPERIENCE, teacher.experience)

        // Insert row
        val id = db.insert(TABLE_TEACHERS, null, values)
        db.close()

        return id
    }

    /**
     * Get all teachers from the database
     */
    fun getAllTeachers(): List<teacherModel> {
        val teacherList = ArrayList<teacherModel>()

        // Select all query
        val selectQuery = "SELECT * FROM $TABLE_TEACHERS"
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery, null)

        // Loop through all rows and add to list
        if (cursor.moveToFirst()) {
            do {
                val nameIndex = cursor.getColumnIndex(COLUMN_NAME)
                val genderIndex = cursor.getColumnIndex(COLUMN_GENDER)
                val dobIndex = cursor.getColumnIndex(COLUMN_DOB)
                val classTypeIndex = cursor.getColumnIndex(COLUMN_CLASS_TYPE)
                val experienceIndex = cursor.getColumnIndex(COLUMN_EXPERIENCE) // Assuming experience is added later

                // Check if columns exist
                if (nameIndex != -1 && genderIndex != -1 && dobIndex != -1 && classTypeIndex != -1) {
                    val name = cursor.getString(nameIndex)
                    val gender = cursor.getString(genderIndex)
                    val dobString = cursor.getString(dobIndex)
                    val classType = cursor.getString(classTypeIndex)
                    val experience = if (experienceIndex != -1) cursor.getInt(experienceIndex) else 0

                    // Parse date string to Date object
                    val dob = DATE_FORMAT.parse(dobString) ?: Date()

                    // Create teacher model and add to list
                    val teacher = teacherModel(name, gender, dob, classType, emptyList(), experience)
                    teacherList.add(teacher)
                }
            } while (cursor.moveToNext())
        }

        // Close cursor and database
        cursor.close()
        db.close()

        return teacherList
    }

    /**
     * Get a teacher by their ID
     */
    fun getTeacherById(id: Int): teacherModel? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_TEACHERS,
            arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_GENDER, COLUMN_DOB, COLUMN_CLASS_TYPE, COLUMN_EXPERIENCE),
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null, null, null, null
        )

        var teacher: teacherModel? = null

        if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(COLUMN_NAME)
            val genderIndex = cursor.getColumnIndex(COLUMN_GENDER)
            val dobIndex = cursor.getColumnIndex(COLUMN_DOB)
            val classTypeIndex = cursor.getColumnIndex(COLUMN_CLASS_TYPE)
            val experienceIndex = cursor.getColumnIndex(COLUMN_EXPERIENCE)

            // Check if columns exist
            if (nameIndex != -1 && genderIndex != -1 && dobIndex != -1 && classTypeIndex != -1 && experienceIndex != -1) {
                val name = cursor.getString(nameIndex)
                val gender = cursor.getString(genderIndex)
                val dobString = cursor.getString(dobIndex)
                val classType = cursor.getString(classTypeIndex)
                val experience = cursor.getInt(experienceIndex)

                // Parse date string to Date object
                val dob = DATE_FORMAT.parse(dobString) ?: Date()

                // Create teacher model
                teacher = teacherModel(name, gender, dob, classType, emptyList(), experience)
            }
        }

        // Close cursor and database
        cursor.close()
        db.close()

        return teacher
    }

    /**
     * Get a teacher by their name
     */
    fun getTeacherByName(name: String): teacherModel? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_TEACHERS,
            arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_GENDER, COLUMN_DOB, COLUMN_CLASS_TYPE, COLUMN_EXPERIENCE),
            "$COLUMN_NAME = ?",
            arrayOf(name),
            null, null, null, null
        )

        var teacher: teacherModel? = null

        if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(COLUMN_NAME)
            val genderIndex = cursor.getColumnIndex(COLUMN_GENDER)
            val dobIndex = cursor.getColumnIndex(COLUMN_DOB)
            val classTypeIndex = cursor.getColumnIndex(COLUMN_CLASS_TYPE)
            val experienceIndex = cursor.getColumnIndex(COLUMN_EXPERIENCE)

            // Check if columns exist
            if (nameIndex != -1 && genderIndex != -1 && dobIndex != -1 && classTypeIndex != -1 && experienceIndex != -1) {
                val teacherName = cursor.getString(nameIndex)
                val gender = cursor.getString(genderIndex)
                val dobString = cursor.getString(dobIndex)
                val classType = cursor.getString(classTypeIndex)
                val experience = cursor.getInt(experienceIndex)

                // Parse date string to Date object
                val dob = DATE_FORMAT.parse(dobString) ?: Date()

                // Create teacher model
                teacher = teacherModel(teacherName, gender, dob, classType, emptyList(), experience)
            }
        }

        // Close cursor and database
        cursor.close()
        db.close()

        return teacher
    }

    fun getAllTeacherNames(): List<String> {
        val names = mutableListOf<String>()
        val selectQuery = "SELECT $COLUMN_NAME FROM $TABLE_TEACHERS"
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val nameIndex = cursor.getColumnIndex(COLUMN_NAME)
                if (nameIndex != -1) {
                    names.add(cursor.getString(nameIndex))
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return names
    }
}
