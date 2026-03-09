package com.simats.triageai

import com.simats.triageai.models.PatientRequest
import com.simats.triageai.models.PatientResponse
import retrofit2.Response

class PatientRepository(private val apiService: ApiService) {
    suspend fun addPatient(adminId: Int, request: PatientRequest): Response<PatientResponse> {
        return apiService.addPatient(adminId, request)
    }
}
