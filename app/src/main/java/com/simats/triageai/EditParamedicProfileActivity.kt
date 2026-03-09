package com.simats.triageai

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.simats.triageai.databinding.ActivityEditParamedicProfileBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class EditParamedicProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditParamedicProfileBinding
    private var userId: Int = -1

    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            imageUri?.let { uploadPhoto(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditParamedicProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val prefs = getSharedPreferences("TriageAI", MODE_PRIVATE)
        userId = prefs.getInt("user_id", -1)

        setupUI()
        loadCurrentProfile()
    }

    private fun setupUI() {
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.ivProfilePhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImage.launch(intent)
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnSaveChanges.setOnClickListener {
            updateProfile()
        }
    }

    private fun loadCurrentProfile() {
        if (userId == -1) return

        ApiClient.apiService.getUserProfile(userId).enqueue(object : Callback<UserProfileResponse> {
            override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { profile ->
                        binding.etFullName.setText(profile.name)
                        binding.etEmail.setText(profile.email)
                        binding.etPhone.setText(profile.phone)
                        
                        profile.profilePhoto?.let { photoPath ->
                            val fullUrl = if (photoPath.startsWith("http")) {
                                photoPath
                            } else {
                                ApiClient.BASE_URL.trimEnd('/') + photoPath
                            }
                            
                            Glide.with(this@EditParamedicProfileActivity)
                                .load(fullUrl)
                                .placeholder(R.drawable.ic_patients)
                                .error(R.drawable.ic_patients)
                                .circleCrop()
                                .into(binding.ivProfilePhoto)
                            
                            binding.ivProfilePhoto.setPadding(0, 0, 0, 0)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                Toast.makeText(this@EditParamedicProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateProfile() {
        val name = binding.etFullName.text.toString()
        val email = binding.etEmail.text.toString()
        val phone = binding.etPhone.text.toString()
        val address = binding.etAddress.text.toString()

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val request = UpdateProfileRequest(name, email, phone, address)

        ApiClient.apiService.updateProfile(userId, request).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EditParamedicProfileActivity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditParamedicProfileActivity, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                Toast.makeText(this@EditParamedicProfileActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun uploadPhoto(imageUri: Uri) {
        val file = uriToFile(imageUri)
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        ApiClient.apiService.uploadPhoto(userId, body).enqueue(object : Callback<PhotoUploadResponse> {
            override fun onResponse(call: Call<PhotoUploadResponse>, response: Response<PhotoUploadResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EditParamedicProfileActivity, "Photo uploaded successfully", Toast.LENGTH_SHORT).show()
                    
                    Glide.with(this@EditParamedicProfileActivity)
                        .load(imageUri)
                        .circleCrop()
                        .into(binding.ivProfilePhoto)
                    
                    binding.ivProfilePhoto.setPadding(0, 0, 0, 0)
                } else {
                    Toast.makeText(this@EditParamedicProfileActivity, "Photo upload failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PhotoUploadResponse>, t: Throwable) {
                Toast.makeText(this@EditParamedicProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
        val file = File(cacheDir, "temp_profile_image.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return file
    }
}
