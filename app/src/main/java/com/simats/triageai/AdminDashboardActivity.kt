package com.simats.triageai

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch

class AdminDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        supportActionBar?.hide()

        setupNavigation()

        findViewById<MaterialCardView>(R.id.btnAddStaff).setOnClickListener {
            startActivity(Intent(this, AddStaffActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.btnCriticalCasesAlert).setOnClickListener {
            // Updated to use the correct activity name if different
            startActivity(Intent(this, AdminCriticalExcessActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.btnViewStaff).setOnClickListener {
            startActivity(Intent(this, ViewStaffActivity::class.java))
        }

        findViewById<ImageView>(R.id.ivNotifications).setOnClickListener {
            // Placeholder for admin notifications
            Toast.makeText(this, "Opening Notifications...", Toast.LENGTH_SHORT).show()
        }

        loadStats()
    }

    private fun setupNavigation() {
        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.adminBottomNav)
        bottomNav.selectedItemId = R.id.nav_admin_dashboard

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_admin_dashboard -> true
                R.id.nav_admin_profile -> {
                    startActivity(Intent(this, AdminProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun loadStats() {
        val prefs = getSharedPreferences("TriageAI", MODE_PRIVATE)
        val adminId = prefs.getInt("user_id", -1)

        if (adminId == -1) return

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getAdminDashboardStats(adminId)
                if (response.isSuccessful) {
                    val stats = response.body()
                    stats?.let {
                        findViewById<TextView>(R.id.tvTotalDoctors).text = it.totalDoctors.toString()
                        findViewById<TextView>(R.id.tvAvailableDoctors).text = it.availableDoctors.toString()
                        findViewById<TextView>(R.id.tvConsultingDoctors).text = it.consultingDoctors.toString()
                        findViewById<TextView>(R.id.tvTotalParamedics).text = it.totalParamedics.toString()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadStats()
    }
}