package com.simats.triageai

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.simats.triageai.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDashboard()
    }

    private fun setupDashboard() {
        // Mocking greeting from local storage if available
        val prefs = requireContext().getSharedPreferences("TriageAI", Context.MODE_PRIVATE)
        val role = prefs.getString("role", "doctor")
        val prettyRole = role?.replaceFirstChar { it.uppercase() } ?: "Doctor"
        binding.tvSubtitle.text = "Welcome, $prettyRole"

        // Category Card Click Listeners
        binding.cardCritical.setOnClickListener {
            startActivity(Intent(requireContext(), CriticalCasesActivity::class.java))
        }

        binding.cardUrgent.setOnClickListener {
            startActivity(Intent(requireContext(), UrgentCasesActivity::class.java))
        }

        binding.cardNonUrgent.setOnClickListener {
            startActivity(Intent(requireContext(), NonUrgentCasesActivity::class.java))
        }

        // Action Button Click Listeners
        binding.btnViewCritical.setOnClickListener {
            startActivity(Intent(requireContext(), CriticalCasesActivity::class.java))
        }

        binding.btnViewUrgent.setOnClickListener {
            startActivity(Intent(requireContext(), UrgentCasesActivity::class.java))
        }

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(requireContext(), TriageStep1Activity::class.java))
        }

        binding.ivNotifications.setOnClickListener {
            // Placeholder for notifications
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
