package com.simats.triageai

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.simats.triageai.databinding.ActivityParamedicSystemDisclaimerBinding

class ParamedicSystemDisclaimerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParamedicSystemDisclaimerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParamedicSystemDisclaimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.navDashboard.setOnClickListener {
            startActivity(Intent(this, ParamedicDashboardActivity::class.java))
            finish()
        }

        binding.navPatients.setOnClickListener {
            startActivity(Intent(this, PatientsActivity::class.java))
            finish()
        }
        
        // navSettings is already the current area context
    }
}
