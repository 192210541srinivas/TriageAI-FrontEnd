package com.simats.triageai

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.simats.triageai.databinding.ActivityTakeActionBinding
import com.simats.triageai.models.Patient
import kotlinx.coroutines.launch

class TakeActionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTakeActionBinding
    private var patient: Patient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTakeActionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        patient = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("patient", Patient::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Patient>("patient")
        }
        if (patient != null) {
            binding.tvPatientName.text = patient?.name
        }

        binding.btnBack.setOnClickListener { finish() }

        binding.cardBeginAssessment.setOnClickListener {
            val doctorId = getSharedPreferences("TriageAI", MODE_PRIVATE).getInt("user_id", -1)
            val patientIdInt = patient?.id?.toIntOrNull() ?: -1
            val paramedicIdInt = patient?.paramedicId?.toIntOrNull() ?: -1

            if (doctorId == -1) {
                Toast.makeText(this, "Session error: Please login again", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (patientIdInt == -1) {
                Toast.makeText(this, "Patient error: Invalid patient ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (paramedicIdInt == -1) {
                Toast.makeText(this, "Paramedic error: Missing paramedic association for this patient", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val response = ApiClient.apiService.sendNotification(
                        sender_id = doctorId,
                        receiver_id = paramedicIdInt,
                        message = "update vitals asking for ${patient?.name}(pt Id:${patient?.id})",
                        type = "ASSESSMENT",
                        patient_id = patientIdInt
                    )

                    if (response.isSuccessful) {
                        Toast.makeText(this@TakeActionActivity, "Notification sent to paramedic", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@TakeActionActivity, "Failed to send notification", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@TakeActionActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.cardAdministerMeds.setOnClickListener {
            val intent = Intent(this, MedicationNotesActivity::class.java)
            intent.putExtra("patient", patient)
            startActivity(intent)
        }

        binding.cardOrderTests.setOnClickListener {
            val intent = Intent(this, TestNotesActivity::class.java)
            intent.putExtra("patient", patient)
            startActivity(intent)
        }

        // Consult specialist button removed as requested
        binding.cardConsultSpecialist.visibility = View.GONE

        binding.cardDischarge.setOnClickListener {
            val doctorId = getSharedPreferences("TriageAI", MODE_PRIVATE).getInt("user_id", -1)
            val patientIdInt = patient?.id?.toIntOrNull() ?: -1

            if (doctorId == -1 || patientIdInt == -1) {
                Toast.makeText(this, "Session or Patient error", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val response = ApiClient.apiService.takeAction(
                        doctorId = doctorId,
                        patientId = patientIdInt,
                        actionType = "DISCHARGE"
                    )

                    if (response.isSuccessful) {
                        Toast.makeText(this@TakeActionActivity, "Patient ${patient?.name} has been discharged.", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@TakeActionActivity, DoctorHistoryNewActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@TakeActionActivity, "Discharge failed", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@TakeActionActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}