package com.simats.triageai.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.simats.triageai.PatientDetailActivity
import com.simats.triageai.databinding.ItemUrgentCaseNewBinding
import com.simats.triageai.models.Patient

class UrgentCasesNewAdapter(
    private var patients: List<Patient>
) : RecyclerView.Adapter<UrgentCasesNewAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemUrgentCaseNewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUrgentCaseNewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val patient = patients[position]
        val context = holder.itemView.context

        holder.binding.apply {
            tvPatientId.text = patient.id
            tvPatientName.text = patient.name
            tvPatientAge.text = "${patient.age} years old"
            tvCondition.text = patient.condition
            tvBp.text = patient.bp
            tvHr.text = patient.hr
            tvSpo2.text = patient.spo2
            tvTemp.text = patient.temp ?: "N/A"
            tvRiskScoreValue.text = "${patient.riskScore}%"
            tvTimeAgo.text = patient.waitTime
            
            // 🔥 Medical History
            tvMedicalHistory.text = if (patient.medicalHistory.isNotEmpty()) {
                "History: ${patient.medicalHistory.joinToString(", ")}"
            } else {
                "History: None"
            }

            root.setOnClickListener {
                val intent = Intent(context, PatientDetailActivity::class.java).apply {
                    putExtra("patient", patient)
                }
                context.startActivity(intent)
            }
            btnViewDetails.setOnClickListener {
                val intent = Intent(context, PatientDetailActivity::class.java).apply {
                    putExtra("patient", patient)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount() = patients.size

    fun updateList(newList: List<Patient>) {
        patients = newList
        notifyDataSetChanged()
    }
}
