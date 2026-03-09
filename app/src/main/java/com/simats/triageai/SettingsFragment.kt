package com.simats.triageai

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.simats.triageai.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSettingsItems()
        setupClickListeners()
    }

    private fun setupSettingsItems() {
        // Account
        binding.itemProfile.apply {
            tvItemTitle.text = "User Profile"
            tvItemSubtitle.text = "View and edit your information"
            viewIconBg.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#EFF6FF"))
            ivItemIcon.setImageResource(R.drawable.ic_person)
            ivItemIcon.imageTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#2563EB"))
        }

        binding.itemPassword.apply {
            tvItemTitle.text = "Change Password"
            tvItemSubtitle.text = "Update your security credentials"
            viewIconBg.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#F5F3FF"))
            ivItemIcon.setImageResource(R.drawable.ic_doctor) // Using doctor as placeholder for lock
            ivItemIcon.imageTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#7C3AED"))
        }

        // Privacy
        binding.itemPrivacy.apply {
            tvItemTitle.text = "Data Privacy & Security"
            tvItemSubtitle.text = "HIPAA compliance information"
            viewIconBg.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#F0FDF4"))
            ivItemIcon.setImageResource(R.drawable.ic_stable) // Using stable as placeholder for shield
            ivItemIcon.imageTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#16A34A"))
        }

        binding.itemTransparency.apply {
            tvItemTitle.text = "AI Transparency & Bias"
            tvItemSubtitle.text = "How our AI makes decisions"
            viewIconBg.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#EFF6FF"))
            ivItemIcon.setImageResource(R.drawable.ic_brain)
            ivItemIcon.imageTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#2563EB"))
        }

        // Legal
        binding.itemDisclaimer.apply {
            tvItemTitle.text = "System Disclaimer"
            tvItemSubtitle.text = "Important legal information"
            viewIconBg.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FFFBEB"))
            ivItemIcon.setImageResource(R.drawable.ic_warning_triangle)
            ivItemIcon.imageTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#D97706"))
        }

        binding.itemTerms.apply {
            tvItemTitle.text = "Terms of Service"
            tvItemSubtitle.text = "Review usage terms"
            viewIconBg.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#F3F4F6"))
            ivItemIcon.setImageResource(R.drawable.ic_history)
            ivItemIcon.imageTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#6B7280"))
        }

        // About
        binding.itemInfo.apply {
            tvItemTitle.text = "App Info & Version"
            tvItemSubtitle.text = "v1.0.0 – Healthcare Edition"
            viewIconBg.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#EFF6FF"))
            ivItemIcon.setImageResource(R.drawable.ic_accuracy)
            ivItemIcon.imageTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#2563EB"))
        }

        // Contact
        binding.itemContact.apply {
            tvItemTitle.text = "Contact Us"
            tvItemSubtitle.text = "Get in touch with support"
            viewIconBg.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#F3F4F6"))
            ivItemIcon.setImageResource(R.drawable.ic_phone)
            ivItemIcon.imageTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#6B7280"))
        }
    }

    private fun setupClickListeners() {
        binding.btnSignOut.setOnClickListener {
            val prefs = requireContext().getSharedPreferences("TriageAI", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
            
            val intent = Intent(requireContext(), RoleSelectionActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
