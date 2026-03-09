package com.simats.triageai

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.simats.triageai.databinding.ActivityTriageStep4Binding

class TriageStep4Activity : AppCompatActivity() {

    private lateinit var binding: ActivityTriageStep4Binding
    private val selectedConditions = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTriageStep4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupConditionListeners()
        setupClickListeners()
    }

    private fun setupConditionListeners() {
        val conditions = listOf(
            binding.condDiabetes, binding.condHypertension, binding.condHeartDisease,
            binding.condAsthma, binding.condCopd, binding.condKidney,
            binding.condLiver, binding.condCancer, binding.condStroke,
            binding.condSeizures
        )

        conditions.forEach { button ->
            button.setOnClickListener {
                // Remove manual toggling
                val condName = button.text.toString()
                if (button.isChecked) selectedConditions.add(condName) else selectedConditions.remove(condName)
                updateButtonStyle(button)
            }
        }
    }

    private fun updateButtonStyle(button: MaterialButton) {
        if (button.isChecked) {
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.blue_dark))
            button.setTextColor(ContextCompat.getColor(this, R.color.white))
            button.strokeWidth = 0
        } else {
            button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            button.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
            button.strokeWidth = 2
            button.strokeColor = ContextCompat.getColorStateList(this, R.color.selection_grey_dark)
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSubmit.setOnClickListener {
            val intent = Intent(this, TriageStep5Activity::class.java)
            intent.putExtras(this.intent)
            intent.putExtra("CHRONIC_CONDITIONS", ArrayList(selectedConditions))
            startActivity(intent)
        }
    }
}
