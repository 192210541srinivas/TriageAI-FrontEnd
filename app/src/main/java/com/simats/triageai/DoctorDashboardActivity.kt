package com.simats.triageai

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.simats.triageai.databinding.ActivityDoctorDashboardBinding
import com.google.android.gms.location.LocationServices
import com.simats.triageai.models.Notification
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient

class DoctorDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorDashboardBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var adminId: Int = 0
    private var isDoctorActive: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        loadUserData()
        setupNavigation()
        loadDashboardCounts()
        setupStatusToggle()
        // setupMockNotification() // Removed mock
        
        // Start real-time notification polling
        startNotificationPolling()
        
        // Update location to backend
        updateDoctorLocation()
    }

    private var isNotificationShowing = false

    private fun startNotificationPolling() {
        lifecycleScope.launch {
            while (coroutineContext.isActive) {
                if (!isNotificationShowing) {
                    fetchCaseAssignments()
                }
                delay(10000) // Poll every 10 seconds
            }
        }
    }

    private suspend fun fetchCaseAssignments() {
        try {
            val response = ApiClient.apiService.getPendingAssignment(adminId)
            if (response.isSuccessful && response.body()?.patient != null) {
                showExternalCaseNotification(response.body()!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateDoctorLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1002)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                lifecycleScope.launch {
                    try {
                        val response = ApiClient.apiService.updateLocation(adminId, location.latitude, location.longitude)
                        if (response.isSuccessful && response.body()?.status == true) {
                            // Location updated successfully
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1002 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            updateDoctorLocation()
        }
    }


    private fun showExternalCaseNotification(data: com.simats.triageai.models.PendingAssignmentResponse) {
        if (isNotificationShowing) return
        val details = data.patient ?: return
        isNotificationShowing = true

        val notificationView = layoutInflater.inflate(R.layout.layout_case_assignment_notification, binding.root, false)
        
        // Fill Data
        notificationView.findViewById<android.widget.TextView>(R.id.tvPatientName).text = details.fullName
        notificationView.findViewById<android.widget.TextView>(R.id.tvPatientMeta).text = 
            "${details.age}Y • ${details.gender} • ${details.caseType}"
            
        notificationView.findViewById<android.widget.TextView>(R.id.tvBP).text = if (!details.bp.isNullOrBlank()) details.bp else "--/--"
        
        // 🔥 Added Heart Rate
        val tvHR = notificationView.findViewById<android.widget.TextView>(R.id.tvHR)
        if (tvHR != null) {
            tvHR.text = if (details.heartRate != null && details.heartRate != 0) "${details.heartRate} bpm" else "-- bpm"
        }
        
        notificationView.findViewById<android.widget.TextView>(R.id.tvSpO2).text = if (details.spo2 != null && details.spo2 != 0) "${details.spo2}%" else "--%"
        notificationView.findViewById<android.widget.TextView>(R.id.tvTemp).text = if (details.temperature != null && details.temperature != 0.0) "${details.temperature}°C" else "--°C"
        notificationView.findViewById<android.widget.TextView>(R.id.tvRR).text = if (details.respiratoryRate != null && details.respiratoryRate != 0) details.respiratoryRate.toString() else "--"
        notificationView.findViewById<android.widget.TextView>(R.id.tvComplaint).text = details.chiefComplaint ?: "No complaint"
        
        val params = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
            androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_PARENT,
            androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        params.topToTop = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
        params.startToStart = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
        params.endToEnd = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
        params.topMargin = 100 
        
        notificationView.layoutParams = params
        notificationView.elevation = 100f
        notificationView.translationY = -800f 
        
        binding.root.addView(notificationView)

        notificationView.animate()
            .translationY(0f)
            .setDuration(800)
            .setInterpolator(android.view.animation.OvershootInterpolator())
            .start()

        // Handle buttons
        notificationView.findViewById<android.view.View>(R.id.btnAccept).setOnClickListener {
            lifecycleScope.launch {
                try {
                    val resp = ApiClient.apiService.acceptCase(data.notificationId ?: -1, adminId)
                    if (resp.isSuccessful && resp.body()?.status == true) {
                        Toast.makeText(this@DoctorDashboardActivity, "Case Accepted", Toast.LENGTH_SHORT).show()
                        dismissNotification(notificationView)
                        loadDashboardCounts() // Refresh counts
                    } else {
                        Toast.makeText(this@DoctorDashboardActivity, "Failed to accept case", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@DoctorDashboardActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        notificationView.findViewById<android.view.View>(R.id.btnReject).setOnClickListener {
            lifecycleScope.launch {
                try {
                    val resp = ApiClient.apiService.rejectCase(data.notificationId ?: -1, adminId)
                    if (resp.isSuccessful && resp.body()?.status == true) {
                        Toast.makeText(this@DoctorDashboardActivity, "Case Rejected", Toast.LENGTH_SHORT).show()
                        dismissNotification(notificationView)
                    } else {
                        Toast.makeText(this@DoctorDashboardActivity, "Failed to reject case", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@DoctorDashboardActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun dismissNotification(view: android.view.View) {
        view.animate()
            .alpha(0f)
            .translationY(-200f)
            .setDuration(400)
            .withEndAction {
                binding.root.removeView(view)
                isNotificationShowing = false
            }.start()
    }

    private fun setupStatusToggle() {
        binding.fabStatus.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val response = ApiClient.apiService.toggleDoctorStatus(adminId)
                    if (response.isSuccessful && response.body() != null) {
                        isDoctorActive = response.body()!!.newStatus == "ACTIVE"
                        updateStatusUI(animate = true)
                    } else {
                        android.widget.Toast.makeText(this@DoctorDashboardActivity, "Failed to update status", android.widget.Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    android.widget.Toast.makeText(this@DoctorDashboardActivity, "Error: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
        // Initial state (ideally this should be fetched from backend)
        updateStatusUI(animate = false)
    }

    private fun updateStatusUI(animate: Boolean) {
        val color = if (isDoctorActive) {
            ContextCompat.getColor(this, R.color.stable_main)
        } else {
            ContextCompat.getColor(this, R.color.critical_main)
        }
        
        val text = if (isDoctorActive) "Active" else "Busy"
        val iconRes = if (isDoctorActive) R.drawable.ic_tick else R.drawable.ic_clock

        if (animate) {
            binding.fabStatus.animate()
                .scaleX(0.8f)
                .scaleY(0.8f)
                .setDuration(150)
                .withEndAction {
                    binding.fabStatus.text = text
                    binding.fabStatus.setIconResource(iconRes)
                    binding.fabStatus.backgroundTintList = ColorStateList.valueOf(color)
                    
                    binding.fabStatus.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(150)
                        .setInterpolator(AccelerateDecelerateInterpolator())
                        .start()
                }.start()
        } else {
            binding.fabStatus.text = text
            binding.fabStatus.setIconResource(iconRes)
            binding.fabStatus.backgroundTintList = ColorStateList.valueOf(color)
        }
    }

    // ---------------- USER DATA ----------------
    private fun loadUserData() {
        val prefs = getSharedPreferences("TriageAI", MODE_PRIVATE)
        adminId = prefs.getInt("user_id", 1)
        val role = prefs.getString("role", "Doctor")
        val prettyRole = role?.replaceFirstChar { it.uppercase() } ?: ""
        binding.tvSubtitle.text = "Welcome, $prettyRole"
    }

    // ---------------- LOAD COUNTS FROM API ----------------
    private fun loadDashboardCounts() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getPriorityCases(adminId)
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    binding.tvCriticalCount.text = data.critical.size.toString()
                    binding.tvUrgentCount.text = data.urgent.size.toString()
                    binding.tvStableCount.text = data.non_urgent.size.toString()
                    
                    val total = data.critical.size + data.urgent.size + data.non_urgent.size
                    binding.tvDoctorTriageCount.text = total.toString()
                } else {
                    showZeroCounts()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showZeroCounts()
            }
        }
    }

    private fun showZeroCounts() {
        binding.tvCriticalCount.text = "0"
        binding.tvUrgentCount.text = "0"
        binding.tvStableCount.text = "0"
    }

    // ---------------- NAVIGATION ----------------
    private fun setupNavigation() {
        binding.btnTreatCritical.setOnClickListener {
            startActivity(Intent(this, CriticalCasesActivity::class.java))
        }
        binding.btnTreatUrgent.setOnClickListener {
            startActivity(Intent(this, UrgentCasesActivity::class.java))
        }
        binding.btnTreatStable.setOnClickListener {
            startActivity(Intent(this, NonUrgentCasesActivity::class.java))
        }
        binding.cardCritical.setOnClickListener {
            startActivity(Intent(this, CriticalCasesActivity::class.java))
        }
        binding.cardUrgent.setOnClickListener {
            startActivity(Intent(this, UrgentCasesActivity::class.java))
        }
        binding.cardNonUrgent.setOnClickListener {
            startActivity(Intent(this, NonUrgentCasesActivity::class.java))
        }
        binding.navPatients.setOnClickListener {
            startActivity(Intent(this, DoctorPatientsActivity::class.java))
            finish()
        }
        binding.ivNotifications.setOnClickListener {
            startActivity(Intent(this, DoctorNotificationCenterActivity::class.java))
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