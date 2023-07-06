package com.example.loginandsignupfirebase.model

data class UserModel(
    val uid: String? = null,
    val email: String? = null,
    val fullName: String? = "",
    val levelUser: String? = null,
    val gender: String? = "",
    val phone: String? = "",
    val address: String? = "",
    val imageUrl: String? = ""
)
