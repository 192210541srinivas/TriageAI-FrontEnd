package com.simats.triageai

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.simats.triageai.databinding.ActivityRoleSelectionBinding

class RoleSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoleSelectionBinding

    private var selectedRole: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoleSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupSelectionLogic()

        binding.btnContinue.setOnClickListener {
            selectedRole?.let { role ->
                navigateToLogin(role)
            } ?: run {
                // Optional: Show toast or prompt to select role
            }
        }
    }

    private fun setupSelectionLogic() {
        binding.cardDoctor.setOnClickListener {
            selectRole("Doctor")
        }

        binding.cardParamedic.setOnClickListener {
            selectRole("Paramedic")
        }

        binding.cardAdmin.setOnClickListener {
            // For Admin, we can navigate directly or select it. 
            // The UI has a separate button for Admin.
            navigateToLogin("Admin")
        }
    }

    private fun selectRole(role: String) {
        selectedRole = role
        
        // Reset all styles
        binding.cardDoctor.strokeWidth = 2
        binding.cardDoctor.strokeColor = getColor(R.color.doctor_card_stroke)
        binding.cardParamedic.strokeWidth = 2
        binding.cardParamedic.strokeColor = getColor(R.color.paramedic_card_stroke)
        binding.cardAdmin.strokeWidth = 0 // Admin card is the small one at top
        
        // Apply selection style
        when (role) {
            "Doctor" -> {
                binding.cardDoctor.strokeWidth = 6
                binding.cardDoctor.strokeColor = getColor(R.color.doctor_icon_bg)
            }
            "Paramedic" -> {
                binding.cardParamedic.strokeWidth = 6
                binding.cardParamedic.strokeColor = getColor(R.color.paramedic_icon_bg)
            }
        }
        
        // Enable continue button
        binding.btnContinue.isEnabled = true
    }

    private fun navigateToLogin(role: String) {
        // store role in preferences immediately so UI can adapt even before server response
        getSharedPreferences("TriageAI", MODE_PRIVATE)
            .edit()
            .putString("role", role.lowercase())
            .apply()

        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("USER_ROLE", role)
        startActivity(intent)
    }
}
