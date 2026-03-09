package com.simats.triageai

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.simats.triageai.databinding.ActivityWhyDecisionBinding

class WhyDecisionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWhyDecisionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWhyDecisionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val explanation = intent.getStringArrayListExtra("EXPLANATION") ?: arrayListOf()
        val status = intent.getStringExtra("CASE_TYPE") ?: "NON-URGENT"

        setupUI(status, explanation)
        setupClickListeners()
    }

    private fun setupUI(status: String, explanationList: List<String>) {
        binding.tvSummaryExplanation.text = "The AI model classified this case as $status based on the following reasoning factors:"

        if (explanationList.isEmpty()) {
            addExplanationCard("Clinical Data", "The classification was determined by the patient's vital signs and symptoms provided during triage.")
        } else {
            explanationList.forEachIndexed { index, text ->
                addExplanationCard("Reasoning Factor ${index + 1}", text)
            }
        }
    }

    private fun addExplanationCard(title: String, description: String) {
        val cardView = com.google.android.material.card.MaterialCardView(this).apply {
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 32)
            }
            radius = 40f
            cardElevation = 4f
        }

        val padding = 48
        val innerLayout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(padding, padding, padding, padding)
        }

        val titleTextView = android.widget.TextView(this).apply {
            text = title
            setTextColor(android.graphics.Color.parseColor("#111827"))
            textSize = 16f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }

        val descTextView = android.widget.TextView(this).apply {
            text = description
            setTextColor(android.graphics.Color.parseColor("#4B5563"))
            textSize = 14f
            setPadding(0, 16, 0, 0)
            setLineSpacing(0f, 1.2f)
        }

        innerLayout.addView(titleTextView)
        innerLayout.addView(descTextView)
        cardView.addView(innerLayout)
        
        binding.layoutExplanations.addView(cardView)
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnActions.setOnClickListener {
            // Navigate to Paramedic Dashboard and clear the task stack
            val intent = Intent(this, ParamedicDashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
