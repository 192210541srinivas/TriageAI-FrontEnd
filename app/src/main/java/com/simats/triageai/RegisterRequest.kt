package com.simats.triageai

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String,
    val phone: String? = null,
    val license_number: String? = null,
    val department: String? = null
)
