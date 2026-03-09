package com.simats.triageai

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.simats.triageai.databinding.ActivityTriageStep5Binding

class TriageStep5Activity : AppCompatActivity() {

    private lateinit var binding: ActivityTriageStep5Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTriageStep5Binding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        displayReviewData()
        setupClickListeners()
    }

    private fun displayReviewData() {
        val intent = intent
        
        // Step 1: Basic Details
        val name = intent.getStringExtra("NAME") ?: "N/A"
        val age = intent.getIntExtra("AGE", 0)
        val gender = intent.getStringExtra("GENDER") ?: "N/A"
        
        binding.rvName.text = name
        binding.rvAge.text = "$age years"
        binding.rvGender.text = gender

        // Step 2: Vital Signs
        val systolic = intent.getIntExtra("SYSTOLIC", 0)
        val diastolic = intent.getIntExtra("DIASTOLIC", 0)
        val heartRate = intent.getIntExtra("HEART_RATE", 0)
        val temperature = intent.getFloatExtra("TEMPERATURE", 0f)
        val spo2 = intent.getIntExtra("SPO2", 0)
        
        binding.rvBp.text = "$systolic/$diastolic mmHg"
        binding.rvHr.text = "$heartRate bpm"
        binding.rvTemp.text = "$temperature°C"
        binding.rvSpo2.text = "$spo2%"

        // Step 3: Symptoms
        val complaint = intent.getStringExtra("CHIEF_COMPLAINT") ?: "N/A"
        binding.rvComplaint.text = complaint

        // Step 4: Medical History (Chronic Conditions)
        val conditions = intent.getStringArrayListExtra("CHRONIC_CONDITIONS")
        binding.rvHistory.text = if (conditions.isNullOrEmpty()) "None reported" else conditions.joinToString(", ")
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSubmit.setOnClickListener {
            val nextIntent = Intent(this, AIProcessingActivity::class.java)
            // Forward all data collected
            nextIntent.putExtras(intent)
            startActivity(nextIntent)
            finish()
        }
    }
}
