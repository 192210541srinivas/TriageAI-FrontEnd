package com.simats.triageai

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.simats.triageai.adapters.PatientAdapter
import com.simats.triageai.databinding.FragmentPatientsBinding
import com.simats.triageai.models.Patient
import com.simats.triageai.models.Priority

class PatientsFragment : Fragment() {

    private var _binding: FragmentPatientsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PatientAdapter
    private var allPatients = listOf<Patient>()
    private var currentFilter: Priority? = null
    private var searchQuery: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPatientsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupData()
        setupUI()
    }

    private fun setupData() {
        allPatients = listOf(
            Patient("P001", "John Martinez", 68, "Male", "Chest pain with SOB", "180/110", "125 bpm", "96%", "37.2°C", "22", 95, "2 min", Priority.CRITICAL, chiefComplaintDescription = "Sudden onset of crushing chest pain."),
            Patient("P007", "Sarah Wilson", 52, "Female", "Severe allergic reaction", "90/60", "140 bpm", "91%", "36.8°C", "24", 93, "4 min", Priority.CRITICAL, chiefComplaintDescription = "Facial swelling and wheezing."),
            Patient("P003", "Emma Thompson", 34, "Female", "High fever with chills", "130/85", "105 bpm", "95%", "39.2°C", "20", 72, "15 min", Priority.URGENT, chiefComplaintDescription = "3 days of fever and cough.")
        )
    }

    private fun setupUI() {
        adapter = PatientAdapter(allPatients, showTreatButton = false)
        binding.rvPatients.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPatients.adapter = adapter

        // Search logic
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s.toString().lowercase()
                filterList()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Filter chips
        binding.chipAll.setOnClickListener { selectFilter(null, it as TextView) }
        binding.chipCritical.setOnClickListener { selectFilter(Priority.CRITICAL, it as TextView) }
        binding.chipUrgent.setOnClickListener { selectFilter(Priority.URGENT, it as TextView) }
        binding.chipStable.setOnClickListener { selectFilter(Priority.NON_URGENT, it as TextView) }
    }

    private fun selectFilter(priority: Priority?, textView: TextView) {
        currentFilter = priority
        
        // Reset all chips
        val chips = listOf(binding.chipAll, binding.chipCritical, binding.chipUrgent, binding.chipStable)
        chips.forEach {
            it.setBackgroundResource(R.drawable.bg_filter_chip_unselected)
            it.setTextColor(ContextCompat.getColor(requireContext(), R.color.nav_inactive)) 
        }
        
        // Highlight selected
        textView.setBackgroundResource(R.drawable.bg_filter_chip_selected)
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        
        filterList()
    }

    private fun filterList() {
        val filtered = allPatients.filter { patient ->
            val matchesSearch = patient.name.lowercase().contains(searchQuery) || 
                               patient.id.lowercase().contains(searchQuery)
            val matchesPriority = currentFilter == null || patient.priority == currentFilter
            matchesSearch && matchesPriority
        }
        adapter.updateList(filtered)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
