package com.simats.triageai

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.simats.triageai.databinding.ActivityRiskScoreAnalysisBinding

class RiskScoreAnalysisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiskScoreAnalysisBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiskScoreAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val score = intent.getIntExtra("RISK_SCORE", 0)
        val status = intent.getStringExtra("CASE_TYPE") ?: "NON-URGENT"
        
        val probCritical = intent.getIntExtra("PROB_CRITICAL", 0)
        val probUrgent = intent.getIntExtra("PROB_URGENT", 0)
        val probNonUrgent = intent.getIntExtra("PROB_NONURGENT", 0)

        setupUI(score, status, probCritical, probUrgent, probNonUrgent)
        setupClickListeners()
    }

    private fun setupUI(score: Int, status: String, pCritical: Int, pUrgent: Int, pNonUrgent: Int) {
        // Overall Card
        binding.tvOverallScore.text = score.toString()
        binding.progressBarOverall.progress = score
        binding.tvStatusBadge.text = status.uppercase()

        // Breakdown percentages
        binding.tvCriticalScore.text = "$pCritical%"
        binding.progressBarCritical.progress = pCritical

        binding.tvUrgentScore.text = "$pUrgent%"
        binding.progressBarUrgent.progress = pUrgent

        binding.tvNonUrgentScore.text = "$pNonUrgent%"
        binding.progressBarNonUrgent.progress = pNonUrgent
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnExplain.setOnClickListener {
            val intent = Intent(this, WhyDecisionActivity::class.java)
            // Forward everything: RISK_SCORE, PROB_CRITICAL, PROB_URGENT, PROB_NONURGENT, EXPLANATION
            intent.putExtras(getIntent())
            startActivity(intent)
        }
    }
}
