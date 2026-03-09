package com.simats.triageai.models

import com.google.gson.annotations.SerializedName

data class PatientResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("patient_id")
    val patientId: Int?,
    @SerializedName("case_type")
    val caseType: String?,
    @SerializedName("risk_score")
    val riskScore: Int?,
    @SerializedName("probabilities")
    val probabilities: Probabilities?,
    @SerializedName("explanation")
    val explanation: List<String>?,
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