package com.simats.triageai

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.simats.triageai.databinding.ActivityAssessmentNotesBinding
import com.simats.triageai.models.Patient
import kotlinx.coroutines.launch

class AssessmentNotesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAssessmentNotesBinding
    private var patient: Patient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAssessmentNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        patient = intent.getParcelableExtra("patient")
        
        binding.btnBack.setOnClickListener { finish() }

        binding.btnSend.setOnClickListener {
            val notes = binding.etAssessmentNotes.text.toString()
            if (notes.isBlank()) {
                Toast.makeText(this, "Please enter assessment notes", Toast.LENGTH_SHORT).show()
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
                // Using begin-assessment endpoint logic
                val response = ApiClient.apiService.beginAssessment(
                    doctorId = doctorId,
                    paramedicId = paramedicId,
                    patientId = patientId
                )
                
                if (response.isSuccessful) {
                    Toast.makeText(this@AssessmentNotesActivity, "Assessment request sent to paramedic", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this@AssessmentNotesActivity, "Failed to send request", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AssessmentNotesActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}