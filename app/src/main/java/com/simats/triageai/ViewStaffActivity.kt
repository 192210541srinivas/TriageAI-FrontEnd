package com.simats.triageai

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.simats.triageai.adapters.StaffAdapter
import com.simats.triageai.databinding.ActivityViewStaffBinding
import kotlinx.coroutines.launch

class ViewStaffActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewStaffBinding
    private lateinit var staffAdapter: StaffAdapter
    private var adminId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewStaffBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val prefs = getSharedPreferences("TriageAI", MODE_PRIVATE)
        adminId = prefs.getInt("user_id", -1)

        setupRecyclerView()
        setupClickListeners()
        loadStaff()
    }

    private fun setupRecyclerView() {
        staffAdapter = StaffAdapter(
            staffList = emptyList(),
            onEditClick = { staff ->
                val intent = Intent(this, EditStaffActivity::class.java).apply {
                    putExtra("USER_ID", staff.id)
                    putExtra("NAME", staff.name)
                    putExtra("PHONE", staff.phone)
                    putExtra("DEPT", staff.department)
                }
                startActivity(intent)
            },
            onDeleteClick = { staff ->
                showDeleteConfirmation(staff)
            }
        )

        binding.rvStaff.apply {
            layoutManager = LinearLayoutManager(this@ViewStaffActivity)
            adapter = staffAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadStaff() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvEmpty.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getAllStaff(adminId)
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val staff = response.body() ?: emptyList()
                    staffAdapter.updateList(staff)
                    binding.tvStaffCount.text = "${staff.size} members"
                    
                    if (staff.isEmpty()) {
                        binding.tvEmpty.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(this@ViewStaffActivity, "Failed to load staff", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@ViewStaffActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDeleteConfirmation(staff: UserProfileResponse) {
        AlertDialog.Builder(this)
            .setTitle("Delete Staff")
            .setMessage("Are you sure you want to delete ${staff.name}? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                deleteStaff(staff.id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteStaff(userId: Int) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.deleteStaff(adminId, userId)
                if (response.isSuccessful) {
                    Toast.makeText(this@ViewStaffActivity, "Staff deleted successfully", Toast.LENGTH_SHORT).show()
                    loadStaff() // Refresh the list
                } else {
                    Toast.makeText(this@ViewStaffActivity, "Failed to delete staff", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ViewStaffActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadStaff() // Refresh in case profile was edited
    }
}
