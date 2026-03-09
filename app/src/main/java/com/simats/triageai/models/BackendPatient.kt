package com.simats.triageai.models

import com.google.gson.annotations.SerializedName

data class BackendPatient(

    @SerializedName("id")
    val id: Int,

    @SerializedName("full_name")
    val fullName: String?,

    @SerializedName("age")
    val age: Int,

    @SerializedName("gender")
    val gender: String?,

    @SerializedName("phone")
    val phone: String?,

    @SerializedName("address")
    val address: String?,

    @SerializedName("systolic")
    val systolic: Int?,

    @SerializedName("diastolic")
    val diastolic: Int?,

    @SerializedName("heart_rate")
    val heartRate: Int?,

    @SerializedName("spo2")
    val spo2: Int?,

    @SerializedName("temperature")
    val temperature: Float?,

    @SerializedName("respiratory_rate")
    val respiratoryRate: Int?,

    @SerializedName("ai_risk_score")
    val aiRiskScore: Int?,

    @SerializedName("case_type")
    val caseType: String?,

    @SerializedName("status")
    val status: String?,

    @SerializedName("chief_complaint")
    val chiefComplaint: String?,

    @SerializedName("medical_conditions")
    val medicalConditions: com.google.gson.JsonElement?,

    @SerializedName("symptoms")
    val symptoms: com.google.gson.JsonElement?,

    @SerializedName("trauma_types")
    val traumaTypes: com.google.gson.JsonElement?,

    @SerializedName("paramedic_id")
    val paramedicId: Int?,

    @SerializedName("paramedic_name")
    val paramedicName: String?,

    @SerializedName("bp")
    val bp: String?,

    @SerializedName("medical_history")
    val medicalHistory: String?,

    @SerializedName("medications")
    val medications: String?,

    @SerializedName("allergies")
    val allergies: String?,

    @SerializedName("doctor_id")
    val doctorId: Int?
)
