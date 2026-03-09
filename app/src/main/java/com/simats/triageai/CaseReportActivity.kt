package com.simats.triageai

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.simats.triageai.databinding.ActivityCaseReportBinding
import com.simats.triageai.models.Patient
import com.simats.triageai.models.Priority

class CaseReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCaseReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaseReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val patient = intent.getParcelableExtra<Patient>("patient")
        if (patient == null) {
            Toast.makeText(this, "Case data not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        populateData(patient)

        binding.btnBack.setOnClickListener { finish() }
        
        binding.btnExportReport.setOnClickListener {
            Toast.makeText(this, "Generating PDF Report for ${patient.name}...", Toast.LENGTH_LONG).show()
            // Future: Implement PDF generation and sharing
        }
    }

    private fun populateData(p: Patient) {
        binding.tvPatientNameHeader.text = "${p.id} • ${p.name}"
        binding.tvPatientNameSummary.text = p.name
        
        // Case Summary
        // Splitting date and time for multi-line display if it follows the mock format
        val dateTimeValue = p.waitTime.replace(" ", "\n")
        binding.tvDateTime.text = dateTimeValue
        
        binding.tvPriority.text = p.priority.name
        binding.tvPriority.setTextColor(when(p.priority) {
            Priority.CRITICAL -> ContextCompat.getColor(this, R.color.critical_main)
            Priority.URGENT -> ContextCompat.getColor(this, R.color.urgent_main)
            Priority.NON_URGENT -> ContextCompat.getColor(this, R.color.stable_main)
        })
        binding.tvRiskScore.text = "${p.riskScore}%"
        binding.tvOutcome.text = p.outcome ?: "Admitted"

        // AI Analysis
        binding.tvChiefComplaint.text = p.condition 
        binding.tvKeySymptoms.text = p.keySymptoms ?: "Severe chest pain, difficulty breathing"
        binding.tvRiskFactors.text = p.riskFactors ?: "Hypertension, diabetes, elevated vitals"
        binding.tvAiRecommendation.text = p.aiRecommendation ?: "Immediate cardiac evaluation"
        
        // Vital Signs
        binding.tvBp.text = "${p.bp} mmHg"
        binding.tvHr.text = "${p.hr} bpm"
        binding.tvSpo2.text = "${p.spo2}%"
        binding.tvTemp.text = p.temp ?: "37.2°C"

        // Treatment summary
        binding.tvTreatmentSummary.text = p.treatmentSummary ?: "Patient was immediately assessed and stabilized. ECG performed showing signs of acute cardiac event. Cardiac enzymes elevated. Patient admitted to cardiac care unit for further monitoring and treatment. Responded well to initial interventions."
    }
}
