package com.simats.triageai.adapters

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.simats.triageai.PatientDetailActivity
import com.simats.triageai.R
import com.simats.triageai.databinding.ItemPatientBinding
import com.simats.triageai.models.Patient
import com.simats.triageai.models.Priority

class PatientAdapter(
    private var patients: List<Patient>,
    private val showTreatButton: Boolean = true
) : RecyclerView.Adapter<PatientAdapter.PatientViewHolder>() {

    private var filteredPatients: List<Patient> = patients

    class PatientViewHolder(val binding: ItemPatientBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val binding = ItemPatientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PatientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val patient = filteredPatients[position]
        val context = holder.itemView.context
        
        holder.binding.apply {
            tvPatientId.text = patient.id
            tvPatientName.text = patient.name
            tvPatientAge.text = "${patient.age} years old"
            tvCondition.text = patient.condition
            tvBp.text = patient.bp
            tvHr.text = patient.hr
            tvSpo2.text = patient.spo2
            tvRiskScoreValue.text = "${patient.riskScore}%"
            tvTimeAgo.text = patient.waitTime
            tvPriorityLabel.text = patient.priority.name

            val color = when (patient.priority) {
                Priority.CRITICAL -> ContextCompat.getColor(context, R.color.critical_main)
                Priority.URGENT -> ContextCompat.getColor(context, R.color.urgent_main)
                Priority.NON_URGENT -> ContextCompat.getColor(context, R.color.stable_main)
            }

            val bgColor = when (patient.priority) {
                Priority.CRITICAL -> Color.parseColor("#FEF2F2")
                Priority.URGENT -> Color.parseColor("#FFF7ED")
                Priority.NON_URGENT -> Color.parseColor("#F0FDF4")
            }

            viewPriorityAccent.setBackgroundColor(color)
            tvPatientId.setTextColor(color)
            tvPatientId.backgroundTintList = ColorStateList.valueOf(bgColor)
            cardStatusBadge.setCardBackgroundColor(color)
            cardCondition.setCardBackgroundColor(bgColor)
            
            // Handle Temperature Vital Visibility
            if (patient.priority == Priority.URGENT && patient.temp != null) {
                spacerTemp.visibility = View.VISIBLE
                layoutTemp.visibility = View.VISIBLE
                tvTemp.text = patient.temp
            } else {
                spacerTemp.visibility = View.GONE
                layoutTemp.visibility = View.GONE
            }

            // Handle Single Action Button
            btnAction.backgroundTintList = ColorStateList.valueOf(color)
            btnAction.text = if (patient.priority == Priority.CRITICAL && showTreatButton) {
                "View Details & Treat"
            } else {
                "View Details"
            }

            // Navigation
            root.setOnClickListener {
                val intent = when {
                    patient.isDischarged -> Intent(context, com.simats.triageai.CaseReportActivity::class.java)
                    !showTreatButton && patient.priority == Priority.NON_URGENT -> Intent(context, com.simats.triageai.NonUrgentDetailActivity::class.java)
                    !showTreatButton && patient.priority != Priority.NON_URGENT -> Intent(context, com.simats.triageai.ActivePatientDetailActivity::class.java)
                    else -> Intent(context, PatientDetailActivity::class.java)
                }
                
                intent.putExtra("patient", patient)
                context.startActivity(intent)
            }
            
            btnAction.setOnClickListener {
                val intent = when {
                    patient.isDischarged -> Intent(context, com.simats.triageai.CaseReportActivity::class.java)
                    !showTreatButton && patient.priority == Priority.NON_URGENT -> Intent(context, com.simats.triageai.NonUrgentDetailActivity::class.java)
                    !showTreatButton && patient.priority != Priority.NON_URGENT -> Intent(context, com.simats.triageai.ActivePatientDetailActivity::class.java)
                    else -> Intent(context, PatientDetailActivity::class.java)
                }
                
                intent.putExtra("patient", patient)
                if (patient.priority == Priority.CRITICAL && showTreatButton && !patient.isDischarged) {
                    intent.putExtra("AUTO_TREAT", true)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount() = filteredPatients.size

    fun updateList(newList: List<Patient>) {
        filteredPatients = newList
        notifyDataSetChanged()
    }
}
