package com.simats.triageai

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.simats.triageai.databinding.ActivityDoctorUserProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DoctorUserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorUserProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorUserProfileBinding.inflate(layoutInflater)
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
            val intent = Intent(this, EditDoctorProfileActivity::class.java)
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
                    Toast.makeText(this@DoctorUserProfileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                Toast.makeText(this@DoctorUserProfileActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
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

        val photoPath = profile.profilePhoto ?: profile.photoUrl ?: profile.profilePhotoUrl ?:
                        profile.photo ?: profile.image ?: profile.profileImage ?: 
                        profile.avatar ?: profile.picture ?: profile.profilePicture ?:
                        profile.profilePhotoCamel ?: profile.photoUrlCamel
        if (!photoPath.isNullOrEmpty()) {
            // Build absolute URL: handle both relative paths (/uploads/...) and full URLs
            val photoUrl = when {
                photoPath.startsWith("http") -> photoPath
                photoPath.startsWith("/") -> ApiClient.BASE_URL.removeSuffix("/") + photoPath
                else -> ApiClient.BASE_URL.removeSuffix("/") + "/" + photoPath
            }
            Log.d("ProfilePhoto", "Loading doctor photo from: $photoUrl")

            Glide.with(this)
                .load(photoUrl)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .circleCrop()
                .into(binding.ivProfilePhoto)
        } else {
            Log.d("ProfilePhoto", "No profile photo URL returned from backend")
            binding.ivProfilePhoto.setImageResource(R.drawable.ic_person)
        }
    }

    private fun setupBottomNav() {
        binding.navDashboard.setOnClickListener {
            startActivity(Intent(this, DoctorDashboardActivity::class.java))
            finishAffinity()
        }

        binding.navPatients.setOnClickListener {
            startActivity(Intent(this, DoctorPatientsActivity::class.java))
            finishAffinity()
        }

        binding.navHistory.setOnClickListener {
            startActivity(Intent(this, DoctorHistoryActivity::class.java))
            finishAffinity()
        }

        binding.navSettings.setOnClickListener {
            startActivity(Intent(this, DoctorSettingsActivity::class.java))
            finishAffinity()
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserProfile() // Refresh profile when returning from Edit screen
    }
}
