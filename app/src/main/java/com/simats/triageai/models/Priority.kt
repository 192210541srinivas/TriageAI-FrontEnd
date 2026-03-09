package com.simats.triageai.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Priority : Parcelable {
    CRITICAL,
    URGENT,
    NON_URGENT
}
