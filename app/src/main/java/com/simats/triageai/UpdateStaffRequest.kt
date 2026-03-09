package com.simats.triageai

data class UpdateStaffRequest(
    val name: String,
    val phone: String?,
    val department: String?,
    val address: String? = null
)
