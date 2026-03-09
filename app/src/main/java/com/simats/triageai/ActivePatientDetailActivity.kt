package com.simats.triageai

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.simats.triageai.databinding.ActivityActivePatientDetailBinding
import com.simats.triageai.models.Patient
import com.simats.triageai.models.Priority

class ActivePatientDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityActivePatientDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActivePatientDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val patient = intent.getParcelableExtra<Patient>("patient")
        if (patient == null) {
            Toast.makeText(this, "Patient data not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        populateData(patient)

        binding.btnBack.setOnClickListener { finish() }

        // Role-based logic
        val prefs = getSharedPreferences("TriageAI", MODE_PRIVATE)
        val role = prefs.getString("role", "doctor")?.lowercase() ?: "doctor"

        if (role == "paramedic") {
            binding.btnTakeAction.text = "Update Vitals"
            
            binding.btnTakeAction.setOnClickListener {
                val intent = Intent(this, VitalsUpdateActivity::class.java)
                intent.putExtra("patient", patient)
                startActivity(intent)
            }
        } else {
            binding.btnTakeAction.text = "Take Action"
            binding.btnTakeAction.setOnClickListener {
                val intent = Intent(this, TakeActionActivity::class.java)
                intent.putExtra("patient", patient)
                startActivity(intent)
            }
        }

        // Action button in the card always goes to Vitals Timeline
        binding.btnViewAction.setOnClickListener {
            val intent = Intent(this, VitalsTimelineActivity::class.java)
            intent.putExtra("patient", patient)
            startActivity(intent)
        }

        // Removed View Timeline listener as it's replaced by Action button
    }



    private fun populateData(p: Patient) {
        val (mainColor, bgColor) = when (p.priority) {
            Priority.CRITICAL -> Pair(Color.parseColor("#E02424"), Color.parseColor("#FFF1F2"))
            Priority.URGENT -> Pair(Color.parseColor("#F97316"), Color.parseColor("#FFF7ED"))
            Priority.NON_URGENT -> Pair(Color.parseColor("#10B981"), Color.parseColor("#F0FDF4"))
        }

        binding.apply {
            // Header
            layoutHeader.setBackgroundColor(mainColor)
            tvPatientName.text = p.name
            tvPatientMeta.text = "${p.age} years old • ${p.id}"

            // Priority Badge
            cardPriorityBadge.setCardBackgroundColor(Color.WHITE)
            tvPriorityBadgeText.setTextColor(mainColor)
            ivPriorityIcon.imageTintList = ColorStateList.valueOf(mainColor)
            tvPriorityBadgeText.text = when (p.priority) {
                Priority.CRITICAL -> "CRITICAL"
                Priority.URGENT -> "URGENT"
                Priority.NON_URGENT -> "STABLE"
            }

            // Vitals
            tvBp.text = p.bp
            tvHr.text = p.hr
            tvSpo2.text = p.spo2
            tvAvpu.text = "Alert" // Placeholder contextually available

            // Risk Card
            cardRiskScore.setCardBackgroundColor(bgColor)
            cardRiskScore.strokeColor = mainColor
            ivRiskIcon.imageTintList = ColorStateList.valueOf(mainColor)
            tvRiskScore.text = p.riskScore.toString()
            tvRiskScore.setTextColor(mainColor)
            pbRiskScore.progress = p.riskScore
            pbRiskScore.progressTintList = ColorStateList.valueOf(mainColor)

            // Status Card
            tvWaitingTimeValue.text = p.waitTime
            tvArrivalTimeValue.text = "14:25 PM" // Placeholder

            // Medical History
            chipGroupHistory.removeAllViews()
            p.medicalHistory.forEach { history ->
                val chip = Chip(this@ActivePatientDetailActivity).apply {
                    text = history
                    chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#F5F3FF"))
                    setTextColor(Color.parseColor("#7C3AED"))
                    chipStrokeWidth = 0f
                    setEnsureMinTouchTargetSize(false)
                }
                chipGroupHistory.addView(chip)
            }

            // Bottom Actions
            btnTakeAction.backgroundTintList = ColorStateList.valueOf(mainColor)
        }
    }
}