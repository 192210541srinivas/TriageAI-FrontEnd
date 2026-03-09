package com.simats.triageai

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.simats.triageai.UserProfileResponse
import com.simats.triageai.PhotoUploadResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.bumptech.glide.Glide
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class AdminProfileActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private var adminId: Int = -1

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { uploadProfilePhoto(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_profile)

        supportActionBar?.hide()

        profileImageView = findViewById(R.id.ivAdminProfilePhoto)
        adminId = getSharedPreferences("TriageAI", MODE_PRIVATE).getInt("user_id", -1)
        
        if (adminId != -1) {
            loadProfile(adminId)
        }
        
        setupNavigation()

        findViewById<com.google.android.material.card.MaterialCardView>(R.id.btnUploadPhoto).setOnClickListener {
            pickImage.launch("image/*")
        }

        findViewById<ImageView>(R.id.btnEditProfile).setOnClickListener {
            val intent = Intent(this, EditAdminProfileActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnSignOut).setOnClickListener {
            getSharedPreferences("TriageAI", MODE_PRIVATE).edit().clear().apply()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.adminBottomNav)
        bottomNav.selectedItemId = R.id.nav_admin_profile

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_admin_dashboard -> {
                    startActivity(Intent(this, AdminDashboardActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_admin_profile -> true
                else -> false
            }
        }
    }

    private fun loadProfile(userId: Int) {
        ApiClient.apiService.getUserProfile(userId).enqueue(object : Callback<UserProfileResponse> {
            override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { updateUI(it) }
                }
            }
            override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                Toast.makeText(this@AdminProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUI(profile: UserProfileResponse) {
        findViewById<TextView>(R.id.tvAdminNameLarge).text = profile.name
        findViewById<TextView>(R.id.tvDetailName).text = profile.name
        findViewById<TextView>(R.id.tvDetailEmail).text = profile.email
        findViewById<TextView>(R.id.tvDetailPhone).text = profile.phone ?: "Not provided"
        findViewById<TextView>(R.id.tvDetailOrg).text = profile.department ?: "Not provided"
        findViewById<TextView>(R.id.tvDetailMemberSince).text = profile.joinedDate ?: "Not available"
        
        if (!profile.profilePhoto.isNullOrEmpty()) {
            val fullUrl = if (profile.profilePhoto.startsWith("http")) profile.profilePhoto else ApiClient.BASE_URL + profile.profilePhoto.removePrefix("/")
            Glide.with(this).load(fullUrl).placeholder(R.drawable.img_31).into(profileImageView)
            profileImageView.imageTintList = null
        }
    }

    private fun uploadProfilePhoto(uri: android.net.Uri) {
        val file = uriToFile(uri) ?: return
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        ApiClient.apiService.uploadPhoto(adminId, body).enqueue(object : Callback<PhotoUploadResponse> {
            override fun onResponse(call: Call<PhotoUploadResponse>, response: Response<PhotoUploadResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AdminProfileActivity, "Photo updated", Toast.LENGTH_SHORT).show()
                    loadProfile(adminId)
                }
            }
            override fun onFailure(call: Call<PhotoUploadResponse>, t: Throwable) {}
        })
    }

    private fun uriToFile(uri: android.net.Uri): File? {
        val inputStream = contentResolver.openInputStream(uri) ?: return null
        val file = File(cacheDir, "temp_profile.jpg")
        FileOutputStream(file).use { inputStream.copyTo(it) }
        return file
    }
}