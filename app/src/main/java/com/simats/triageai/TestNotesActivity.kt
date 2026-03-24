package com.simats.triageai

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.simats.triageai.databinding.ActivityTestNotesBinding
import com.simats.triageai.models.Patient
import kotlinx.coroutines.launch

class TestNotesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestNotesBinding
    private var patient: Patient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestNotesBinding.inflate(layoutInflater)
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
            val notes = binding.etTestNotes.text.toString()
            if (notes.isBlank()) {
                Toast.makeText(this, "Please enter test instructions", Toast.LENGTH_SHORT).show()
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
                // Use take-action for TEST (which backend handles by creating notification)
                val response = ApiClient.apiService.takeAction(
                    doctorId = doctorId,
                    patientId = patientId,
                    actionType = "TEST",
                    notes = notes
                )
                
                if (response.isSuccessful) {
                    Toast.makeText(this@TestNotesActivity, "Test orders sent to paramedic", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this@TestNotesActivity, "Failed to send orders", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@TestNotesActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}