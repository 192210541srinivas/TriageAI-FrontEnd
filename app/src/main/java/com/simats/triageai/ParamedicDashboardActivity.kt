package com.simats.triageai

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.simats.triageai.databinding.ActivityParamedicDashboardBinding
import com.simats.triageai.models.ParamedicWaitingCountResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ParamedicDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParamedicDashboardBinding
    private var paramedicId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParamedicDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val prefs = getSharedPreferences("TriageAI", MODE_PRIVATE)
        paramedicId = prefs.getInt("user_id", -1)

        setupDashboard()
        loadWaitingPatientCounts()
    }

    override fun onResume() {
        super.onResume()
        loadWaitingPatientCounts()
    }

    private fun setupDashboard() {
        binding.cardNewTriage.setOnClickListener {
            startNewTriage()
        }

        binding.fabAdd.setOnClickListener {
            startNewTriage()
        }

        binding.navPatients.setOnClickListener {
            startActivity(Intent(this, PatientsActivity::class.java))
            finish()
        }

        binding.navSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            finish()
        }

        binding.ivNotifications.setOnClickListener {
            startActivity(Intent(this, ParamedicNotificationCenterActivity::class.java))
        }
    }

    private fun loadWaitingPatientCounts() {
        if (paramedicId == -1) return

        ApiClient.apiService.getParamedicWaitingCount(paramedicId).enqueue(object : Callback<ParamedicWaitingCountResponse> {
            override fun onResponse(
                call: Call<ParamedicWaitingCountResponse>,
                response: Response<ParamedicWaitingCountResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    val counts = data.waitingCounts
                    
                    binding.tvCriticalCount.text = counts.critical.toString()
                    binding.tvUrgentCount.text = counts.urgent.toString()
                    binding.tvStableCount.text = counts.nonUrgent.toString()
                    
                    // Update "Patients Triaged" count (from Today's Activity section)
                    findViewById<TextView>(R.id.tvTriageCountValue)?.text = data.totalWaiting.toString()
                }
            }

            override fun onFailure(call: Call<ParamedicWaitingCountResponse>, t: Throwable) {
                // Keep showing defaults on failure
            }
        })
    }

    private fun startNewTriage() {
        startActivity(Intent(this, TriageStep1Activity::class.java))
    }
}
