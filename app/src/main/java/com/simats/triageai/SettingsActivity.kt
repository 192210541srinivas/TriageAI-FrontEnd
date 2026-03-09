package com.simats.triageai

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.simats.triageai.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupUI()
        setupBottomNav()
    }

    private fun setupUI() {
        binding.layoutUserProfile.setOnClickListener {
            startActivity(Intent(this, UserProfileActivity::class.java))
        }

        binding.layoutChangePassword.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        binding.layoutPrivacy.setOnClickListener {
            startActivity(Intent(this, ParamedicPrivacyActivity::class.java))
        }

        binding.layoutAiTransparency.setOnClickListener {
            startActivity(Intent(this, DoctorAiTransparencyActivity::class.java))
        }
        
        binding.layoutDisclaimer.setOnClickListener {
            startActivity(Intent(this, ParamedicSystemDisclaimerActivity::class.java))
        }

        binding.layoutTerms.setOnClickListener {
            startActivity(Intent(this, ParamedicTermsOfServiceActivity::class.java))
        }

        binding.layoutAppInfo.setOnClickListener {
            startActivity(Intent(this, DoctorAppInfoActivity::class.java))
        }

        binding.btnSignOut.setOnClickListener {
            val intent = Intent(this, RoleSelectionActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupBottomNav() {
        binding.navDashboard.setOnClickListener {
            val intent = Intent(this, ParamedicDashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.navPatients.setOnClickListener {
            val intent = Intent(this, PatientsActivity::class.java)
            startActivity(intent)
            finish()
        }

        // navSettings is already the current screen
    }
}
