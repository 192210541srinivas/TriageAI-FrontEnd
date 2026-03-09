package com.simats.triageai

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.simats.triageai.databinding.ActivityTriageStep3Binding

class TriageStep3Activity : AppCompatActivity() {

    private lateinit var binding: ActivityTriageStep3Binding
    private val selectedSymptoms = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTriageStep3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupValidation()
        setupSymptomListeners()
        setupTraumaListeners()
        setupClickListeners()
    }

    private fun setupValidation() {
        binding.etComplaint.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateForm()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupSymptomListeners() {
        val symptoms = listOf(
            binding.symptomChestPain, binding.symptomShortnessOfBreath, binding.symptomSevereHeadache,
            binding.symptomAbdominalPain, binding.symptomNausea, binding.symptomFever,
            binding.symptomDizziness, binding.symptomCough, binding.symptomFatigue,
            binding.symptomRash, binding.symptomLoc, binding.symptomConfusion,
            binding.symptomTrauma
        )

        symptoms.forEach { button ->
            button.setOnClickListener {
                // Remove manual toggling, let MaterialButton handle it
                val symptomName = button.text.toString()
                if (button.isChecked) selectedSymptoms.add(symptomName) else selectedSymptoms.remove(symptomName)
                
                updateButtonStyle(button)
                
                if (button.id == binding.symptomTrauma.id) {
                    toggleTraumaOptions(button.isChecked)
                }
            }
        }
    }

    private fun setupTraumaListeners() {
        val traumaSubOptions = listOf(
            binding.traumaBlunt, binding.traumaPenetrating, 
            binding.traumaBurns, binding.traumaPTSD, binding.traumaStress
        )

        traumaSubOptions.forEach { button ->
            button.setOnClickListener {
                // Remove manual toggling
                val traumaName = "Trauma: ${button.text}"
                if (button.isChecked) selectedSymptoms.add(traumaName) else selectedSymptoms.remove(traumaName)
                updateButtonStyle(button)
            }
        }
    }

    private fun updateButtonStyle(button: MaterialButton) {
        if (button.isChecked) {
            val colorRes = when (button.id) {
                R.id.symptomChestPain, R.id.symptomShortnessOfBreath, R.id.symptomSevereHeadache, 
                R.id.symptomLoc, R.id.symptomConfusion, R.id.symptomTrauma -> R.color.critical_dark
                R.id.symptomAbdominalPain, R.id.symptomNausea, R.id.symptomFever, R.id.symptomDizziness -> R.color.urgent_dark
                R.id.symptomCough, R.id.symptomFatigue, R.id.symptomRash -> R.color.stable_dark
                else -> R.color.blue_dark
            }
            button.setBackgroundColor(ContextCompat.getColor(this, colorRes))
            button.setTextColor(ContextCompat.getColor(this, R.color.white))
            button.strokeWidth = 0
            
            // Add subtle elevation/presence when selected
            button.elevation = 4f
        } else {
            button.elevation = 0f
            val (bgColorRes, strokeColorRes, textColorRes) = when (button.id) {
                R.id.symptomChestPain, R.id.symptomShortnessOfBreath, R.id.symptomSevereHeadache, 
                R.id.symptomLoc, R.id.symptomConfusion, R.id.symptomTrauma -> 
                    Triple(R.color.critical_bg, R.color.selection_red_dark, R.color.text_primary)
                R.id.symptomAbdominalPain, R.id.symptomNausea, R.id.symptomFever, R.id.symptomDizziness -> 
                    Triple(R.color.urgent_bg, R.color.selection_orange_dark, R.color.text_primary)
                R.id.symptomCough, R.id.symptomFatigue, R.id.symptomRash -> 
                    Triple(R.color.stable_bg, R.color.selection_green_dark, R.color.text_primary)
                else -> Triple(android.R.color.transparent, R.color.selection_grey_dark, R.color.text_secondary)
            }
            
            button.setBackgroundColor(ContextCompat.getColor(this, bgColorRes))
            button.strokeColor = ContextCompat.getColorStateList(this, strokeColorRes)
            button.setTextColor(ContextCompat.getColor(this, textColorRes))
            button.strokeWidth = 2
        }
    }

    private fun toggleTraumaOptions(show: Boolean) {
        binding.layoutTraumaOptions.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun validateForm() {
        val complaint = binding.etComplaint.text.toString().trim()
        binding.btnContinue.isEnabled = complaint.isNotEmpty()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnContinue.setOnClickListener {
            val intent = Intent(this, TriageStep4Activity::class.java)
            intent.putExtras(this.intent)
            intent.putExtra("CHIEF_COMPLAINT", binding.etComplaint.text.toString().trim())
            intent.putExtra("SYMPTOMS", ArrayList(selectedSymptoms))
            startActivity(intent)
        }
    }
}
