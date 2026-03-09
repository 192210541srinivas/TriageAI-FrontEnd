package com.simats.triageai.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Patient(
    var id: String = "",
    var name: String = "",
    var age: Int = 0,
    var gender: String? = "",
    var condition: String = "",
    var bp: String = "",
    var hr: String = "",
    var spo2: String = "",
    var temp: String? = null,
    var respRate: String = "",
    var riskScore: Int = 0,
    var waitTime: String = "",
    var priority: Priority = Priority.NON_URGENT,
    var phone: String = "",
    var address: String = "",
    var systolic: Int = 0,
    var diastolic: Int = 0,
    var heartRate: Int = 0,
    var temperature: Float = 0f,
    var spo2Int: Int = 0,
    var respiratoryRate: Int = 0,
    var chiefComplaint: String = "",
    var chiefComplaintDescription: String = "",
    var symptoms: List<String> = emptyList(),
    var keySymptoms: String = "",
    var riskFactors: String = "",
    var ctScanUri: Uri? = null,
    var mriScanUri: Uri? = null,
    var xRayUri: Uri? = null,
    var medicalConditions: List<String> = emptyList(),
    var medicalHistory: List<String> = emptyList(),
    var medications: List<String> = emptyList(),
    var allergies: List<String> = emptyList(),
    var paramedicName: String = "",
    var paramedicId: String = "",
    var assessment: String = "",
    var isDischarged: Boolean = false,
    var outcome: String? = null,
    var aiRecommendation: String = "",
    var treatmentSummary: String = "",
    var doctorId: String = ""
) : Parcelable
