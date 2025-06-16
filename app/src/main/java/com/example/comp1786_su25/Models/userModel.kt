package com.example.comp1786_su25.Models

class userModel(
    var username: String,
    var email: String,
    var password: String,
    var isLoggedIn: Boolean
) {
    override fun toString(): String {
        return "userModel{" +
                "username='$username'" +
                ", email='$email'" +
                ", password='$password'" +
                ", isLoggedIn=$isLoggedIn" +
                "}"
    }
}