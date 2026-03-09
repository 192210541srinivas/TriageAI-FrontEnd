package com.simats.triageai.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.simats.triageai.PatientDetailActivity
import com.simats.triageai.databinding.ItemUrgentCaseBinding
import com.simats.triageai.models.Patient

class UrgentCasesAdapter(
    private var patients: List<Patient>
) : RecyclerView.Adapter<UrgentCasesAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemUrgentCaseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUrgentCaseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
