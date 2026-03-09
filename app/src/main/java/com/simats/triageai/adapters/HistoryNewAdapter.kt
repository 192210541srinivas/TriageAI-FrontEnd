package com.simats.triageai.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.simats.triageai.R
import com.simats.triageai.databinding.ItemHistoryCaseNewBinding
import com.simats.triageai.models.Patient
import com.simats.triageai.models.Priority

class HistoryNewAdapter(
    private var patients: List<Patient>,
    private val onItemClick: (Patient) -> Unit
) : RecyclerView.Adapter<HistoryNewAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(val binding: ItemHistoryCaseNewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryCaseNewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val patient = patients[position]
        val context = holder.itemView.context
        
        with(holder.binding) {
            tvIdBadge.text = patient.id
            tvPatientName.text = patient.name
            tvDateTime.text = patient.waitTime // Using waitTime field for the date • time display
            tvRiskScoreValue.text = "${patient.riskScore}%"
            tvOutcomeValue.text = patient.outcome ?: "Admitted"
            
            // Priority Tag
            tvPriorityBadge.text = patient.priority.name
            val priorityColor = when (patient.priority) {
                Priority.CRITICAL -> ContextCompat.getColor(context, R.color.critical_main)
                Priority.URGENT -> ContextCompat.getColor(context, R.color.urgent_main)
                Priority.NON_URGENT -> ContextCompat.getColor(context, R.color.stable_main)
            }
            cardPriorityBadge.setCardBackgroundColor(priorityColor)

            root.setOnClickListener { onItemClick(patient) }
        }
    }

    override fun getItemCount() = patients.size

    fun updateList(newList: List<Patient>) {
        patients = newList
        notifyDataSetChanged()
    }
}
