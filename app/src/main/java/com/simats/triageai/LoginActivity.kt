package com.simats.triageai

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSignIn: Button
    private lateinit var loadingIndicator: ProgressBar
    private var expectedRole: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnSignIn = findViewById(R.id.btnSignIn)
        loadingIndicator = findViewById(R.id.loadingIndicator)

        expectedRole = intent.getStringExtra("USER_ROLE")?.lowercase()
        
        expectedRole?.let { role ->
            val indicator = findViewById<MaterialCardView>(R.id.roleIndicator)
            val icon = findViewById<ImageView>(R.id.roleIcon)
            val text = findViewById<TextView>(R.id.roleText)
            
            when (role) {
                "doctor" -> {
                    indicator.setCardBackgroundColor(ContextCompat.getColor(this, R.color.doctor_card_bg))
                    icon.setImageResource(R.drawable.ic_doctor)
                    icon.setColorFilter(ContextCompat.getColor(this, R.color.doctor_icon_bg))
                    text.text = "Doctor Login"
                    text.setTextColor(ContextCompat.getColor(this, R.color.doctor_icon_bg))
                }
                "paramedic" -> {
                    indicator.setCardBackgroundColor(ContextCompat.getColor(this, R.color.paramedic_card_bg))
                    icon.setImageResource(R.drawable.ic_heart_red)
                    icon.setColorFilter(ContextCompat.getColor(this, R.color.paramedic_icon_bg))
                    text.text = "Paramedic Login"
                    text.setTextColor(ContextCompat.getColor(this, R.color.paramedic_icon_bg))
                }
                "admin" -> {
                    indicator.setCardBackgroundColor(ContextCompat.getColor(this, R.color.admin_card_bg))
                    icon.setImageResource(R.drawable.ic_person)
                    icon.setColorFilter(ContextCompat.getColor(this, R.color.admin_icon_bg))
                    text.text = "Admin Login"
                    text.setTextColor(ContextCompat.getColor(this, R.color.admin_icon_bg))
                }
            }
        }

        btnSignIn.setOnClickListener {
            loginUser()
        }

        findViewById<TextView>(R.id.btnForgotPassword).setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    private fun loginUser() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        loadingIndicator.visibility = ProgressBar.VISIBLE
        btnSignIn.isEnabled = false

        val request = LoginRequest(email, password)

        ApiClient.apiService.login(request)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    loadingIndicator.visibility = ProgressBar.GONE
                    btnSignIn.isEnabled = true

                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null && body.status) {
                            
                            val receivedRole = (body.user?.role ?: "").lowercase().trim()
                            
                            // ROLE VALIDATION: Check if received role matches the expected role from selection screen
                            if (expectedRole != null && receivedRole != expectedRole) {
                                val message = "These credentials belong to a ${receivedRole}. Please use ${expectedRole} credentials."
                                Toast.makeText(this@LoginActivity, message, Toast.LENGTH_LONG).show()
                                return
                            }

                            getSharedPreferences("TriageAI", MODE_PRIVATE)
                                .edit()
                                .putString("role", receivedRole)
                                .putInt("user_id", body.user?.id ?: -1)
                                .putString("status", body.user?.status ?: "ACTIVE")
                                .apply()

                            when (receivedRole) {
                                "doctor" -> {
                                    Log.d("LoginActivity", "Redirecting to Doctor Dashboard")
                                    startActivity(Intent(this@LoginActivity, DoctorDashboardActivity::class.java))
                                }
                                "paramedic" -> {
                                    Log.d("LoginActivity", "Redirecting to Paramedic Dashboard")
                                    startActivity(Intent(this@LoginActivity, ParamedicDashboardActivity::class.java))
                                }
                                "admin" -> {
                                    Log.d("LoginActivity", "Redirecting to Admin Dashboard")
                                    startActivity(Intent(this@LoginActivity, AdminDashboardActivity::class.java))
                                }
                            }
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, body?.message ?: "Login failed", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Server error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    loadingIndicator.visibility = ProgressBar.GONE
                    btnSignIn.isEnabled = true
                    Toast.makeText(this@LoginActivity, "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }
}
