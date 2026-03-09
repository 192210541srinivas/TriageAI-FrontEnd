package com.simats.triageai

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.lifecycleScope
import com.simats.triageai.adapters.CriticalCasesAdapter
import com.simats.triageai.databinding.ActivityCriticalCasesBinding
import com.simats.triageai.models.Patient
import com.simats.triageai.models.Priority
import kotlinx.coroutines.launch
import org.json.JSONArray

class CriticalCasesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCriticalCasesBinding
    private lateinit var adapter: CriticalCasesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCriticalCasesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupRecyclerView()
        loadCriticalCases()

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        adapter = CriticalCasesAdapter(mutableListOf())
        binding.rvCriticalCases.layoutManager = LinearLayoutManager(this)
        binding.rvCriticalCases.adapter = adapter
    }

    // 🔥 JSON ARRAY PARSER
    private fun parseJsonArray(json: String?): List<String> {
        if (json.isNullOrBlank()) return emptyList()

        return try {
            val array = JSONArray(json)
            List(array.length()) { array.getString(it) }
        } catch (e: Exception) {
            listOf(json)
        }
    }

    private fun loadCriticalCases() {

        val prefs = getSharedPreferences("TriageAI", MODE_PRIVATE)
        val adminId = prefs.getInt("user_id", 1)

        lifecycleScope.launch {
            try {

                val response = ApiClient.apiService.getPriorityCases(adminId)

                if (response.isSuccessful && response.body() != null) {

                    val backendPatients = response.body()!!.critical

                    val uiPatients = backendPatients.map { bp ->
                        com.simats.triageai.utils.PatientMapper.mapToUiPatient(bp)
                    }

                    binding.tvCriticalCount.text = uiPatients.size.toString()
                    adapter.updateList(uiPatients)

                } else {
                    binding.tvCriticalCount.text = "0"
                }

            } catch (e: Exception) {
                e.printStackTrace()
                binding.tvCriticalCount.text = "0"
            }
        }
    }
}