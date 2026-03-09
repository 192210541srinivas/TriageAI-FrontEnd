package com.simats.triageai

import com.google.gson.annotations.SerializedName

data class ToggleStatusResponse(
    val status: Boolean,
    @SerializedName("new_status")
    val newStatus: String
)
