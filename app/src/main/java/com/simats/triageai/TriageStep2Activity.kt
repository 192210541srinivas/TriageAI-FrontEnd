package com.simats.triageai

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.simats.triageai.databinding.ActivityTriageStep2Binding

class TriageStep2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityTriageStep2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTriageStep2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupValidation()
        setupClickListeners()
    }

    private fun setupValidation() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateForm()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.etSystolic.addTextChangedListener(textWatcher)
        binding.etDiastolic.addTextChangedListener(textWatcher)
        binding.etHeartRate.addTextChangedListener(textWatcher)
        binding.etTemperature.addTextChangedListener(textWatcher)
        binding.etSpo2.addTextChangedListener(textWatcher)
        binding.etRespiratoryRate.addTextChangedListener(textWatcher)

        binding.toggleGroupAvpu.addOnButtonCheckedListener { _, _, _ ->
            validateForm()
            updateAvpuStyles()
        }
    }

    private fun updateAvpuStyles() {
        val buttons = listOf(binding.btnAlert, binding.btnVerbal, binding.btnPainful, binding.btnUnconscious)
        
        buttons.forEach { button ->
            if (button.isChecked) {
                when (button.id) {
                    R.id.btnAlert -> {
                        button.setBackgroundColor(ContextCompat.getColor(this, R.color.stable_dark))
                        button.setTextColor(ContextCompat.getColor(this, R.color.white))
                    }
                    R.id.btnVerbal -> {
                        button.setBackgroundColor(ContextCompat.getColor(this, R.color.urgent_dark))
                        button.setTextColor(ContextCompat.getColor(this, R.color.white))
                    }
                    R.id.btnPainful -> {
                        button.setBackgroundColor(ContextCompat.getColor(this, R.color.selection_orange_very_dark))
                        button.setTextColor(ContextCompat.getColor(this, R.color.white))
                    }
                    R.id.btnUnconscious -> {
                        button.setBackgroundColor(ContextCompat.getColor(this, R.color.critical_dark))
                        button.setTextColor(ContextCompat.getColor(this, R.color.white))
                    }
                }
                button.strokeWidth = 0
            } else {
                button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
                button.strokeWidth = 2
                when (button.id) {
                    R.id.btnAlert -> {
                        button.setTextColor(ContextCompat.getColor(this, R.color.stable_dark))
                        button.strokeColor = ContextCompat.getColorStateList(this, R.color.stable_main)
                    }
                    R.id.btnVerbal -> {
                        button.setTextColor(ContextCompat.getColor(this, R.color.urgent_dark))
                        button.strokeColor = ContextCompat.getColorStateList(this, R.color.urgent_main)
                    }
                    R.id.btnPainful -> {
                        button.setTextColor(ContextCompat.getColor(this, R.color.selection_orange_very_dark))
                        button.strokeColor = ContextCompat.getColorStateList(this, R.color.selection_orange_dark)
                    }
                    R.id.btnUnconscious -> {
                        button.setTextColor(ContextCompat.getColor(this, R.color.critical_dark))
                        button.strokeColor = ContextCompat.getColorStateList(this, R.color.critical_main)
                    }
                }
            }
        }
    }

    private fun validateForm() {
        val systolic = binding.etSystolic.text.toString().trim()
        val diastolic = binding.etDiastolic.text.toString().trim()
        val heartRate = binding.etHeartRate.text.toString().trim()
        val temp = binding.etTemperature.text.toString().trim()
        val spo2 = binding.etSpo2.text.toString().trim()
        val respiratoryRate = binding.etRespiratoryRate.text.toString().trim()
        val avpuSelected = binding.toggleGroupAvpu.checkedButtonId != -1

        val isValid = systolic.isNotEmpty() && diastolic.isNotEmpty() && 
                      heartRate.isNotEmpty() && temp.isNotEmpty() && 
                      spo2.isNotEmpty() && respiratoryRate.isNotEmpty() &&
                      avpuSelected
        
        binding.btnContinue.isEnabled = isValid
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnContinue.setOnClickListener {
            val systolicStr = binding.etSystolic.text.toString().trim()
            val diastolicStr = binding.etDiastolic.text.toString().trim()
            val heartRateStr = binding.etHeartRate.text.toString().trim()
            val tempStr = binding.etTemperature.text.toString().trim()
            val spo2Str = binding.etSpo2.text.toString().trim()
            val respiratoryRateStr = binding.etRespiratoryRate.text.toString().trim()

            val sysVal = systolicStr.toFloatOrNull() ?: 0f
            val diaVal = diastolicStr.toFloatOrNull() ?: 0f
            val hrVal = heartRateStr.toFloatOrNull() ?: 0f
            val tempVal = tempStr.toFloatOrNull() ?: 0f
            val spo2Val = spo2Str.toFloatOrNull() ?: 0f
            val rrVal = respiratoryRateStr.toFloatOrNull() ?: 0f

            val vSys = com.simats.triageai.utils.ValidationUtils.validateVitalRange("SYSTOLIC", sysVal)
            val vDia = com.simats.triageai.utils.ValidationUtils.validateVitalRange("DIASTOLIC", diaVal)
            val vHr = com.simats.triageai.utils.ValidationUtils.validateVitalRange("HR", hrVal)
            val vTemp = com.simats.triageai.utils.ValidationUtils.validateVitalRange("TEMP", tempVal)
            val vSpo2 = com.simats.triageai.utils.ValidationUtils.validateVitalRange("SPO2", spo2Val)
            val vRr = com.simats.triageai.utils.ValidationUtils.validateVitalRange("RR", rrVal)

            if (vSys is com.simats.triageai.utils.ValidationResult.Invalid) {
                binding.etSystolic.error = vSys.message
                return@setOnClickListener
            }
            if (vDia is com.simats.triageai.utils.ValidationResult.Invalid) {
                binding.etDiastolic.error = vDia.message
                return@setOnClickListener
            }
            if (vHr is com.simats.triageai.utils.ValidationResult.Invalid) {
                binding.etHeartRate.error = vHr.message
                return@setOnClickListener
            }
            if (vTemp is com.simats.triageai.utils.ValidationResult.Invalid) {
                binding.etTemperature.error = vTemp.message
                return@setOnClickListener
            }
            if (vSpo2 is com.simats.triageai.utils.ValidationResult.Invalid) {
                binding.etSpo2.error = vSpo2.message
                return@setOnClickListener
            }
            if (vRr is com.simats.triageai.utils.ValidationResult.Invalid) {
                binding.etRespiratoryRate.error = vRr.message
                return@setOnClickListener
            }

            val intent = Intent(this, TriageStep3Activity::class.java)
            // Pass forward Step 1 data
            intent.putExtras(this.intent)
            
            // Add Step 2 data
            intent.putExtra("SYSTOLIC", sysVal.toInt())
            intent.putExtra("DIASTOLIC", diaVal.toInt())
            intent.putExtra("HEART_RATE", hrVal.toInt())
            intent.putExtra("TEMPERATURE", tempVal)
            intent.putExtra("SPO2", spo2Val.toInt())
            intent.putExtra("RESPIRATORY_RATE", rrVal.toInt())
            
            val avpu = when (binding.toggleGroupAvpu.checkedButtonId) {
                R.id.btnAlert -> "Alert"
                R.id.btnVerbal -> "Verbal"
                R.id.btnPainful -> "Pain"
                R.id.btnUnconscious -> "Unresponsive"
                else -> "Unknown"
            }
            intent.putExtra("AVPU", avpu)

            startActivity(intent)
        }
    }
}
