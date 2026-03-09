package com.simats.triageai

import com.google.gson.annotations.SerializedName

data class PhotoUploadResponse(
    val status: Boolean,
    @SerializedName("photo_url")
    val photoUrl: String
)
