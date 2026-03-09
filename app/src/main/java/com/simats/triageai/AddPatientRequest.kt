package com.simats.triageai

data class PatientRequest(
    val full_name: String,
    val age: Int,
    val gender: String,
    val phone: String,
    val address: String,
    val email: String,          // 🔥 ADD THIS
    val systolic: Int,
    val diastolic: Int,
    val heart_rate: Int,
    val temperature: Float,
    val spo2: Int,
    val respiratory_rate: Int,
    val avpu: String,
    val symptoms: List<String>?,
    val trauma_types: List<String>?,
    val medical_conditions: List<String>?
)