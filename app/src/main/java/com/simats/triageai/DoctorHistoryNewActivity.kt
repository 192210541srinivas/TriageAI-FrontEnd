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
import android.text.Editable
import android.text.TextWatcher

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
        setupSearch()
        loadHistoryFromBackend()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.navDashboard.setOnClickListener {
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

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterHistory(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterHistory(query: String) {
        val filteredList = if (query.isEmpty()) {
            historyPatients
        } else {
            historyPatients.filter {
                it.name.contains(query, ignoreCase = true) || it.id.contains(query, ignoreCase = true)
            }
        }
        if (::adapter.isInitialized) {
            adapter.updateList(filteredList)
            binding.tvCasesCount.text = "Showing ${filteredList.size} cases"
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
                            numericId = item.patient_id,
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