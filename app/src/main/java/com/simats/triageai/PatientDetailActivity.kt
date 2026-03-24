package com.simats.triageai

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.simats.triageai.databinding.ActivityPatientDetailBinding
import com.simats.triageai.models.Patient
import com.simats.triageai.models.Priority
import kotlinx.coroutines.launch

class PatientDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPatientDetailBinding
    private var patient: Patient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        patient = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("patient", Patient::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Patient>("patient")
        }
        if (patient == null) {
            Toast.makeText(this, "No patient data available", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupUI()
    }

    private fun setupUI() {
        patient?.let { p ->
            binding.apply {
                val (mainColor, bgColor, categoryText) = when (p.priority) {
                    Priority.CRITICAL -> {
                        listOf(
                            ContextCompat.getColor(this@PatientDetailActivity, R.color.critical_main),
                            Color.parseColor("#FFF1F2"),
                            "Red - Immediate"
                        )
                    }
                    Priority.URGENT -> {
                        listOf(
                            ContextCompat.getColor(this@PatientDetailActivity, R.color.urgent_main),
                            Color.parseColor("#FFF7ED"),
                            "Yellow - Urgent"
                        )
                    }
                    Priority.NON_URGENT -> {
                        listOf(
                            ContextCompat.getColor(this@PatientDetailActivity, R.color.stable_main),
                            Color.parseColor("#F0FDF4"),
                            "Green - Stable"
                        )
                    }
                }

                // Header
                layoutHeader.setBackgroundColor(mainColor as Int)
                tvHeaderName.text = p.name
                tvHeaderDetails.text = "${p.age} years • ${p.gender} • ID: ${p.id}"

                val (badgeIcon, badgeColor) = when (p.priority) {
                    Priority.CRITICAL -> Pair(R.drawable.ic_pulse_blue, mainColor)
                    Priority.URGENT -> Pair(R.drawable.ic_pulse_blue, mainColor)
                    Priority.NON_URGENT -> Pair(R.drawable.ic_accuracy, mainColor)
                }
                ivPriorityIcon.setImageResource(badgeIcon)
                ivPriorityIcon.imageTintList = ColorStateList.valueOf(badgeColor as Int)
                tvPriorityBadgeText.text = p.priority.name
                tvPriorityBadgeText.setTextColor(badgeColor)

                // Vitals Card
                tvBp.text = p.bp
                tvHr.text = p.hr
                tvSpo2.text = p.spo2
                tvTemp.text = p.temp
                tvRespRate.text = p.respRate

                // Risk Score Card
                cardRiskScore.setCardBackgroundColor(bgColor as Int)
                cardRiskScore.strokeColor = mainColor as Int
                ivRiskIcon.imageTintList = ColorStateList.valueOf(mainColor)
                tvRiskScore.text = p.riskScore.toString()
                tvRiskScore.setTextColor(mainColor)
                pbRiskScore.progress = p.riskScore
                pbRiskScore.progressTintList = ColorStateList.valueOf(mainColor)

                // Status Card
                tvWaitingTime.text = p.waitTime
                tvArrivalTime.text = "14:25 PM" // Placeholder

                // Medical History Chips
                cgMedicalHistory.removeAllViews()
                p.medicalHistory.forEach { condition ->
                    val chip = com.google.android.material.chip.Chip(this@PatientDetailActivity).apply {
                        text = condition
                        chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#F3E8FF"))
                        setTextColor(Color.parseColor("#7E22CE"))
                        chipStrokeWidth = 0f
                        setEnsureMinTouchTargetSize(false)
                    }
                    cgMedicalHistory.addView(chip)
                }

                // Bottom Actions
                btnPrimaryAction.backgroundTintList = ColorStateList.valueOf(mainColor as Int)
                tvCategoryValue.text = categoryText as String
                cardScore.setCardBackgroundColor(bgColor as Int)
                tvPriorityScore.setTextColor(mainColor as Int)
                tvPriorityScore.text = p.riskScore.toString()

                val prefs = getSharedPreferences("TriageAI", MODE_PRIVATE)
                val role = prefs.getString("role", "doctor")?.lowercase()
                
                if (role == "doctor") {
                    btnPrimaryAction.text = "Treat Patient"
                } else {
                    btnPrimaryAction.text = "Update Vitals"
                }

                btnPrimaryAction.setOnClickListener {
                    if (role == "doctor") {
                        handleTreatPatient(p.id.toIntOrNull() ?: -1)
                    } else {
                        Toast.makeText(this@PatientDetailActivity, "Update Vitals clicked", Toast.LENGTH_SHORT).show()
                    }
                }

                btnBack.setOnClickListener { finish() }
                

            }
        }
    }

    private fun handleTreatPatient(patientId: Int) {
        if (patientId == -1) {
            Toast.makeText(this, "Invalid Patient ID", Toast.LENGTH_SHORT).show()
            return
        }

        val prefs = getSharedPreferences("TriageAI", MODE_PRIVATE)
        val doctorId = prefs.getInt("user_id", -1)

        if (doctorId == -1) {
            Toast.makeText(this, "Doctor session expired", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.assignPatient(
                    doctorId,
                    patientId
                )
                
                if (response.isSuccessful) {
                    Toast.makeText(this@PatientDetailActivity, "Patient assigned to you", Toast.LENGTH_LONG).show()
                    
                    val intent = Intent(this@PatientDetailActivity, DoctorPatientsActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: response.message()
                    Toast.makeText(this@PatientDetailActivity, "Failed: $errorMsg", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@PatientDetailActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
