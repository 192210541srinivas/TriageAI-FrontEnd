package com.simats.triageai

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.simats.triageai.databinding.FragmentVitalsTimelineBinding
import com.simats.triageai.models.BackendVitals
import com.simats.triageai.models.Patient
import com.simats.triageai.models.Priority
import com.simats.triageai.models.PatientActionLog
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class VitalsTimelineFragment : Fragment() {

    private var _binding: FragmentVitalsTimelineBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var pagerAdapter: ActionsPagerAdapter
    private var patient: Patient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        patient = arguments?.getParcelable("patient")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVitalsTimelineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        fetchVitals()
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener { 
            activity?.onBackPressed()
        }

        patient?.let {
            binding.tvPatientHeader.text = "${it.name} • ${it.id}"
            updateMonitorInfo(it.priority)
        }

        pagerAdapter = ActionsPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter
        
        com.google.android.material.tabs.TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Vitals"
                1 -> "Medication"
                else -> "Tests"
            }
        }.attach()
    }

    private fun updateMonitorInfo(priority: Priority) {
        val interval = when(priority) {
            Priority.CRITICAL -> "5"
            Priority.URGENT -> "15"
            else -> "30"
        }
        binding.tvMonitorInfo.text = "Vitals are monitored every $interval minutes for this patient's priority level"
    }

    private fun fetchVitals() {
        val patientIdStr = patient?.id?.filter { it.isDigit() } ?: ""
        val patientId = patientIdStr.toIntOrNull() ?: return
        
        binding.progressBar.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                coroutineScope {
                    val vitalsDeferred = async { ApiClient.apiService.getVitalsHistory(patientId) }
                    val timelineDeferred = async { ApiClient.apiService.getPatientTimeline(patientId) }

                    val vitalsRes = vitalsDeferred.await()
                    val timelineRes = timelineDeferred.await()

                    binding.progressBar.visibility = View.GONE

                    if (vitalsRes.isSuccessful && vitalsRes.body() != null) {
                        val history = vitalsRes.body()!!
                        if (history.isNotEmpty()) {
                            updateLatestReading(history.first())
                        }
                        pagerAdapter.updateVitals(history)
                    }

                    if (timelineRes.isSuccessful && timelineRes.body() != null) {
                        val logs = timelineRes.body()!!
                        val medications = logs.filter { it.actionType == "MEDICATION" || it.actionType == "ADMINISTER_MEDICATION" }
                        val tests = logs.filter { it.actionType == "TEST" || it.actionType == "ORDER_TEST" }
                        
                        pagerAdapter.updateMedication(medications)
                        pagerAdapter.updateTests(tests)
                    }
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun updateLatestReading(latest: BackendVitals) {
        val time = try {
            val parts = latest.recordedAt.split("T")
            if (parts.size > 1) parts[1].substring(0, 5) else latest.recordedAt
        } catch (e: Exception) {
            "N/A"
        }
        
        binding.tvLatestTime.text = "Latest Reading ($time)"
        binding.tvLatestBp.text = latest.bp
        binding.tvLatestHr.text = latest.heartRate.toString()
        binding.tvLatestTemp.text = String.format("%.1f", latest.temperature)
        binding.tvLatestSpo2.text = latest.spo2.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class ActionsPagerAdapter(fragment: Fragment) : androidx.viewpager2.adapter.FragmentStateAdapter(fragment) {
        
        private val vitalsFragment = HistoryTabFragment.newInstance("VITALS")
        private val medicationFragment = HistoryTabFragment.newInstance("MEDICATION")
        private val testsFragment = HistoryTabFragment.newInstance("TESTS")

        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> vitalsFragment
                1 -> medicationFragment
                else -> testsFragment
            }
        }

        fun updateVitals(data: List<BackendVitals>) {
            vitalsFragment.setData(data)
        }

        fun updateMedication(data: List<PatientActionLog>) {
            medicationFragment.setData(data)
        }

        fun updateTests(data: List<PatientActionLog>) {
            testsFragment.setData(data)
        }
    }

    companion object {
        fun newInstance(patient: Patient): VitalsTimelineFragment {
            val fragment = VitalsTimelineFragment()
            val args = Bundle()
            args.putParcelable("patient", patient)
            fragment.arguments = args
            return fragment
        }
    }
}
