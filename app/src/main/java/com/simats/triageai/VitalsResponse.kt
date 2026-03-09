package com.simats.triageai

import java.util.Date

data class VitalsResponse(
    val recorded_at: Date,
    val bp_systolic: Int,
    val bp_diastolic: Int,
    val heart_rate: Int,
    val temperature: Float,
    val spo2: Int
)
