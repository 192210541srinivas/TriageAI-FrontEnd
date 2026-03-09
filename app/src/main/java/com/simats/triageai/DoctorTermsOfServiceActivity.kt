package com.simats.triageai

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.simats.triageai.databinding.ActivityDoctorTermsOfServiceBinding

class DoctorTermsOfServiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorTermsOfServiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorTermsOfServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}
