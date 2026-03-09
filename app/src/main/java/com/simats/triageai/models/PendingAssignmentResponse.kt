package com.simats.triageai.models

import com.google.gson.annotations.SerializedName

data class PendingAssignmentResponse(
    @SerializedName("assignment") val assignment: String? = null,
    @SerializedName("notification_id") val notificationId: Int? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("patient") val patient: PendingPatientDetails? = null
)

data class PendingPatientDetails(
    @SerializedName("id") val id: Int,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("age") val age: Int,
    @SerializedName("gender") val gender: String,
    @SerializedName("case_type") val caseType: String,
    @SerializedName("bp") val bp: String? = null,
    @SerializedName("heart_rate") val heartRate: Int? = null,
    @SerializedName("spo2") val spo2: Int? = null,
    @SerializedName("temperature") val temperature: Double? = null,
    @SerializedName("respiratory_rate") val respiratoryRate: Int? = null,
    @SerializedName("chief_complaint") val chiefComplaint: String
)
