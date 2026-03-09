package com.simats.triageai.models

import com.google.gson.annotations.SerializedName

data class ParamedicWaitingCountResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("waiting_counts") val waitingCounts: WaitingCounts,
    @SerializedName("total_waiting") val totalWaiting: Int
)

data class WaitingCounts(
    @SerializedName("critical") val critical: Int,
    @SerializedName("urgent") val urgent: Int,
    @SerializedName("non_urgent") val nonUrgent: Int
)
