package com.simats.triageai.utils

import com.simats.triageai.models.BackendPatient
import com.simats.triageai.models.Patient
import com.simats.triageai.models.Priority

object PatientMapper {
    fun mapToUiPatient(bp: BackendPatient): Patient {
        val medicalHistoryList = if (!bp.medicalHistory.isNullOrBlank()) {
            bp.medicalHistory!!.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        } else if (bp.medicalConditions != null) {
            when {
                bp.medicalConditions.isJsonArray -> {
                    val array = bp.medicalConditions.asJsonArray
                    List(array.size()) { array.get(it).asString }
                }
                bp.medicalConditions.isJsonPrimitive -> {
                    listOf(bp.medicalConditions.asString)
                }
                else -> emptyList()
            }
        } else {
            emptyList()
        }

        val symptomsList = when {
            bp.symptoms?.isJsonArray == true -> {
                val array = bp.symptoms.asJsonArray
                List(array.size()) { array.get(it).asString }
            }
            bp.symptoms?.isJsonPrimitive == true -> {
                listOf(bp.symptoms.asString)
            }
            else -> emptyList()
        }

        val traumaList = when {
            bp.traumaTypes?.isJsonArray == true -> {
                val array = bp.traumaTypes.asJsonArray
                List(array.size()) { array.get(it).asString }
            }
            bp.traumaTypes?.isJsonPrimitive == true -> {
                listOf(bp.traumaTypes.asString)
            }
            else -> emptyList()
        }

        val medicationsList = if (!bp.medications.isNullOrBlank()) {
            try {
                val array = org.json.JSONArray(bp.medications)
                List(array.length()) { array.getString(it) }
            } catch (e: Exception) {
                listOf(bp.medications!!)
            }
        } else {
            emptyList()
        }

        val allergiesList = if (!bp.allergies.isNullOrBlank()) {
            try {
                val array = org.json.JSONArray(bp.allergies)
                List(array.length()) { array.getString(it) }
            } catch (e: Exception) {
                listOf(bp.allergies!!)
            }
        } else {
            emptyList()
        }

        // Logic to parse symptoms and chief complaint should ideally be here too
        // but for now we'll focus on the vitals and history fixes

        val bpDisplay = when {
            !bp.bp.isNullOrBlank() -> bp.bp!!
            bp.systolic != null && bp.diastolic != null && bp.systolic != 0 && bp.diastolic != 0 -> "${bp.systolic}/${bp.diastolic}"
            else -> "--/--"
        }

        val hrDisplay = if (bp.heartRate != null && bp.heartRate != 0) "${bp.heartRate} bpm" else "-- bpm"
        val spo2Display = if (bp.spo2 != null && bp.spo2 != 0) "${bp.spo2}%" else "--%"
        val tempDisplay = if (bp.temperature != null && bp.temperature != 0f) "${bp.temperature}°C" else "--°C"
        val respRateDisplay = if (bp.respiratoryRate != null && bp.respiratoryRate != 0) "${bp.respiratoryRate} /min" else "--"

        return Patient(
            id = bp.id.toString(),
            name = bp.fullName ?: "Unknown Patient",
            age = bp.age,
            gender = bp.gender ?: "",
            condition = bp.chiefComplaint ?: "",
            bp = bpDisplay,
            hr = hrDisplay,
            spo2 = spo2Display,
            temp = tempDisplay,
            respRate = respRateDisplay,
            riskScore = bp.aiRiskScore ?: 0,
            priority = when (bp.caseType?.uppercase()) {
                "CRITICAL" -> Priority.CRITICAL
                "URGENT" -> Priority.URGENT
                else -> Priority.NON_URGENT
            },
            waitTime = bp.status ?: "Waiting",
            medicalHistory = medicalHistoryList,
            medicalConditions = medicalHistoryList,
            symptoms = symptomsList,
            medications = medicationsList,
            allergies = allergiesList,
            paramedicId = bp.paramedicId?.toString() ?: "",
            paramedicName = bp.paramedicName ?: "",
            doctorId = bp.doctorId?.toString() ?: ""
        )
    }
}
