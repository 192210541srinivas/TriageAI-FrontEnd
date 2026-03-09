package com.simats.triageai.models

import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BackendVitals(
    @SerializedName("id") val id: Int,
    @SerializedName("bp") val bp: String,
    @SerializedName("heart_rate") val heartRate: Int,
    @SerializedName("temperature") val temperature: Double,
    @SerializedName("spo2") val spo2: Int,
    @SerializedName("respiratory_rate") val respiratoryRate: Int,
    @SerializedName("recorded_at") val recordedAt: String
) : Parcelable
