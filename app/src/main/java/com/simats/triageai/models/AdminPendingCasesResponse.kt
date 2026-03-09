package com.simats.triageai.models

import com.google.gson.annotations.SerializedName

data class AdminPendingCasesResponse(
    @SerializedName("critical")
    val critical: List<BackendPatient>
)
