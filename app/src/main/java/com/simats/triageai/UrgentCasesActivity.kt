package com.simats.triageai

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.simats.triageai.adapters.UrgentCasesNewAdapter
import com.simats.triageai.databinding.ActivityUrgentCasesBinding
import com.simats.triageai.models.Patient
import com.simats.triageai.models.Priority
import kotlinx.coroutines.launch

class UrgentCasesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUrgentCasesBinding
    private lateinit var adapter: UrgentCasesNewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUrgentCasesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupRecyclerView()
        loadUrgentCases()

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        adapter = UrgentCasesNewAdapter(emptyList())
        binding.rvUrgentCases.layoutManager = LinearLayoutManager(this)
        binding.rvUrgentCases.adapter = adapter
    }

    // 🔥 JSON ARRAY PARSER
    private fun parseJsonArray(json: String?): List<String> {
        if (json.isNullOrBlank()) return emptyList()

        return try {
            val array = org.json.JSONArray(json)
            List(array.length()) { array.getString(it) }
        } catch (e: Exception) {
            listOf(json)
        }
    }

    private fun loadUrgentCases() {
        val prefs = getSharedPreferences("TriageAI", MODE_PRIVATE)
        val adminId = prefs.getInt("user_id", 1)

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getPriorityCases(adminId)

                if (response.isSuccessful && response.body() != null) {
                    val backendPatients = response.body()!!.urgent

                    val uiPatients = backendPatients.map { bp ->
                        com.simats.triageai.utils.PatientMapper.mapToUiPatient(bp)
                    }

                    binding.tvUrgentCount.text = uiPatients.size.toString()
                    adapter.updateList(uiPatients)
                } else {
                    binding.tvUrgentCount.text = "0"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                binding.tvUrgentCount.text = "0"
            }
        }
    }
}
