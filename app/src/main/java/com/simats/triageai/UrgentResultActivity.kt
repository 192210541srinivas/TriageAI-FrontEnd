package com.simats.triageai

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.simats.triageai.databinding.ActivityUrgentResultBinding

class UrgentResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUrgentResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUrgentResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val score = intent.getIntExtra("RISK_SCORE", 0)
        val chiefComplaint = intent.getStringExtra("CHIEF_COMPLAINT") ?: "Medical Evaluation"
        val systolic = intent.getIntExtra("SYSTOLIC", 0)
        val diastolic = intent.getIntExtra("DIASTOLIC", 0)
        val hr = intent.getIntExtra("HEART_RATE", 0)
        val spo2 = intent.getIntExtra("SPO2", 0)
        val temp = intent.getDoubleExtra("TEMPERATURE", 0.0)

        setupUI(score, chiefComplaint, systolic, diastolic, hr, spo2, temp)
        setupClickListeners()
    }

    private fun setupUI(score: Int, complaint: String, sys: Int, dia: Int, hr: Int, o2: Int, temp: Double) {
        binding.tvScoreValue.text = score.toString()
        binding.progressBarRisk.progress = score
        
        // Update Findings
        binding.tvFindingDesc1.text = "BP: $sys/$dia, HR: $hr, SpO2: $o2%, Temp: $temp°C"
        binding.tvFindingHeader2.text = complaint
        binding.tvFindingDesc2.text = "Patient's primary reported symptom"
    }

    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener {
            finish()
        }

        binding.btnAnalysis.setOnClickListener {
            val intent = Intent(this, RiskScoreAnalysisActivity::class.java)
            // Forward everything robustly
            intent.putExtras(getIntent())
            startActivity(intent)
        }

        binding.btnQueue.setOnClickListener {
            val intent = Intent(this, ParamedicDashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
