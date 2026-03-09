package com.simats.triageai.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.simats.triageai.databinding.ItemVitalsHistoryBinding
import com.simats.triageai.models.BackendVitals

class VitalsHistoryAdapter(
    private var history: List<BackendVitals>
) : RecyclerView.Adapter<VitalsHistoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemVitalsHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVitalsHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = history[position]
        holder.binding.tvTime.text = formatTime(item.recordedAt)
        holder.binding.tvBp.text = item.bp
        holder.binding.tvHr.text = item.heartRate.toString()
        holder.binding.tvTemp.text = String.format("%.1f", item.temperature)
        holder.binding.tvSpo2.text = item.spo2.toString()
    }

    override fun getItemCount() = history.size

    fun updateList(newList: List<BackendVitals>) {
        history = newList
        notifyDataSetChanged()
    }

    private fun formatTime(timestamp: String): String {
        // Simple extraction of HH:mm from ISO string (e.g., 2026-02-26T15:10:00)
        return try {
            val parts = timestamp.split("T")
            if (parts.size > 1) {
                parts[1].substring(0, 5)
            } else {
                timestamp
            }
        } catch (e: Exception) {
            timestamp
        }
    }
}
