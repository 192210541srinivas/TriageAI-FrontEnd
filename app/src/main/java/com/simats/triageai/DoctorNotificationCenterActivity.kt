package com.simats.triageai

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.simats.triageai.adapters.NotificationAdapter
import com.simats.triageai.databinding.ActivityDoctorNotificationCenterBinding
import com.simats.triageai.models.Notification
import kotlinx.coroutines.launch

class DoctorNotificationCenterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorNotificationCenterBinding
    private lateinit var adapter: NotificationAdapter
    private var notifications = mutableListOf<Notification>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorNotificationCenterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupUI()
        fetchNotifications()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener { finish() }

        adapter = NotificationAdapter(notifications) { notification ->
            if (notification.status != "READ") {
                markAsRead(notification)
            }
        }
        binding.rvNotifications.layoutManager = LinearLayoutManager(this)
        binding.rvNotifications.adapter = adapter

        binding.btnMarkAllRead.setOnClickListener {
            markAllAsRead()
        }
    }

    private fun fetchNotifications() {
        val doctorId = getSharedPreferences("TriageAI", MODE_PRIVATE).getInt("user_id", -1)
        if (doctorId == -1) return

        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getNotifications(doctorId)
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    notifications.clear()
                    val filtered = response.body()!!.filter { it.type != "CASE_ASSIGNMENT" }
                    notifications.addAll(filtered)
                    adapter.notifyDataSetChanged()
                    updateUnreadCount()
                    
                    if (notifications.isEmpty()) {
                        binding.emptyState.visibility = View.VISIBLE
                    } else {
                        binding.emptyState.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@DoctorNotificationCenterActivity, "Error fetching notifications", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun markAsRead(notification: Notification) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.markNotificationAsRead(notification.id)
                if (response.isSuccessful) {
                    notification.status = "READ"
                    adapter.notifyDataSetChanged()
                    updateUnreadCount()
                }
            } catch (e: Exception) {
                // Silently fail
            }
        }
    }

    private fun markAllAsRead() {
        val doctorId = getSharedPreferences("TriageAI", MODE_PRIVATE).getInt("user_id", -1)
        if (doctorId == -1) return

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.markAllNotificationsAsRead(doctorId)
                if (response.isSuccessful) {
                    notifications.forEach { it.status = "READ" }
                    adapter.notifyDataSetChanged()
                    updateUnreadCount()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DoctorNotificationCenterActivity, "Error updating status", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUnreadCount() {
        val unreadCount = notifications.count { it.status != "READ" }
        binding.tvUnreadCount.text = "$unreadCount unread notifications"
    }
}
