package com.simats.triageai.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.simats.triageai.ApiClient
import com.simats.triageai.R
import com.simats.triageai.UserProfileResponse
import com.simats.triageai.databinding.ItemStaffBinding

class StaffAdapter(
    private var staffList: List<UserProfileResponse>,
    private val onEditClick: (UserProfileResponse) -> Unit,
    private val onDeleteClick: (UserProfileResponse) -> Unit
) : RecyclerView.Adapter<StaffAdapter.StaffViewHolder>() {

    inner class StaffViewHolder(val binding: ItemStaffBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffViewHolder {
        val binding = ItemStaffBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StaffViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StaffViewHolder, position: Int) {
        val staff = staffList[position]
        holder.binding.apply {
            tvStaffName.text = staff.name
            tvStaffRole.text = staff.role.uppercase()
            tvStaffDept.text = "• ${staff.department ?: "N/A"}"
            tvStaffPhone.text = "Phone: ${staff.phone ?: "N/A"}"
 
            // Load profile photo
            val photoPath = staff.profilePhoto
            val fullUrl = if (!photoPath.isNullOrEmpty()) {
                if (photoPath.startsWith("http")) photoPath
                else ApiClient.BASE_URL.trimEnd('/') + photoPath
            } else null
 
            Glide.with(holder.itemView.context)
                .load(fullUrl)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .circleCrop()
                .into(ivStaffProfile)

            btnEdit.setOnClickListener { onEditClick(staff) }
            btnDelete.setOnClickListener { onDeleteClick(staff) }
        }
    }

    override fun getItemCount() = staffList.size

    fun updateList(newList: List<UserProfileResponse>) {
        staffList = newList
        notifyDataSetChanged()
    }
}
