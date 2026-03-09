package com.simats.triageai.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.simats.triageai.databinding.ItemHistoryColumnVitalsBinding
import com.simats.triageai.models.BackendVitals

class VitalsColumnAdapter(
    private var items: List<BackendVitals> = emptyList()
) : RecyclerView.Adapter<VitalsColumnAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemHistoryColumnVitalsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHistoryColumnVitalsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvTime.text = formatTime(item.recordedAt)
        holder.binding.tvBp.text = item.bp
        holder.binding.tvHr.text = "${item.heartRate} bpm"
        holder.binding.tvTemp.text = "${item.temperature}°C"
        holder.binding.tvSpo2.text = "${item.spo2}%"
    }

    override fun getItemCount() = items.size

    fun updateList(newList: List<BackendVitals>) {
        items = newList.sortedByDescending { it.recordedAt }
        notifyDataSetChanged()
    }

    private fun formatTime(timestamp: String): String {
        return try {
            val parts = timestamp.split("T")
            if (parts.size > 1) {
                val datePart = parts[0] // 2026-02-27
                val timePart = parts[1].substring(0, 5) // 19:42
                
                val dateParts = datePart.split("-")
                if (dateParts.size == 3) {
                    "${dateParts[2]}/${dateParts[1]} $timePart" // 27/02 19:42
                } else {
                    "$datePart $timePart"
                }
            } else {
                timestamp
            }
        } catch (e: Exception) {
            timestamp
        }
    }
}
