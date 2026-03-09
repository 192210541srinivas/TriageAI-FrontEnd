package com.simats.triageai.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.simats.triageai.databinding.ItemHistoryColumnActionBinding
import com.simats.triageai.models.PatientActionLog

class ActionColumnAdapter(
    private var items: List<PatientActionLog> = emptyList()
) : RecyclerView.Adapter<ActionColumnAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemHistoryColumnActionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHistoryColumnActionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvActionTime.text = formatTime(item.timestamp)
        holder.binding.tvActionNotes.text = item.notes ?: "Action taken"
    }

    override fun getItemCount() = items.size

    fun updateList(newList: List<PatientActionLog>) {
        items = newList.sortedByDescending { it.timestamp }
        notifyDataSetChanged()
    }

    private fun formatTime(timestamp: String): String {
        return try {
            val parts = timestamp.split("T")
            if (parts.size > 1) {
                val datePart = parts[0] // 2026-02-27
                val timePart = parts[1].substring(0, 5) // 14:30
                
                val dateParts = datePart.split("-")
                if (dateParts.size == 3) {
                    "${dateParts[2]}/${dateParts[1]} $timePart" // 27/02 14:30
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
