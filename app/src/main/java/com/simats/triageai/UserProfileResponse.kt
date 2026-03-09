package com.simats.triageai

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val phone: String?,
    @SerializedName("license_number")
    val licenseNumber: String?,
    val department: String?,
    @SerializedName("joined_date")
    val joinedDate: String?,
    @SerializedName("profile_photo")
    val profilePhoto: String?,
    val address: String?
)
