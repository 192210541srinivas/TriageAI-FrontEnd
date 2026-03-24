package com.simats.triageai

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simats.triageai.models.AdminPendingCasesResponse
import com.simats.triageai.models.BackendPatient
import com.simats.triageai.models.Patient
import com.simats.triageai.utils.PatientMapper
import kotlinx.coroutines.launch
import retrofit2.Response

class AdminCriticalExcessActivity : AppCompatActivity() {

    private lateinit var criticalAdapter: CriticalQueueAdapter
    private lateinit var doctorsAdapter: AvailableDoctorsAdapter
    private val criticalPatients = mutableListOf<Patient>()
    private val availableDoctors = mutableListOf<UserProfileResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_critical_excess)

        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        setupRecyclerViews()
        loadData()
    }

    private fun setupRecyclerViews() {
        val rvCritical = findViewById<RecyclerView>(R.id.rvCriticalQueue)
        rvCritical.layoutManager = LinearLayoutManager(this)
        criticalAdapter = CriticalQueueAdapter(criticalPatients) { patient ->
            val intent = Intent(this, AdminCriticalCaseProfileActivity::class.java)
            intent.putExtra("PATIENT_ID", patient.id.toIntOrNull() ?: -1)
            intent.putExtra("patient", patient as android.os.Parcelable)
            startActivity(intent)
        }
        rvCritical.adapter = criticalAdapter

    }

    private fun loadData() {
        val prefs = getSharedPreferences("TriageAI", MODE_PRIVATE)
        val adminId = prefs.getInt("user_id", -1)

        if (adminId == -1) return

        lifecycleScope.launch {
            try {
                // Load critical patients
                val patientResponse = ApiClient.apiService.getPendingCases(adminId)
                if (patientResponse.isSuccessful) {
                    patientResponse.body()?.critical?.let { backendList ->
                        criticalPatients.clear()
                        criticalPatients.addAll(backendList.map { PatientMapper.mapToUiPatient(it) })
                        criticalAdapter.notifyDataSetChanged()
                    }
                }

                // Load internal doctors

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    class CriticalQueueAdapter(private val items: List<Patient>, private val onItemClick: (Patient) -> Unit) : RecyclerView.Adapter<CriticalQueueAdapter.ViewHolder>() {
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.tvPatientName)
            val age: TextView = view.findViewById(R.id.tvPatientAge)
            val waitTime: TextView = view.findViewById(R.id.tvWaitTime)
            val priority: TextView = view.findViewById(R.id.tvPriorityBadge)
            val condition: TextView = view.findViewById(R.id.tvCondition)
            val vitals: TextView = view.findViewById(R.id.tvVitals)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_critical_case_row, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.name.text = item.name
            holder.age.text = "• ${item.age}Y"
            holder.waitTime.text = item.waitTime
            holder.priority.text = item.priority.name
            holder.condition.text = item.condition.ifBlank { "No complaint" }
            
            holder.vitals.text = "HR: ${item.hr}  BP: ${item.bp}  O₂: ${item.spo2}  T: ${item.temp ?: "--°C"}"
            holder.itemView.setOnClickListener { onItemClick(item) }
        }

        override fun getItemCount() = items.size
    }

    class AvailableDoctorsAdapter(private val items: List<UserProfileResponse>) : RecyclerView.Adapter<AvailableDoctorsAdapter.ViewHolder>() {
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.tvDoctorName)
            val specialty: TextView = view.findViewById(R.id.tvSpecialty)
            val ratio: TextView = view.findViewById(R.id.tvWorkloadRatio)
            val progress: ProgressBar = view.findViewById(R.id.pbWorkload)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_available_doctor, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.name.text = item.name
            holder.specialty.text = item.department ?: "General"
            holder.ratio.text = "Available"
            holder.progress.progress = 100
        }

        override fun getItemCount() = items.size
    }
}
