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
    @SerializedName("photo_url")
    val photoUrl: String?,
    @SerializedName("profile_photo_url")
    val profilePhotoUrl: String?,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("profile_image")
    val profileImage: String?,
    @SerializedName("avatar")
    val avatar: String?,
    @SerializedName("picture")
    val picture: String?,
    @SerializedName("profile_picture")
    val profilePicture: String?,
    @SerializedName("profilePhoto")
    val profilePhotoCamel: String?,
    @SerializedName("photoUrl")
    val photoUrlCamel: String?,
    val address: String?
)
