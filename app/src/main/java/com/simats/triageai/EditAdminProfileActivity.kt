package com.simats.triageai

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.simats.triageai.models.*
import com.simats.triageai.UserProfileResponse
import com.simats.triageai.GenericResponse
import com.simats.triageai.UpdateProfileRequest
import com.simats.triageai.ChangePasswordRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditAdminProfileActivity : AppCompatActivity() {

    private lateinit var etName: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etAddress: TextInputEditText
    private lateinit var etOldPassword: TextInputEditText
    private lateinit var etNewPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private var adminId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_admin_profile)

        adminId = getSharedPreferences("TriageAI", MODE_PRIVATE).getInt("user_id", -1)

        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etAddress = findViewById(R.id.etAddress)
        etOldPassword = findViewById(R.id.etOldPassword)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        loadExistingProfile()

        findViewById<Button>(R.id.btnSaveChanges).setOnClickListener {
            handleSave()
        }
    }

    private fun loadExistingProfile() {
        if (adminId == -1) return

        ApiClient.apiService.getUserProfile(adminId).enqueue(object : Callback<UserProfileResponse> {
            override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
                if (response.isSuccessful) {
                    val profile = response.body()
                    profile?.let {
                        etName.setText(it.name)
                        etPhone.setText(it.phone)
                        etAddress.setText(it.address)
                    }
                }
            }

            override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                Toast.makeText(this@EditAdminProfileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun handleSave() {
        val name = etName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val address = etAddress.text.toString().trim()
        val oldPass = etOldPassword.text.toString().trim()
        val newPass = etNewPassword.text.toString().trim()
        val confirmPass = etConfirmPassword.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Update Profile Info
        val updateRequest = UpdateProfileRequest(name = name, phone = phone, address = address)
        ApiClient.apiService.updateProfile(adminId, updateRequest).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EditAdminProfileActivity, "Profile updated", Toast.LENGTH_SHORT).show()
                    
                    // 2. Handle Password Change if relevant fields are filled
                    if (oldPass.isNotEmpty() || newPass.isNotEmpty() || confirmPass.isNotEmpty()) {
                        changePassword(oldPass, newPass, confirmPass)
                    } else {
                        finish()
                    }
                } else {
                    Toast.makeText(this@EditAdminProfileActivity, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                Toast.makeText(this@EditAdminProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun changePassword(old: String, new: String, confirm: String) {
        if (old.isEmpty() || new.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Please fill all password fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (new != confirm) {
            Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        val passwordRequest = ChangePasswordRequest(oldPassword = old, newPassword = new, confirmPassword = confirm)
        ApiClient.apiService.changePassword(adminId, passwordRequest).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EditAdminProfileActivity, "Password changed successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditAdminProfileActivity, "Password change failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                Toast.makeText(this@EditAdminProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
