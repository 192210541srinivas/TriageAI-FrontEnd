package com.simats.triageai

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.simats.triageai.adapters.NonUrgentCasesNewAdapter
import com.simats.triageai.databinding.ActivityNonUrgentCasesBinding
import com.simats.triageai.models.Patient
import com.simats.triageai.models.Priority
import kotlinx.coroutines.launch

class NonUrgentCasesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNonUrgentCasesBinding
    private lateinit var adapter: NonUrgentCasesNewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNonUrgentCasesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupRecyclerView()
        loadNonUrgentCases()

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        adapter = NonUrgentCasesNewAdapter(emptyList())
        binding.rvNonUrgentCases.layoutManager = LinearLayoutManager(this)
        binding.rvNonUrgentCases.adapter = adapter
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

    private fun loadNonUrgentCases() {
        val prefs = getSharedPreferences("TriageAI", MODE_PRIVATE)
        val adminId = prefs.getInt("user_id", 1)

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getPriorityCases(adminId)

                if (response.isSuccessful && response.body() != null) {
                    val backendPatients = response.body()!!.non_urgent

                    val uiPatients = backendPatients.map { bp ->
                        com.simats.triageai.utils.PatientMapper.mapToUiPatient(bp)
                    }

                    binding.tvStableCount.text = uiPatients.size.toString()
                    adapter.updateList(uiPatients)
                } else {
                    binding.tvStableCount.text = "0"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                binding.tvStableCount.text = "0"
            }
        }
    }
}
