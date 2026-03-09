package com.simats.triageai

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.simats.triageai.adapters.PatientAdapter
import com.simats.triageai.databinding.ActivityDoctorHistoryBinding
import com.simats.triageai.models.Patient
import com.simats.triageai.models.Priority

class DoctorHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorHistoryBinding
    private lateinit var adapter: PatientAdapter
    private var historyPatients = listOf<Patient>()
    private var searchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupData()
        setupUI()
        setupBottomNav()
    }

    private fun setupData() {
        // Mocking some "treated" patients for history
        historyPatients = listOf(
            Patient("P010", "Alice Cooper", 45, "Female", "Post-op follow up", "120/80", "72 bpm", "98%", null, "18", 10, "Treated 2h ago", Priority.NON_URGENT),
            Patient("P012", "Bob Marley", 32, "Male", "Sprained ankle", "115/75", "68 bpm", "99%", null, "20", 5, "Treated 4h ago", Priority.NON_URGENT),
            Patient("P002", "Jane Smith", 55, "Female", "Controlled hypertension", "140/90", "80 bpm", "97%", null, "22", 25, "Treated 5h ago", Priority.URGENT)
        )
    }

    private fun setupUI() {
        adapter = PatientAdapter(historyPatients, showTreatButton = false)
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = adapter

        // Search logic
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s.toString().lowercase()
                filterList()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterList() {
        val filtered = historyPatients.filter { patient ->
            patient.name.lowercase().contains(searchQuery) || 
            patient.id.lowercase().contains(searchQuery)
        }
        adapter.updateList(filtered)
    }

    private fun setupBottomNav() {
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
}
