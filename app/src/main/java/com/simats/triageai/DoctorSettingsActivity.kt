package com.simats.triageai

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.simats.triageai.databinding.ActivityDoctorSettingsBinding

class DoctorSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupUI()
        setupBottomNav()
    }

    private fun setupUI() {
        binding.layoutUserProfile.setOnClickListener {
            startActivity(Intent(this, DoctorUserProfileActivity::class.java))
        }

        binding.layoutChangePassword.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        binding.layoutPrivacy.setOnClickListener {
            startActivity(Intent(this, DoctorPrivacyActivity::class.java))
        }

        binding.layoutAiTransparency.setOnClickListener {
            startActivity(Intent(this, DoctorAiTransparencyActivity::class.java))
        }
        
        binding.layoutDisclaimer.setOnClickListener {
            startActivity(Intent(this, DoctorSystemDisclaimerActivity::class.java))
        }

        binding.layoutTerms.setOnClickListener {
            startActivity(Intent(this, DoctorTermsOfServiceActivity::class.java))
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
            startActivity(Intent(this, DoctorDashboardActivity::class.java))
            finish()
        }

        binding.navPatients.setOnClickListener {
            startActivity(Intent(this, DoctorPatientsActivity::class.java))
            finish()
        }

        binding.navHistory.setOnClickListener {
            startActivity(Intent(this, DoctorHistoryNewActivity::class.java))
            finish()
        }

        // navSettings is already the current screen
    }
}
