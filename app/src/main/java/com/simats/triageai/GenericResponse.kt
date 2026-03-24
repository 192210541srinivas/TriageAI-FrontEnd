package com.simats.triageai

data class GenericResponse(
    val status: Boolean,
    val message: String?,
    val user_id: Int? = null
)
