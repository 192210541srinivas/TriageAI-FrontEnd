package com.simats.triageai.models

import com.google.gson.annotations.SerializedName

data class PatientResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("patient_id")
    val patientId: com.google.gson.JsonElement?,
    @SerializedName("case_type")
    val caseType: String?,
    @SerializedName("risk_score")
    val riskScore: com.google.gson.JsonElement?,
    @SerializedName("probabilities")
    val probabilities: com.google.gson.JsonElement?,
    @SerializedName("explanation")
    val explanation: com.google.gson.JsonElement?,
    @SerializedName("detail")
    val detail: String?
)

data class Probabilities(

    @SerializedName("CRITICAL")
    val critical: Double?,

    @SerializedName("URGENT")
    val urgent: Double?,

    @SerializedName("NON-URGENT")
    val nonUrgent: Double?
)