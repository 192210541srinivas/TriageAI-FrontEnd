package com.simats.triageai.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.simats.triageai.R
import com.simats.triageai.databinding.ItemNotificationBinding
import com.simats.triageai.models.Notification

class NotificationAdapter(
    private var notifications: List<Notification>,
    private val onNotificationClick: (Notification) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notifications[position]
        holder.binding.tvTitle.text = notification.type.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
        holder.binding.tvMessage.text = notification.message
        holder.binding.tvTime.text = notification.createdAt // Assuming formatted string from backend

        // Unread styling
        if (notification.status != "READ") {
            holder.binding.unreadStrip.visibility = View.VISIBLE
            holder.binding.unreadDot.visibility = View.VISIBLE
            holder.binding.cardNotification.strokeWidth = 2
            holder.binding.cardNotification.setStrokeColor(ColorStateList.valueOf(
                ContextCompat.getColor(holder.itemView.context, R.color.blue_primary)
            ))
        } else {
            holder.binding.unreadStrip.visibility = View.GONE
            holder.binding.unreadDot.visibility = View.GONE
            holder.binding.cardNotification.strokeWidth = 0
        }

        // Type-based styling (matching design colors)
        val context = holder.itemView.context
        when (notification.type.uppercase()) {
            "ESCALATION", "CRITICAL" -> {
                holder.binding.ivIcon.setImageResource(R.drawable.ic_critical)
                holder.binding.iconContainer.setCardBackgroundColor(Color.parseColor("#FEF2F2"))
                holder.binding.ivIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#EF4444"))
            }
            "ASSESSMENT", "WAIT_TIME" -> {
                holder.binding.ivIcon.setImageResource(R.drawable.ic_clock)
                holder.binding.iconContainer.setCardBackgroundColor(Color.parseColor("#FFFBEB"))
                holder.binding.ivIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#F59E0B"))
            }
            "MEDICATION", "TEST" -> {
                holder.binding.ivIcon.setImageResource(R.drawable.ic_brain)
                holder.binding.iconContainer.setCardBackgroundColor(Color.parseColor("#EFF6FF"))
                holder.binding.ivIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#3B82F6"))
            }
            "CASE_ASSIGNMENT" -> {
                holder.binding.ivIcon.setImageResource(R.drawable.ic_check)
                holder.binding.iconContainer.setCardBackgroundColor(Color.parseColor("#F0FDF4"))
                holder.binding.ivIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#22C55E"))
            }
            "REJECTION" -> {
                holder.binding.ivIcon.setImageResource(R.drawable.ic_critical)
                holder.binding.iconContainer.setCardBackgroundColor(Color.parseColor("#FFF7ED"))
                holder.binding.ivIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#F97316"))
            }
            "REMINDER" -> {
                holder.binding.ivIcon.setImageResource(R.drawable.ic_clock)
                holder.binding.iconContainer.setCardBackgroundColor(Color.parseColor("#FFF7ED"))
                holder.binding.ivIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#F97316"))
            }
            "VITALS_UPDATE" -> {
                holder.binding.ivIcon.setImageResource(R.drawable.ic_pulse_blue)
                holder.binding.iconContainer.setCardBackgroundColor(Color.parseColor("#F0F9FF"))
                holder.binding.ivIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#0EA5E9"))
            }
            else -> {
                holder.binding.ivIcon.setImageResource(R.drawable.ic_brain)
                holder.binding.iconContainer.setCardBackgroundColor(Color.LTGRAY)
            }
        }

        holder.itemView.setOnClickListener { onNotificationClick(notification) }
    }

    override fun getItemCount() = notifications.size

    fun updateList(newList: List<Notification>) {
        notifications = newList
        notifyDataSetChanged()
    }
}
