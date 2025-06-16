package com.example.comp1786_su25.MVC

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.comp1786_su25.Models.userModel

class Identifier(context: Context) : SQLiteOpenHelper(context, "user.db", null, 1) {

    companion object {
        const val USERS = "users"
        const val USERNAME = "username"
        const val EMAIL = "email"
        const val PASSWORD = "password"
        const val ID = "id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE IF NOT EXISTS $USERS (" +
                "$ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$USERNAME TEXT NOT NULL, " +
                "$EMAIL TEXT NOT NULL, " +
                "$PASSWORD TEXT NOT NULL" +
                ");"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades if needed
    }

    fun addOne(userModel: userModel): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(USERNAME, userModel.username)
        cv.put(EMAIL, userModel.email)
        cv.put(PASSWORD, userModel.password)

        return db.insert(USERS, null, cv) != -1L
    }

    /**
     * Searches for a user by username or email and verifies the password
     * @param identifier The username or email to search for
     * @param password The password to verify
     * @return userModel if found, null otherwise
     */
    fun getUserByUsernameOrEmail(identifier: String, password: String): userModel? {
        val db = this.readableDatabase
        var user: userModel? = null

        // Query that checks if either username or email matches and password is correct
        val query = "SELECT * FROM $USERS " +
                "WHERE ($USERNAME = ? OR $EMAIL = ?) AND " +
                "$PASSWORD = ?"

        val cursor = db.rawQuery(query, arrayOf(identifier, identifier, password))

        if (cursor.moveToFirst()) {
            // User found, create a userModel object
            val username = cursor.getString(cursor.getColumnIndexOrThrow(USERNAME))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(EMAIL))
            val userPassword = cursor.getString(cursor.getColumnIndexOrThrow(PASSWORD))

            // Create user model with logged in status true
            user = userModel(username, email, userPassword, true)
        }

        cursor.close()
        return user
    }
}