package com.simats.triageai

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.simats.triageai.adapters.ActionColumnAdapter
import com.simats.triageai.adapters.VitalsColumnAdapter
import com.simats.triageai.databinding.FragmentHistoryTabBinding
import com.simats.triageai.models.BackendVitals
import com.simats.triageai.models.PatientActionLog

class HistoryTabFragment : Fragment() {

    private var _binding: FragmentHistoryTabBinding? = null
    private val binding get() = _binding!!
    private var type: String? = null
    private var data: List<Any> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = arguments?.getString("type")
        // Note: In a real app, you'd use a ViewModel to share data. 
        // For simplicity, we'll assume data is passed or updated via a method.
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        if (data.isNotEmpty()) {
            updateUI()
        }
    }

    private fun setupRecyclerView() {
        binding.rvHistory.layoutManager = LinearLayoutManager(context)
        updateUI()
    }

    fun setData(newData: List<Any>) {
        data = newData
        if (_binding != null) {
            updateUI()
        }
    }

    private fun updateUI() {
        when (type) {
            "VITALS" -> {
                val adapter = VitalsColumnAdapter()
                binding.rvHistory.adapter = adapter
                adapter.updateList(data.filterIsInstance<BackendVitals>())
            }
            "MEDICATION", "TESTS" -> {
                val adapter = ActionColumnAdapter()
                binding.rvHistory.adapter = adapter
                adapter.updateList(data.filterIsInstance<PatientActionLog>())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(type: String): HistoryTabFragment {
            val fragment = HistoryTabFragment()
            val args = Bundle()
            args.putString("type", type)
            fragment.arguments = args
            return fragment
        }
    }
}
