package com.simats.triageai

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.simats.triageai.databinding.ActivityMedicationNotesBinding
import com.simats.triageai.models.Patient
import kotlinx.coroutines.launch

class MedicationNotesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMedicationNotesBinding
    private var patient: Patient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMedicationNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        patient = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("patient", Patient::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Patient>("patient")
        }
        
        binding.btnBack.setOnClickListener { finish() }

        binding.btnSend.setOnClickListener {
            val notes = binding.etMedicationNotes.text.toString()
            if (notes.isBlank()) {
                Toast.makeText(this, "Please enter medication notes", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            sendNotes(notes)
        }
    }

    private fun sendNotes(notes: String) {
        val prefs = getSharedPreferences("TriageAI", MODE_PRIVATE)
        val doctorId = prefs.getInt("user_id", -1)
        val patientId = patient?.id?.toIntOrNull() ?: -1
        val paramedicId = patient?.paramedicId?.toIntOrNull() ?: -1

        if (doctorId == -1 || patientId == -1 || paramedicId == -1) {
            Toast.makeText(this, "Session, Patient or Paramedic error", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                // Using the administer-medication endpoint from backend
                val response = ApiClient.apiService.administerMedication(
                    doctorId = doctorId,
                    paramedicId = paramedicId,
                    patientId = patientId,
                    notes = notes
                )

                if (response.isSuccessful) {
                    Toast.makeText(this@MedicationNotesActivity, "Notes sent", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    val error = response.errorBody()?.string()
                    Toast.makeText(
                        this@MedicationNotesActivity,
                        "Failed: ${response.code()} - $error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MedicationNotesActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}