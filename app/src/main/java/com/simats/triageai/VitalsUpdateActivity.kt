package com.simats.triageai

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.simats.triageai.databinding.ActivityVitalsUpdateBinding
import com.simats.triageai.models.Patient
import kotlinx.coroutines.launch

class VitalsUpdateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVitalsUpdateBinding
    private var patient: Patient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVitalsUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        patient = intent.getParcelableExtra("patient")
        if (patient == null) {
            Toast.makeText(this, "Patient data missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.btnBack.setOnClickListener { finish() }

        binding.btnSubmit.setOnClickListener {
            validateAndSubmit()
        }
    }

    private fun validateAndSubmit() {
        val systolic = binding.etSystolic.text.toString()
        val diastolic = binding.etDiastolic.text.toString()
        val hr = binding.etHeartRate.text.toString()
        val spo2 = binding.etSpo2.text.toString()
        val temp = binding.etTemperature.text.toString()
        val respRate = binding.etRespRate.text.toString()

        if (systolic.isEmpty() || diastolic.isEmpty() || hr.isEmpty() || 
            spo2.isEmpty() || temp.isEmpty() || respRate.isEmpty()) {
            Toast.makeText(this, "Please fill all vitals", Toast.LENGTH_SHORT).show()
            return
        }

        val patientId = patient?.id?.toIntOrNull() ?: -1
        val paramedicId = getSharedPreferences("TriageAI", MODE_PRIVATE).getInt("user_id", -1)

        if (patientId == -1 || paramedicId == -1) {
            Toast.makeText(this, "Session error", Toast.LENGTH_SHORT).show()
            return
        }

        // Apply a small scale animation on click
        binding.btnSubmit.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction {
            binding.btnSubmit.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
            
            submitVitals(patientId, paramedicId, systolic, diastolic, hr, spo2, temp, respRate)
        }.start()
    }

    private fun submitVitals(
        patientId: Int,
        paramedicId: Int,
        sys: String,
        dia: String,
        hr: String,
        spo2: String,
        temp: String,
        rr: String
    ) {
        val request = AddVitalsRequest(
            patient_id = patientId,
            systolic = sys.toInt(),
            diastolic = dia.toInt(),
            heart_rate = hr.toInt(),
            temperature = temp.toFloat(),
            spo2 = spo2.toInt(),
            respiratory_rate = rr.toInt()
        )

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.addVitals(patientId, paramedicId, request)
                if (response.isSuccessful) {
                    // Send notification to assigned doctor
                    val docId = patient?.doctorId?.toIntOrNull() ?: -1
                    if (docId != -1) {
                        val msg = "${patient?.paramedicName}(ID:${paramedicId}) updated vitals of ${patient?.name}(ID:${patient?.id})\n" +
                                "HR: $hr, BP: $sys/$dia, SpO2: $spo2%, Temp: $temp°C, RR: $rr"
                        
                        ApiClient.apiService.sendNotification(
                            sender_id = paramedicId,
                            receiver_id = docId,
                            message = msg,
                            type = "VITALS_UPDATE",
                            patient_id = patientId
                        )
                    }

                    Toast.makeText(this@VitalsUpdateActivity, "Vitals updated successfully", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this@VitalsUpdateActivity, "Failed to update vitals", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@VitalsUpdateActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}