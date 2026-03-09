package com.simats.triageai.models

import com.google.gson.annotations.SerializedName

data class NearbyDoctorResponse(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("count")
    val count: Int,
    @SerializedName("doctors")
    val doctors: List<NearbyDoctor>
)

data class NearbyDoctor(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("distance_km")
    val distanceKm: Double,
    @SerializedName("status")
    val status: String
)
