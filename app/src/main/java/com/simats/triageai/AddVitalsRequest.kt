package com.simats.triageai

import com.google.gson.annotations.SerializedName

data class AddVitalsRequest(
    @SerializedName("patient_id") val patient_id: Int,
    @SerializedName("systolic") val systolic: Int,
    @SerializedName("diastolic") val diastolic: Int,
    @SerializedName("heart_rate") val heart_rate: Int,
    @SerializedName("temperature") val temperature: Float,
    @SerializedName("spo2") val spo2: Int,
    @SerializedName("respiratory_rate") val respiratory_rate: Int
)
