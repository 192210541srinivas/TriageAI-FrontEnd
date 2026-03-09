package com.simats.triageai

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.simats.triageai.databinding.ActivityDoctorSystemDisclaimerBinding

class DoctorSystemDisclaimerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorSystemDisclaimerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorSystemDisclaimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}
