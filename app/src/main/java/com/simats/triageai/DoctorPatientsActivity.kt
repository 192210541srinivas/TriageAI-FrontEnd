package com.simats.triageai

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.simats.triageai.adapters.PatientAdapter
import com.simats.triageai.databinding.ActivityDoctorPatientsBinding
import com.simats.triageai.models.BackendPatient
import com.simats.triageai.models.Patient
import com.simats.triageai.models.Priority
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DoctorPatientsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorPatientsBinding
    private lateinit var adapter: PatientAdapter
    private var allPatients = listOf<Patient>()
    private var currentFilter: Priority? = null
    private var searchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorPatientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupUI()
        setupBottomNav()
        loadDoctorPatients()
    }

    private fun loadDoctorPatients() {

        val prefs = getSharedPreferences("TriageAI", MODE_PRIVATE)
        val doctorId = prefs.getInt("user_id", -1)

        ApiClient.apiService.getMyPatients(doctorId)
            .enqueue(object : Callback<List<BackendPatient>> {

                override fun onResponse(
                    call: Call<List<BackendPatient>>,
                    response: Response<List<BackendPatient>>
                ) {

                    if (response.isSuccessful && response.body() != null) {

                        val backendPatients = response.body()!!

                        allPatients = backendPatients.map { bp ->
                            com.simats.triageai.utils.PatientMapper.mapToUiPatient(bp)
                        }

                        adapter.updateList(allPatients)

                    }
                }

                override fun onFailure(call: Call<List<BackendPatient>>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    private fun setupUI() {
        adapter = PatientAdapter(allPatients, showTreatButton = false)
        binding.rvPatients.layoutManager = LinearLayoutManager(this)
        binding.rvPatients.adapter = adapter

        // Search logic
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s.toString().lowercase()
                filterList()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Filter chips
        binding.chipAll.setOnClickListener { selectFilter(null, it as TextView) }
        binding.chipCritical.setOnClickListener { selectFilter(Priority.CRITICAL, it as TextView) }
        binding.chipUrgent.setOnClickListener { selectFilter(Priority.URGENT, it as TextView) }
        binding.chipStable.setOnClickListener { selectFilter(Priority.NON_URGENT, it as TextView) }
    }

    private fun selectFilter(priority: Priority?, textView: TextView) {
        currentFilter = priority
        
        // Reset all chips
        val chips = listOf(binding.chipAll, binding.chipCritical, binding.chipUrgent, binding.chipStable)
        chips.forEach {
            it.setBackgroundResource(R.drawable.bg_filter_chip_unselected)
            it.setTextColor(getColor(R.color.nav_inactive)) 
        }
        
        // Highlight selected
        textView.setBackgroundResource(R.drawable.bg_filter_chip_selected)
        textView.setTextColor(getColor(R.color.white))
        
        filterList()
    }

    private fun filterList() {
        val filtered = allPatients.filter { patient ->
            val matchesSearch = patient.name.lowercase().contains(searchQuery) || 
                               patient.id.lowercase().contains(searchQuery)
            val matchesPriority = currentFilter == null || patient.priority == currentFilter
            matchesSearch && matchesPriority
        }
        adapter.updateList(filtered)
    }

    private fun setupBottomNav() {
        binding.navDashboard.setOnClickListener {
            startActivity(Intent(this, DoctorDashboardActivity::class.java))
            finish()
        }
        
        binding.navHistory.setOnClickListener {
            startActivity(Intent(this, DoctorHistoryNewActivity::class.java))
            finish()
        }

        binding.navSettings.setOnClickListener {
            startActivity(Intent(this, DoctorSettingsActivity::class.java))
            finish()
        }
    }
}
