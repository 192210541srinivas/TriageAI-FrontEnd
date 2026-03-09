package com.simats.triageai.models

import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PatientActionLog(
    @SerializedName("action_type") val actionType: String,
    @SerializedName("notes") val notes: String?,
    @SerializedName("doctor_id") val doctorId: Int,
    @SerializedName("timestamp") val timestamp: String
) : Parcelable
