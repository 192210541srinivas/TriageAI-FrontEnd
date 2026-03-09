package com.simats.triageai

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.simats.triageai.adapters.PatientAdapter
import com.simats.triageai.databinding.FragmentHistoryBinding
import com.simats.triageai.models.Patient
import com.simats.triageai.models.Priority

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PatientAdapter
    private var historyPatients = listOf<Patient>()
    private var searchQuery: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupData()
        setupUI()
    }

    private fun setupData() {
        // Mocking some "treated" patients for history
        historyPatients = listOf(
            Patient(
                id = "T089",
                name = "John Martinez",
                age = 58,
                gender = "Male",
                condition = "Chest pain with shortness of breath",
                bp = "180/110",
                hr = "125",
                spo2 = "89",
                temp = "37.2°C",
                respRate = "24",
                riskScore = 95,
                waitTime = "Jan 22, 2026 14:25",
                priority = Priority.CRITICAL,
                isDischarged = true,
                outcome = "Admitted",
                keySymptoms = "Severe chest pain, difficulty breathing, sweating",
                riskFactors = "Hypertension, diabetes, elevated vitals",
                aiRecommendation = "Immediate cardiac evaluation",
                chiefComplaintDescription = "Patient reports acute crushing chest pain for 30 mins.",
                treatmentSummary = "Patient was immediately assessed and stabilized. ECG performed showing signs of acute cardiac event. Cardiac enzymes elevated. Patient admitted to cardiac care unit for further monitoring and treatment. Responded well to initial interventions."
            ),
            Patient("P012", "Bob Marley", 32, "Male", "Sprained ankle", "115/75", "68 bpm", "99%", "36.8°C", "20", 5, "Jan 22, 2026 10:00", Priority.NON_URGENT, chiefComplaintDescription = "Minor injury from soccer.", isDischarged = true),
            Patient("P002", "Jane Smith", 55, "Female", "Controlled hypertension", "140/90", "80 bpm", "97%", "37.2°C", "22", 25, "Jan 21, 2026 18:30", Priority.URGENT, chiefComplaintDescription = "Medication adjustment required.", isDischarged = true)
        )
    }

    private fun setupUI() {
        adapter = PatientAdapter(historyPatients, showTreatButton = false)
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter

        // Search logic
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s.toString().lowercase()
                filterList()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterList() {
        val filtered = historyPatients.filter { patient ->
            patient.name.lowercase().contains(searchQuery) || 
            patient.id.lowercase().contains(searchQuery)
        }
        adapter.updateList(filtered)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
