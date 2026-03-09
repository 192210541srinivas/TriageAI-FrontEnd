package com.simats.triageai.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.simats.triageai.databinding.ItemActionLogBinding
import com.simats.triageai.databinding.ItemVitalsHistoryBinding
import com.simats.triageai.models.BackendVitals
import com.simats.triageai.models.PatientActionLog

class TimelineAdapter(
    private var items: List<Any> = emptyList()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_VITALS = 0
        private const val TYPE_ACTION = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is BackendVitals -> TYPE_VITALS
            is PatientActionLog -> TYPE_ACTION
            else -> throw IllegalArgumentException("Unknown item type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_VITALS -> VitalsViewHolder(ItemVitalsHistoryBinding.inflate(inflater, parent, false))
            TYPE_ACTION -> ActionViewHolder(ItemActionLogBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is VitalsViewHolder -> holder.bind(item as BackendVitals)
            is ActionViewHolder -> holder.bind(item as PatientActionLog)
        }
    }

    override fun getItemCount() = items.size

    fun updateList(newList: List<Any>) {
        items = newList.sortedByDescending { it.getTimestamp() }
        notifyDataSetChanged()
    }

    private fun Any.getTimestamp(): String {
        return when (this) {
            is BackendVitals -> this.recordedAt
            is PatientActionLog -> this.timestamp
            else -> ""
        }
    }

    class VitalsViewHolder(private val binding: ItemVitalsHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BackendVitals) {
            binding.tvTime.text = formatTime(item.recordedAt)
            binding.tvBp.text = item.bp
            binding.tvHr.text = item.heartRate.toString()
            binding.tvTemp.text = String.format("%.1f", item.temperature)
            binding.tvSpo2.text = item.spo2.toString()
        }
    }

    class ActionViewHolder(private val binding: ItemActionLogBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PatientActionLog) {
            binding.tvActionTime.text = formatTime(item.timestamp)
            binding.tvActionType.text = item.actionType.replace("_", " ")
            binding.tvActionNotes.text = item.notes ?: "No additional notes"
            binding.tvDoctorInfo.text = "By Doctor #${item.doctorId}"
        }
    }
}

private fun formatTime(timestamp: String): String {
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
