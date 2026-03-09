package com.simats.triageai

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class DoctorPrivacyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_privacy)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener {
            onBackPressed()
        }
    }
}
