package com.simats.triageai

import com.google.gson.annotations.SerializedName

data class LatestVitalsResponse(
    @SerializedName("patient_id") val patientId: Int,
    @SerializedName("bp") val bp: String,
    @SerializedName("heart_rate") val heartRate: Int,
    @SerializedName("temperature") val temperature: Double,
    @SerializedName("spo2") val spo2: Int,
    @SerializedName("respiratory_rate") val respiratoryRate: Int,
    @SerializedName("recorded_at") val recordedAt: String
)
