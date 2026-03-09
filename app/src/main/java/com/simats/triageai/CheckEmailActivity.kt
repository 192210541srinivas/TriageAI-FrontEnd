package com.simats.triageai

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class CheckEmailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_email)

        val email = intent.getStringExtra("EMAIL") ?: "your email"
        findViewById<TextView>(R.id.tvEmail).text = email

        findViewById<MaterialButton>(R.id.btnBackToLogin).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        findViewById<TextView>(R.id.btnResend).setOnClickListener {
            Toast.makeText(this, "Reset email resent to $email", Toast.LENGTH_SHORT).show()
        }
    }
}
