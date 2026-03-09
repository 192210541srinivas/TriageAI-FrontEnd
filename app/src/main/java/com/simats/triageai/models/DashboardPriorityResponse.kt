package com.simats.triageai.models

import com.google.gson.annotations.SerializedName

data class DashboardPriorityResponse(

    @SerializedName("critical")
    val critical: List<BackendPatient>,

    @SerializedName("urgent")
    val urgent: List<BackendPatient>,

    @SerializedName("non-urgent")   // 👈 This maps the hyphen key
    val non_urgent: List<BackendPatient>
)