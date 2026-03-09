package com.simats.triageai

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.simats.triageai.databinding.ActivityCriticalResultBinding

class CriticalResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCriticalResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCriticalResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val score = intent.getIntExtra("RISK_SCORE", 0)
        val status = intent.getStringExtra("CASE_TYPE") ?: "CRITICAL"
        
        val chiefComplaint = intent.getStringExtra("CHIEF_COMPLAINT") ?: "Medical Evaluation"
        val systolic = intent.getIntExtra("SYSTOLIC", 0)
        val diastolic = intent.getIntExtra("DIASTOLIC", 0)
        val hr = intent.getIntExtra("HEART_RATE", 0)
        val spo2 = intent.getIntExtra("SPO2", 0)
        val temp = intent.getDoubleExtra("TEMPERATURE", 0.0)

        setupUI(score, status, chiefComplaint, systolic, diastolic, hr, spo2, temp)
        setupClickListeners()
    }

    private fun setupUI(score: Int, status: String, complaint: String, sys: Int, dia: Int, hr: Int, o2: Int, temp: Double) {
        binding.tvScoreValue.text = score.toString()
        binding.progressBarRisk.progress = score
        
        // Update Findings
        binding.tvFindingDesc1.text = "BP: $sys/$dia, HR: $hr, SpO2: $o2%, Temp: $temp°C"
        binding.tvFindingHeader2.text = complaint
        binding.tvFindingDesc2.text = "Patient's primary reported symptom"

        when (status.uppercase()) {
            "CRITICAL" -> {
                setTheme(
                    bgColor = "#DC2626",
                    iconRes = R.drawable.ic_warning_triangle,
                    badgeText = "CRITICAL PRIORITY",
                    title = "Immediate Attention Required",
                    subtitle = "Patient requires urgent medical intervention",
                    findingsTitle = "Critical Findings",
                    actionsTitle = "Immediate Actions Required",
                    actionsText = "1. Notify emergency team immediately\n2. Prepare for possible cardiac intervention\n3. Monitor vitals continuously\n4. Arrange immediate diagnostic tests",
                    actionBgColor = "#FEF2F2",
                    actionTextColor = "#991B1B"
                )
            }
            "URGENT" -> {
                setTheme(
                    bgColor = "#EA580C", // Deep Orange
                    iconRes = R.drawable.ic_clock,
                    badgeText = "URGENT PRIORITY",
                    title = "Requires Prompt Attention",
                    subtitle = "Patient should be seen within 30 minutes",
                    findingsTitle = "Key Findings",
                    actionsTitle = "Recommended Actions",
                    actionsText = "1. Schedule for evaluation within 30 minutes\n2. Continue vital signs monitoring\n3. Prepare treatment area\n4. Alert available physician",
                    actionBgColor = "#FFF7ED",
                    actionTextColor = "#9A3412"
                )
            }
            "NON-URGENT", "STABLE" -> {
                setTheme(
                    bgColor = "#059669", // Green
                    iconRes = R.drawable.ic_completed_check,
                    badgeText = "NON-URGENT / STABLE",
                    title = "Patient is Stable",
                    subtitle = "Can be treated when resources are available",
                    findingsTitle = "Assessment Summary",
                    actionsTitle = "Care Recommendations",
                    actionsText = "1. Place in standard waiting queue\n2. Periodic vital signs check (every 30-60 min)\n3. Provide comfort measures if needed\n4. Schedule for routine examination",
                    actionBgColor = "#ECFDF5",
                    actionTextColor = "#065F46"
                )
            }
        }
    }

    private fun setTheme(
        bgColor: String,
        iconRes: Int,
        badgeText: String,
        title: String,
        subtitle: String,
        findingsTitle: String,
        actionsTitle: String,
        actionsText: String,
        actionBgColor: String,
        actionTextColor: String
    ) {
        val colorInt = Color.parseColor(bgColor)
        binding.viewHeaderBg.setBackgroundColor(colorInt)
        binding.ivStatusIcon.setImageResource(iconRes)
        binding.ivStatusIcon.imageTintList = ColorStateList.valueOf(colorInt)
        binding.badgeUrgency.text = badgeText
        binding.tvTitle.text = title
        binding.tvSubtitle.text = subtitle
        
        binding.tvScoreLabel.setTextColor(colorInt)
        binding.tvScoreValue.setTextColor(colorInt)
        binding.progressBarRisk.progressTintList = ColorStateList.valueOf(colorInt)
        binding.cardRiskScore.setStrokeColor(ColorStateList.valueOf(Color.parseColor(actionBgColor)))

        binding.tvFindingsTitle.text = findingsTitle
        
        binding.cardActions.setCardBackgroundColor(Color.parseColor(actionBgColor))
        binding.tvActionsTitle.text = actionsTitle
        binding.tvActionsTitle.setTextColor(Color.parseColor(actionTextColor))
        binding.tvActionList.text = actionsText
        binding.tvActionList.setTextColor(Color.parseColor(actionTextColor))

        binding.btnAnalysis.backgroundTintList = ColorStateList.valueOf(colorInt)
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
