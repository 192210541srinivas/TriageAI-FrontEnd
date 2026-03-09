package com.simats.triageai

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.simats.triageai.databinding.ActivityParamedicTermsOfServiceBinding

class ParamedicTermsOfServiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParamedicTermsOfServiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParamedicTermsOfServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}
