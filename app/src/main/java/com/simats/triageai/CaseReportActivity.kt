package com.simats.triageai

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.simats.triageai.databinding.ActivityCaseReportBinding
import com.simats.triageai.models.Patient
import com.simats.triageai.models.Priority
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.google.gson.JsonArray

class CaseReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCaseReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaseReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val patient = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("patient", Patient::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Patient>("patient")
        }
        if (patient == null) {
            Toast.makeText(this, "Case data not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        populateData(patient)
        fetchFullDetails(patient.numericId)

        binding.btnBack.setOnClickListener { finish() }

    }

    private fun populateData(p: Patient) {
        binding.tvPatientNameHeader.text = "${p.id} • ${p.name}"
        binding.tvPatientNameSummary.text = p.name
        
        // Case Summary
        // Splitting date and time for multi-line display if it follows the mock format
        val dateTimeValue = p.waitTime.replace(" ", "\n")
        binding.tvDateTime.text = dateTimeValue
        
        binding.tvPriority.text = p.priority.name
        binding.tvPriority.setTextColor(when(p.priority) {
            Priority.CRITICAL -> ContextCompat.getColor(this, R.color.critical_main)
            Priority.URGENT -> ContextCompat.getColor(this, R.color.urgent_main)
            Priority.NON_URGENT -> ContextCompat.getColor(this, R.color.stable_main)
        })
        binding.tvRiskScore.text = "${p.riskScore}%"
        binding.tvOutcome.text = p.outcome ?: "Admitted"

        // AI Analysis
        binding.tvChiefComplaint.text = p.condition 
        binding.tvKeySymptoms.text = p.keySymptoms ?: "Severe chest pain, difficulty breathing"
        binding.tvRiskFactors.text = p.riskFactors ?: "Hypertension, diabetes, elevated vitals"

        // Vital Signs
        binding.tvBp.text = "${p.bp} mmHg"
        binding.tvHr.text = "${p.hr} bpm"
        binding.tvSpo2.text = "${p.spo2}%"
        binding.tvTemp.text = p.temp ?: "37.2°C"

        // Treatment summary
        binding.tvTreatmentSummary.text = p.treatmentSummary.ifBlank { "Patient was immediately assessed and stabilized. ECG performed. Responded well to initial interventions." }
    }

    private fun fetchFullDetails(patientId: Int) {
        if (patientId == 0) return

        lifecycleScope.launch {
            try {
                // 1. Fetch Profile for vitals and complaint
                val response = ApiClient.apiService.getPatientProfile(patientId)
                if (response.isSuccessful && response.body() != null) {
                    val bp = response.body()!!
                    Log.d("CaseReport", "Profile fetch successful: $bp")
                    updateUIWithBackendData(bp)
                    
                    // If vitals are still essentially missing (0/0), try the latest vitals endpoint
                    if (bp.systolic == 0 || bp.systolic == null) {
                        fetchLatestVitals(patientId)
                    }
                } else {
                    Log.e("CaseReport", "Profile fetch failed: ${response.code()}")
                }

                // Treatment logs
                val timelineResponse = ApiClient.apiService.getPatientTimeline(patientId)
                if (timelineResponse.isSuccessful && timelineResponse.body() != null) {
                    val logs = timelineResponse.body()!!
                    if (logs.isNotEmpty()) {
                        val summary = logs.joinToString("\n\n") { log ->
                            "[${log.actionType.replace("_", " ")}]\n${log.notes ?: "No notes"}"
                        }
                        binding.tvTreatmentSummary.text = summary
                    }
                }
            } catch (e: Exception) {
                Log.e("CaseReport", "Error fetching extra details", e)
                e.printStackTrace()
            }
        }
    }

    private fun fetchLatestVitals(patientId: Int) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getLatestVitals(patientId)
                if (response.isSuccessful && response.body() != null) {
                    val lv = response.body()!!
                    Log.d("CaseReport", "Latest vitals fetch successful: $lv")
                    binding.tvBp.text = "${lv.bp} mmHg"
                    binding.tvHr.text = "${lv.heartRate} bpm"
                    binding.tvSpo2.text = "${lv.spo2}%"
                    binding.tvTemp.text = "${lv.temperature}°C"
                }
            } catch (e: Exception) {
                Log.e("CaseReport", "Error fetching latest vitals", e)
            }
        }
    }

    private fun updateUIWithBackendData(bp: com.simats.triageai.models.BackendPatient) {
        binding.tvChiefComplaint.text = bp.chiefComplaint ?: "Not specified"
        
        // Vitals
        binding.tvBp.text = if (!bp.bp.isNullOrBlank()) "${bp.bp} mmHg" else "${bp.systolic ?: 0}/${bp.diastolic ?: 0} mmHg"
        binding.tvHr.text = "${bp.heartRate ?: 0} bpm"
        binding.tvSpo2.text = "${bp.spo2 ?: 0}%"
        binding.tvTemp.text = if (bp.temperature != null && bp.temperature != 0f) "${bp.temperature}°C" else "37.0°C"

        // Symptoms & trauma merged for Key Symptoms
        val symptomsList = parseJsonArray(bp.symptoms).toMutableList()
        val traumaList = parseJsonArray(bp.traumaTypes)
        symptomsList.addAll(traumaList)
        
        binding.tvKeySymptoms.text = if (symptomsList.isNotEmpty()) symptomsList.joinToString(", ") else "None recorded"

        // Medical conditions for Risk Factors
        val conditionsList = parseJsonArray(bp.medicalConditions)
        binding.tvRiskFactors.text = if (conditionsList.isNotEmpty()) conditionsList.joinToString(", ") else "None recorded"
        
        // Final fallback: if vitals display shows placeholder or 0, it means data is definitely missing
        if (binding.tvHr.text.toString().startsWith("0")) {
            // Log it
            Log.w("CaseReport", "Vitals still showing 0 after update")
        }
        
        // AI Recommendation removed from layout
    }

    private fun parseJsonArray(element: com.google.gson.JsonElement?): List<String> {
        val list = mutableListOf<String>()
        try {
            if (element != null && element.isJsonArray) {
                val array = element.asJsonArray
                for (i in 0 until array.size()) {
                    list.add(array.get(i).asString)
                }
            } else if (element != null && element.isJsonPrimitive) {
                // Sometimes it's a string containing a JSON array
                val str = element.asString
                if (str.startsWith("[")) {
                    @Suppress("DEPRECATION")
                    val array = com.google.gson.JsonParser().parse(str).asJsonArray
                    for (i in 0 until array.size()) {
                        list.add(array.get(i).asString)
                    }
                } else if (str.isNotBlank()) {
                    list.add(str)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }
}
