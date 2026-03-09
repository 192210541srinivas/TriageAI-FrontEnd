package com.simats.triageai

import com.google.gson.annotations.SerializedName

data class AdminDashboardStats(
    @SerializedName("total_doctors")
    val totalDoctors: Int,
    @SerializedName("available_doctors")
    val availableDoctors: Int,
    @SerializedName("consulting_doctors")
    val consultingDoctors: Int,
    @SerializedName("total_paramedics")
    val totalParamedics: Int
)
