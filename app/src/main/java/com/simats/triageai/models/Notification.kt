package com.simats.triageai.models

import com.google.gson.annotations.SerializedName

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Notification(
    @SerializedName("id") val id: Int,
    @SerializedName("sender_id") val senderId: Int? = null,
    @SerializedName("receiver_id") val receiverId: Int? = null,
    @SerializedName("patient_id") val patientId: Int? = null,
    @SerializedName("message") val message: String,
    @SerializedName("type") val type: String,
    @SerializedName("status") var status: String,
    @SerializedName("action") var action: String? = null,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("patient_details") val patientDetails: CaseAssignmentDetails? = null
) : Parcelable

@Parcelize
data class CaseAssignmentDetails(
    @SerializedName("full_name") val fullName: String,
    @SerializedName("age") val age: Int,
    @SerializedName("gender") val gender: String,
    @SerializedName("bp") val bp: String,
    @SerializedName("spo2") val spo2: Int,
    @SerializedName("temperature") val temperature: Double,
    @SerializedName("respiratory_rate") val respiratoryRate: Int,
    @SerializedName("case_type") val caseType: String,
    @SerializedName("chief_complaint") val chiefComplaint: String
) : Parcelable
