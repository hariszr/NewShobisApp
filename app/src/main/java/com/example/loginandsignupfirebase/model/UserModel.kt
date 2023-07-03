package com.example.loginandsignupfirebase.model

data class UserModel(
    val uid: String? = null,
    val email: String? = null,
    val fullName: String? = "",
    val phone: String? = "",
    val address: String? = "",
    val imageUrl: String? = ""
)
