package com.simats.triageai

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.simats.triageai.databinding.ActivityTriageStep1Binding

class TriageStep1Activity : AppCompatActivity() {

    private lateinit var binding: ActivityTriageStep1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTriageStep1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupGenderSpinner()
        setupValidation()
        setupClickListeners()
    }

    private fun setupGenderSpinner() {
        val genders = arrayOf("Male", "Female", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genders)
        binding.spinnerGender.setAdapter(adapter)
        
        binding.spinnerGender.setOnItemClickListener { _, _, _, _ ->
            validateForm()
        }
    }

    private fun setupValidation() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateForm()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.etName.addTextChangedListener(textWatcher)
        binding.etEmail.addTextChangedListener(textWatcher)
        binding.etAge.addTextChangedListener(textWatcher)
        binding.etPhone.addTextChangedListener(textWatcher)
    }

    private fun validateForm() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val age = binding.etAge.text.toString().trim()
        val gender = binding.spinnerGender.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        
        val isValid = name.isNotEmpty() && email.isNotEmpty() && age.isNotEmpty() && 
                      gender.isNotEmpty() && phone.isNotEmpty()
        binding.btnContinue.isEnabled = isValid
    }

    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener {
            finish()
        }

        binding.btnContinue.setOnClickListener {
            val intent = Intent(this, TriageStep2Activity::class.java).apply {
                putExtra("NAME", binding.etName.text.toString().trim())
                putExtra("EMAIL", binding.etEmail.text.toString().trim())
                putExtra("AGE", binding.etAge.text.toString().trim().toIntOrNull() ?: 0)
                putExtra("GENDER", binding.spinnerGender.text.toString().trim())
                putExtra("PHONE", binding.etPhone.text.toString().trim())
                putExtra("ADDRESS", binding.etAddress.text.toString().trim())
            }
            startActivity(intent)
        }
    }
}
