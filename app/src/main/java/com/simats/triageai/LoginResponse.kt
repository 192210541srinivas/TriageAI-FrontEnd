package com.simats.triageai

data class LoginResponse(
    val status: Boolean,
    val message: String?,
    val user: UserData?
)

data class UserData(
    val id: Int?,
    val name: String?,
    val role: String?
)
