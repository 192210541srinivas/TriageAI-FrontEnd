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

class AddStaffActivity : AppCompatActivity() {

    private var selectedRole: String = "doctor"
    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etAddress: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var etLicenseNumber: EditText
    private lateinit var etDepartment: EditText
    private lateinit var tvDepartmentLabel: TextView
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
            ivDoctorIcon.setColorFilter(ContextCompat.getColor(this, R.color.doctor_icon_bg))
            tvDoctor.setTextColor(ContextCompat.getColor(this, R.color.doctor_icon_bg))

            // Unhighlight Paramedic
            cvParamedic.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
            cvParamedic.strokeColor = ContextCompat.getColor(this, R.color.paramedic_card_stroke)
            cvParamedic.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics).toInt()
            ivParamedicIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_secondary))
            tvParamedic.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
            
            // Show Department
            tvDepartmentLabel.visibility = View.VISIBLE
            etDepartment.visibility = View.VISIBLE
        } else {
            // Highlight Paramedic
            cvParamedic.setCardBackgroundColor(ContextCompat.getColor(this, R.color.paramedic_card_bg))
            cvParamedic.strokeColor = ContextCompat.getColor(this, R.color.paramedic_icon_bg)
            cvParamedic.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, resources.displayMetrics).toInt()
            ivParamedicIcon.setColorFilter(ContextCompat.getColor(this, R.color.paramedic_icon_bg))
            tvParamedic.setTextColor(ContextCompat.getColor(this, R.color.paramedic_icon_bg))

            // Unhighlight Doctor
            cvDoctor.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
            cvDoctor.strokeColor = ContextCompat.getColor(this, R.color.doctor_card_stroke)
            cvDoctor.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics).toInt()
            ivDoctorIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_secondary))
            tvDoctor.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
            
            // Hide Department (as it's only mandatory for doctor, maybe hide or just make optional? 
            // The prompt says mandatory if doctor, implying we can hide it or make it non-mandatory for paramedic.
            // Let's keep it visible but not validate it for paramedic for better UX or hide it if it's strictly for doctors)
            tvDepartmentLabel.visibility = View.GONE
            etDepartment.visibility = View.GONE
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

        if (name.isEmpty()) {
            etFullName.error = "Full Name is required"
            return
        }
        if (email.isEmpty()) {
            etEmail.error = "Email is required"
            return
        }
        if (phone.isEmpty()) {
            etPhone.error = "Phone number is required"
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

        if (password.length < 8) {
            etPassword.error = "Password must be at least 8 characters"
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
                        Toast.makeText(this@AddStaffActivity, response.body()?.message ?: "Staff account created", Toast.LENGTH_LONG).show()
                        finish()
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
}
