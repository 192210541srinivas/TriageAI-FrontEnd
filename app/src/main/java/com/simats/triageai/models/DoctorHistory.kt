package com.simats.triageai.models

data class DoctorHistory(
    val id: Int,
    val doctor_id: Int,
    val patient_id: Int,
    val patient_name: String,
    val case_code: String,
    val case_type: String,
    val ai_risk_score: Int,
    val outcome: String,
    val created_at: String
)