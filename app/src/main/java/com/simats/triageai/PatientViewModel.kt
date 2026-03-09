package com.simats.triageai

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.triageai.models.PatientRequest
import com.simats.triageai.models.PatientResponse
import kotlinx.coroutines.launch

class PatientViewModel(private val repository: PatientRepository) : ViewModel() {

    private val _patientResponse = MutableLiveData<Resource<PatientResponse>>()
    val patientResponse: LiveData<Resource<PatientResponse>> get() = _patientResponse

    fun addPatient(adminId: Int, request: PatientRequest) {
        _patientResponse.postValue(Resource.Loading())
        viewModelScope.launch {
            try {
                val response = repository.addPatient(adminId, request)
                if (response.isSuccessful) {
                    _patientResponse.postValue(Resource.Success(response.body()!!))
                } else {
                    _patientResponse.postValue(Resource.Error(response.message()))
                }
            } catch (e: Exception) {
                _patientResponse.postValue(Resource.Error(e.message ?: "An error occurred"))
            }
        }
    }
}

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}
