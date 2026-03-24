package com.simats.triageai

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.simats.triageai.databinding.ActivityNonUrgentDetailBinding
import com.simats.triageai.models.Patient

class NonUrgentDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNonUrgentDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNonUrgentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val patient = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("patient", Patient::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Patient>("patient")
        }
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
    }

    private fun populateData(p: Patient) {
        binding.apply {
            tvPatientName.text = p.name
            tvPatientMeta.text = "${p.age} years old • ${p.id}"

            tvBp.text = p.bp
            tvHr.text = p.hr
            tvSpo2.text = p.spo2
            tvAvpu.text = "Alert"

            tvRiskScore.text = p.riskScore.toString()
            pbRiskScore.progress = p.riskScore
            tvWaitingTime.text = p.waitTime
            tvArrivalTime.text = "14:25 PM"
        }
    }
}
