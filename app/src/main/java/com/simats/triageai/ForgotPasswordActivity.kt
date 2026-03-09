package com.simats.triageai

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val btnBack = findViewById<LinearLayout>(R.id.btnBack)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val btnSendInstructions = findViewById<MaterialButton>(R.id.btnSendInstructions)

        btnBack.setOnClickListener {
            finish()
        }

        btnSendInstructions.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show loading or disable button
            btnSendInstructions.isEnabled = false
            btnSendInstructions.text = "Sending..."

            // Call Backend
            val request = ForgotPasswordEmailRequest(email)
            ApiClient.apiService.forgotPassword(request).enqueue(object : Callback<GenericResponse> {
                override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                    btnSendInstructions.isEnabled = true
                    btnSendInstructions.text = "Send Instructions"

                    if (response.isSuccessful && response.body()?.status == true) {
                        val intent = Intent(this@ForgotPasswordActivity, CheckEmailActivity::class.java)
                        intent.putExtra("EMAIL", email)
                        startActivity(intent)
                        finish()
                    } else {
                        val errorMessage = when (response.code()) {
                            404 -> "Email not registered"
                            else -> response.body()?.message ?: "Failed to send email"
                        }
                        Toast.makeText(this@ForgotPasswordActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    btnSendInstructions.isEnabled = true
                    btnSendInstructions.text = "Send Instructions"
                    Toast.makeText(this@ForgotPasswordActivity, "Connection failed: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}
