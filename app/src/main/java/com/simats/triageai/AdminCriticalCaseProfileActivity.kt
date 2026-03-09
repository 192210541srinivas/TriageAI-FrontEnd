package com.simats.triageai

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import com.simats.triageai.models.NearbyDoctor
import com.simats.triageai.models.BackendPatient

class AdminCriticalCaseProfileActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_critical_case_profile)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val btnSearch = findViewById<Button>(R.id.btnSearchExternal)
        val progress = findViewById<View>(R.id.progressSearch)
        val layoutDoctors = findViewById<View>(R.id.layoutDoctorsNearby)

        val patientId = intent.getIntExtra("PATIENT_ID", -1)
        val adminId = getSharedPreferences("TriageAI", MODE_PRIVATE).getInt("user_id", -1)

        if (patientId != -1) {
            loadPatientDetails(patientId)
        }

        btnSearch.setOnClickListener {
            if (adminId == -1) {
                Toast.makeText(this, "Admin session not found", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            checkLocationPermissionAndSearch(btnSearch, progress, layoutDoctors, patientId, adminId)
        }
    }

    private fun loadPatientDetails(patientId: Int) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getPatientProfile(patientId)
                if (response.isSuccessful && response.body() != null) {
                    bindPatientData(response.body()!!)
                } else {
                    Toast.makeText(this@AdminCriticalCaseProfileActivity, "Failed to load patient details", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun bindPatientData(bp: BackendPatient) {
        val patient = com.simats.triageai.utils.PatientMapper.mapToUiPatient(bp)
        
        findViewById<TextView>(R.id.tvPatientName).text = patient.name
        findViewById<TextView>(R.id.tvPatientDemographics).text = "${patient.age} years • ${patient.gender} • ID: P${patient.id}"
        
        // Vitals
        findViewById<TextView>(R.id.tvBloodPressure).text = patient.bp
        findViewById<TextView>(R.id.tvHeartRate).text = patient.hr
        findViewById<TextView>(R.id.tvTemperature).text = patient.temp
        findViewById<TextView>(R.id.tvSpo2).text = patient.spo2
        findViewById<TextView>(R.id.tvRespiratoryRate).text = if (patient.respRate != "--") "${patient.respRate} breaths/min" else "--"
        
        // Chief Complaint
        findViewById<TextView>(R.id.tvChiefComplaint).text = patient.condition.ifBlank { "No complaint" }
        findViewById<TextView>(R.id.tvComplaintDescription).text = if (patient.symptoms.isNotEmpty()) 
            patient.symptoms.joinToString(", ") else ""

        // Medical History
        findViewById<TextView>(R.id.tvMedicalHistory).text = if (patient.medicalHistory.isNotEmpty()) 
            patient.medicalHistory.joinToString(", ") else "None recorded"
            
        findViewById<TextView>(R.id.tvMedications).text = if (patient.medications.isNotEmpty()) 
            patient.medications.joinToString(", ") else "None recorded"
        findViewById<TextView>(R.id.tvAllergies).text = if (patient.allergies.isNotEmpty()) 
            patient.allergies.joinToString(", ") else "None recorded"
        
        // Contact
        findViewById<TextView>(R.id.tvContactInfo).text = "Phone: ${bp.phone ?: "-"}\nAddress: ${bp.address ?: "-"}"
        
        // Paramedic
        findViewById<TextView>(R.id.tvParamedicName).text = patient.paramedicName.ifBlank { "Unknown" }
        
        // Risk & Priority
        findViewById<TextView>(R.id.tvRiskScore).text = patient.riskScore.toString()
        findViewById<TextView>(R.id.tvPriorityValue).text = patient.priority.name
    }

    private fun checkLocationPermissionAndSearch(btnSearch: Button, progress: View, layoutDoctors: View, patientId: Int, adminId: Int) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1001)
            return
        }

        btnSearch.isEnabled = false
        progress.visibility = View.VISIBLE

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                searchNearbyDoctors(location.latitude, location.longitude, btnSearch, progress, layoutDoctors, patientId, adminId)
            } else {
                Toast.makeText(this, "Could not get location. Ensure GPS is on.", Toast.LENGTH_LONG).show()
                btnSearch.isEnabled = true
                progress.visibility = View.GONE
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Location error: ${it.message}", Toast.LENGTH_SHORT).show()
            btnSearch.isEnabled = true
            progress.visibility = View.GONE
        }
    }

    private fun searchNearbyDoctors(lat: Double, lng: Double, btnSearch: Button, progress: View, layoutDoctors: View, patientId: Int, adminId: Int) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.searchNearbyDoctors(adminId, lat, lng)
                if (response.isSuccessful && response.body()?.status == true) {
                    val doctors = response.body()?.doctors ?: emptyList()
                    
                    if (doctors.isEmpty()) {
                        Toast.makeText(this@AdminCriticalCaseProfileActivity, "No nearby doctors found at $lat, $lng", Toast.LENGTH_LONG).show()
                    } else {
                        updateDoctorsList(doctors, patientId, adminId)
                        if (layoutDoctors.visibility != View.VISIBLE) {
                            showDoctorsLayout(layoutDoctors)
                        }
                    }
                } else {
                    Toast.makeText(this@AdminCriticalCaseProfileActivity, "Failed to find nearby doctors", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AdminCriticalCaseProfileActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                btnSearch.isEnabled = true
                progress.visibility = View.GONE
            }
        }
    }

    private fun showDoctorsLayout(layout: View) {
        layout.visibility = View.VISIBLE
        layout.alpha = 0f
        layout.translationY = 50f
        layout.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500)
            .setInterpolator(android.view.animation.DecelerateInterpolator())
            .start()
    }

    private fun updateDoctorsList(doctors: List<NearbyDoctor>, patientId: Int, adminId: Int) {
        val rv = findViewById<RecyclerView>(R.id.rvDoctorsNearby)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = DoctorsNearbyAdapter(doctors) { doctorId ->
            assignCase(adminId, patientId, doctorId)
        }
    }

    private fun assignCase(adminId: Int, patientId: Int, doctorId: Int) {
        if (patientId == -1) {
            Toast.makeText(this, "Invalid patient ID", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.assignCase(adminId, patientId, doctorId)
                if (response.isSuccessful && response.body()?.status == true) {
                    Toast.makeText(this@AdminCriticalCaseProfileActivity, "Assignment request sent successfully", Toast.LENGTH_LONG).show()
                    finish() // Close profile after assignment
                } else {
                    Toast.makeText(this@AdminCriticalCaseProfileActivity, "Failed to assign case", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AdminCriticalCaseProfileActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val btnSearch = findViewById<Button>(R.id.btnSearchExternal)
            val progress = findViewById<View>(R.id.progressSearch)
            val layoutDoctors = findViewById<View>(R.id.layoutDoctorsNearby)
            val patientId = intent.getIntExtra("PATIENT_ID", -1)
            val adminId = getSharedPreferences("TriageAI", MODE_PRIVATE).getInt("user_id", -1)
            
            checkLocationPermissionAndSearch(btnSearch, progress, layoutDoctors, patientId, adminId)
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    class DoctorsNearbyAdapter(
        private val items: List<NearbyDoctor>,
        private val onAssignClick: (Int) -> Unit
    ) : RecyclerView.Adapter<DoctorsNearbyAdapter.ViewHolder>() {
        
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.tvDoctorName)
            val rating: TextView = view.findViewById(R.id.tvRating)
            val specialty: TextView = view.findViewById(R.id.tvSpecialty)
            val status: TextView = view.findViewById(R.id.tvStatus)
            val load: TextView = view.findViewById(R.id.tvCurrentLoad)
            val btnAssign: View = view.findViewById(R.id.btnAssign)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_doctor_assign_card, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.name.text = item.name
            holder.rating.text = "★ 4.9" // Backend doesn't have rating yet, using default
            holder.specialty.text = "External Doctor"
            holder.status.text = item.status
            holder.load.text = "Distance: ${item.distanceKm} km"
            
            holder.btnAssign.setOnClickListener {
                onAssignClick(item.id)
            }
        }

        override fun getItemCount() = items.size
    }
}
