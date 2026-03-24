package com.simats.triageai

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.simats.triageai.databinding.ActivityEditStaffBinding
import kotlinx.coroutines.launch

class EditStaffActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditStaffBinding
    private var userId: Int = -1
    private var adminId: Int = -1
 
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditStaffBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val prefs = getSharedPreferences("TriageAI", MODE_PRIVATE)
        adminId = prefs.getInt("user_id", -1)
 
        userId = intent.getIntExtra("USER_ID", -1)
        val name = intent.getStringExtra("NAME") ?: ""
        val phone = intent.getStringExtra("PHONE") ?: ""
        val dept = intent.getStringExtra("DEPT") ?: ""

        binding.etName.setText(name)
        binding.etPhone.setText(phone)
        binding.etDepartment.setText(dept)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSave.setOnClickListener {
            saveChanges()
        }
    }

    private fun saveChanges() {
        val name = binding.etName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val dept = binding.etDepartment.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show()
            return
        }
        if (!name.matches("^[a-zA-Z\\s]+$".toRegex())) {
            Toast.makeText(this, "Name must contain only alphabets", Toast.LENGTH_SHORT).show()
            return
        }

        val request = UpdateStaffRequest(name, phone, dept, address)

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.updateStaff(adminId, userId, request)
                if (response.isSuccessful) {
                    Toast.makeText(this@EditStaffActivity, "Changes saved successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditStaffActivity, "Failed to save changes", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditStaffActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
