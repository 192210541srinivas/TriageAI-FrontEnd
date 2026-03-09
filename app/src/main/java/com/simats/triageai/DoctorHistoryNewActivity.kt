package com.simats.triageai

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.simats.triageai.adapters.HistoryNewAdapter
import com.simats.triageai.databinding.ActivityDoctorHistoryNewBinding
import com.simats.triageai.models.Patient
import com.simats.triageai.models.Priority
import kotlinx.coroutines.launch

class DoctorHistoryNewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorHistoryNewBinding
    private lateinit var adapter: HistoryNewAdapter
    private var historyPatients: List<Patient> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorHistoryNewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupUI()
        loadHistoryFromBackend()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.navDashboard.setOnClickListener {
            startActivity(Intent(this, DoctorDashboardActivity::class.java))
            finish()
        }

        binding.navPatients.setOnClickListener {
            startActivity(Intent(this, DoctorPatientsActivity::class.java))
            finish()
        }

        binding.navSettings.setOnClickListener {
            startActivity(Intent(this, DoctorSettingsActivity::class.java))
            finish()
        }
    }

    private fun loadHistoryFromBackend() {
        val prefs = getSharedPreferences("TriageAI", MODE_PRIVATE)
        val doctorId = prefs.getInt("user_id", -1)

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getDoctorHistory(doctorId)

                if (response.isSuccessful && response.body() != null) {
                    val historyList = response.body()!!.map { item ->
                        Patient(
                            id = item.case_code ?: "",
                            name = item.patient_name ?: "Unknown",
                            age = 0,
                            gender = "",
                            condition = item.case_type ?: "NON_URGENT",
                            bp = "",
                            hr = "",
                            spo2 = "",
                            temp = "",
                            respRate = "",
                            riskScore = item.ai_risk_score,
                            waitTime = item.created_at ?: "",
                            priority = when(item.case_type) {
                                "CRITICAL" -> Priority.CRITICAL
                                "URGENT" -> Priority.URGENT
                                else -> Priority.NON_URGENT
                            },
                            isDischarged = true,
                            outcome = item.outcome ?: "Treated & Released"
                        )
                    }

                    historyPatients = historyList

                    adapter = HistoryNewAdapter(historyPatients) { patient ->
                        val intent = Intent(this@DoctorHistoryNewActivity, CaseReportActivity::class.java)
                        intent.putExtra("patient", patient)
                        startActivity(intent)
                    }

                    binding.rvHistory.layoutManager = LinearLayoutManager(this@DoctorHistoryNewActivity)
                    binding.rvHistory.adapter = adapter
                    binding.tvCasesCount.text = "Showing ${historyPatients.size} cases"
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}