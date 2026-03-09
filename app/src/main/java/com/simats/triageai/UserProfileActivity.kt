package com.simats.triageai

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.simats.triageai.databinding.ActivityUserProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupUI()
        setupBottomNav()
        loadUserProfile()
    }

    private fun setupUI() {
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        
        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(this, EditParamedicProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadUserProfile() {
        val prefs = getSharedPreferences("TriageAI", MODE_PRIVATE)
        val userId = prefs.getInt("user_id", -1)

        if (userId == -1) {
            Toast.makeText(this, "User ID not found. Please login again.", Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.apiService.getUserProfile(userId).enqueue(object : Callback<UserProfileResponse> {
            override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { profile ->
                        updateUI(profile)
                    }
                } else {
                    Toast.makeText(this@UserProfileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                Toast.makeText(this@UserProfileActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUI(profile: UserProfileResponse) {
        binding.tvFullName.text = profile.name
        binding.tvRole.text = profile.role.replaceFirstChar { it.uppercase() }
        binding.tvEmail.text = profile.email
        binding.tvPhone.text = profile.phone ?: "Not provided"
        binding.tvLicenseNumber.text = profile.licenseNumber ?: "N/A"
        binding.tvDepartment.text = profile.department ?: "N/A"
        binding.tvJoinedDate.text = profile.joinedDate ?: "N/A"

        if (!profile.profilePhoto.isNullOrEmpty()) {
            val photoUrl = if (profile.profilePhoto.startsWith("http")) {
                profile.profilePhoto
            } else {
                ApiClient.BASE_URL.removeSuffix("/") + profile.profilePhoto
            }
            
            Glide.with(this)
                .load(photoUrl)
                .placeholder(R.drawable.bg_nav_item_selected)
                .error(R.drawable.bg_nav_item_selected)
                .circleCrop()
                .into(binding.ivProfilePhoto)
        }
    }

    private fun setupBottomNav() {
        binding.navDashboard.setOnClickListener {
            val intent = Intent(this, ParamedicDashboardActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

        binding.navPatients.setOnClickListener {
            val intent = Intent(this, PatientsActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

        binding.navSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserProfile()
    }
}
