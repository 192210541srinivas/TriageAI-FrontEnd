package com.simats.triageai

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.net.Uri
import android.graphics.Bitmap
import androidx.activity.result.contract.ActivityResultContracts
import android.app.AlertDialog
import java.io.File
import java.io.FileOutputStream
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import com.simats.triageai.PhotoUploadResponse

class AddStaffActivity : AppCompatActivity() {

    private var selectedRole: String = "doctor"
    private lateinit var etFullName: EditText
    private lateinit var layoutUploadPhoto: LinearLayout
    private lateinit var ivPhotoPreview: ImageView
    private var selectedPhotoUri: Uri? = null

    private val takePicturePreview = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        bitmap?.let {
            val file = File(cacheDir, "camera_image_${System.currentTimeMillis()}.jpg")
            val os = FileOutputStream(file)
            it.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.flush()
            os.close()
            selectedPhotoUri = Uri.fromFile(file)
            com.bumptech.glide.Glide.with(this).load(it).into(ivPhotoPreview)
        }
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedPhotoUri = it
            com.bumptech.glide.Glide.with(this).load(it).into(ivPhotoPreview)
        }
    }
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etAddress: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var etLicenseNumber: EditText
    private lateinit var etDepartment: EditText
    private lateinit var tvDepartmentLabel: TextView
    private lateinit var layoutDepartment: View
    private lateinit var btnCreateAccount: Button
    
    private lateinit var cvDoctor: MaterialCardView
    private lateinit var cvParamedic: MaterialCardView
    private lateinit var ivDoctorIcon: ImageView
    private lateinit var tvDoctor: TextView
    private lateinit var ivParamedicIcon: ImageView
    private lateinit var tvParamedic: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_staff)

        // Initialize views
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etAddress = findViewById(R.id.etAddress)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        etLicenseNumber = findViewById(R.id.etLicenseNumber)
        etDepartment = findViewById(R.id.etDepartment)
        tvDepartmentLabel = findViewById(R.id.tvDepartmentLabel)
        btnCreateAccount = findViewById(R.id.btnCreateAccount)
        layoutDepartment = findViewById(R.id.layoutDepartment)
        layoutUploadPhoto = findViewById(R.id.layoutUploadPhoto)
        ivPhotoPreview = findViewById(R.id.ivPhotoPreview)
        
        cvDoctor = findViewById(R.id.cvDoctor)
        cvParamedic = findViewById(R.id.cvParamedic)
        ivDoctorIcon = findViewById(R.id.ivDoctorIcon)
        tvDoctor = findViewById(R.id.tvDoctor)
        ivParamedicIcon = findViewById(R.id.ivParamedicIcon)
        tvParamedic = findViewById(R.id.tvParamedic)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }

        cvDoctor.setOnClickListener {
            selectRole("doctor")
        }

        cvParamedic.setOnClickListener {
            selectRole("paramedic")
        }

        btnCreateAccount.setOnClickListener {
            createStaffAccount()
        }
        
        layoutUploadPhoto.setOnClickListener {
            val options = arrayOf("Take Photo", "Choose from Gallery")
            AlertDialog.Builder(this)
                .setTitle("Select Profile Photo")
                .setItems(options) { _, which ->
                    if (which == 0) takePicturePreview.launch(null) else pickImage.launch("image/*")
                }
                .show()
        }

        // Default selection
        selectRole("doctor")
    }

    private fun selectRole(role: String) {
        selectedRole = role
        if (role == "doctor") {
            // Highlight Doctor
            cvDoctor.setCardBackgroundColor(ContextCompat.getColor(this, R.color.doctor_card_bg))
            cvDoctor.strokeColor = ContextCompat.getColor(this, R.color.doctor_icon_bg)
            cvDoctor.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, resources.displayMetrics).toInt()
            ivDoctorIcon.clearColorFilter()
            ivDoctorIcon.alpha = 1.0f
            tvDoctor.setTextColor(ContextCompat.getColor(this, R.color.doctor_icon_bg))

            // Unhighlight Paramedic
            cvParamedic.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
            cvParamedic.strokeColor = ContextCompat.getColor(this, R.color.paramedic_card_stroke)
            cvParamedic.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics).toInt()
            ivParamedicIcon.clearColorFilter()
            ivParamedicIcon.alpha = 0.5f
            tvParamedic.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
            
            // Show Department
            tvDepartmentLabel.visibility = View.VISIBLE
            layoutDepartment.visibility = View.VISIBLE
        } else {
            // Highlight Paramedic
            cvParamedic.setCardBackgroundColor(ContextCompat.getColor(this, R.color.paramedic_card_bg))
            cvParamedic.strokeColor = ContextCompat.getColor(this, R.color.paramedic_icon_bg)
            cvParamedic.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, resources.displayMetrics).toInt()
            ivParamedicIcon.clearColorFilter()
            ivParamedicIcon.alpha = 1.0f
            tvParamedic.setTextColor(ContextCompat.getColor(this, R.color.paramedic_icon_bg))

            // Unhighlight Doctor
            cvDoctor.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
            cvDoctor.strokeColor = ContextCompat.getColor(this, R.color.doctor_card_stroke)
            cvDoctor.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics).toInt()
            ivDoctorIcon.clearColorFilter()
            ivDoctorIcon.alpha = 0.5f
            tvDoctor.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
            
            // Hide Department (as it's only mandatory for doctor, maybe hide or just make optional? 
            // The prompt says mandatory if doctor, implying we can hide it or make it non-mandatory for paramedic.
            // Let's keep it visible but not validate it for paramedic for better UX or hide it if it's strictly for doctors)
            tvDepartmentLabel.visibility = View.GONE
            layoutDepartment.visibility = View.GONE
        }
    }

    private fun createStaffAccount() {
        val name = etFullName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val address = etAddress.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()
        val licenseNumber = etLicenseNumber.text.toString().trim()
        val department = etDepartment.text.toString().trim()

        if (name.isEmpty() || !com.simats.triageai.utils.ValidationUtils.isValidName(name)) {
            etFullName.error = "Full Name must contain only alphabets"
            return
        }
        
        if (email.isEmpty() || !com.simats.triageai.utils.ValidationUtils.isValidEmail(email)) {
            etEmail.error = "Please enter a valid email address"
            return
        }

        if (phone.isEmpty() || !com.simats.triageai.utils.ValidationUtils.isValidPhone(phone)) {
            etPhone.error = "Please enter a valid 10-digit phone number"
            return
        }

        if (password.isEmpty()) {
            etPassword.error = "Password is required"
            return
        }
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.error = "Please confirm your password"
            return
        }
        if (licenseNumber.isEmpty()) {
            etLicenseNumber.error = "License number is required"
            return
        }

        if (password != confirmPassword) {
            etConfirmPassword.error = "Passwords do not match"
            return
        }

        // Validate password strength
        val validationResult = com.simats.triageai.utils.ValidationUtils.validatePassword(password)
        if (validationResult is com.simats.triageai.utils.ValidationResult.Invalid) {
            etPassword.error = validationResult.message
            return
        }
        
        if (selectedRole == "doctor" && department.isEmpty()) {
            etDepartment.error = "Department is required for doctors"
            return
        }

        val adminId = getSharedPreferences("TriageAI", MODE_PRIVATE).getInt("user_id", -1)
        if (adminId == -1) {
            Toast.makeText(this, "Admin session expired. Please login again.", Toast.LENGTH_SHORT).show()
            return
        }

        val registerRequest = RegisterRequest(
            name = name,
            email = email,
            password = password,
            role = selectedRole,
            phone = phone,
            license_number = licenseNumber,
            department = if (selectedRole == "doctor") department else null
        )

        btnCreateAccount.isEnabled = false
        btnCreateAccount.text = "Creating Account..."

        ApiClient.apiService.adminAddUser(adminId, registerRequest)
            .enqueue(object : Callback<GenericResponse> {
                override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                    btnCreateAccount.isEnabled = true
                    btnCreateAccount.text = "Create Staff Account"

                    if (response.isSuccessful) {
                        val newUserId = response.body()?.user_id
                        if (newUserId != null && selectedPhotoUri != null) {
                            uploadProfilePhoto(newUserId, selectedPhotoUri!!)
                        } else {
                            Toast.makeText(this@AddStaffActivity, response.body()?.message ?: "Staff account created", Toast.LENGTH_LONG).show()
                            finish()
                        }
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: "Failed to create account"
                        Toast.makeText(this@AddStaffActivity, errorMsg, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    btnCreateAccount.isEnabled = true
                    btnCreateAccount.text = "Create Staff Account"
                    Toast.makeText(this@AddStaffActivity, "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun uploadProfilePhoto(userId: Int, fileUri: Uri) {
        btnCreateAccount.text = "Uploading Photo..."
        try {
            val inputStream = contentResolver.openInputStream(fileUri)
            val file = File(cacheDir, "upload_image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            ApiClient.apiService.uploadPhoto(userId, body).enqueue(object : Callback<PhotoUploadResponse> {
                override fun onResponse(call: Call<PhotoUploadResponse>, response: Response<PhotoUploadResponse>) {
                    Toast.makeText(this@AddStaffActivity, "Staff account & photo created successfully", Toast.LENGTH_LONG).show()
                    finish()
                }

                override fun onFailure(call: Call<PhotoUploadResponse>, t: Throwable) {
                    Toast.makeText(this@AddStaffActivity, "Account created, but photo upload failed", Toast.LENGTH_LONG).show()
                    finish()
                }
            })
        } catch (e: Exception) {
            Toast.makeText(this@AddStaffActivity, "Staff account created. Error preparing photo", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
