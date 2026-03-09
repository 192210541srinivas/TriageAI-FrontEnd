package com.simats.triageai

data class AssignPatientRequest(
    val doctor_id: Int,
    val patient_id: Int
)
