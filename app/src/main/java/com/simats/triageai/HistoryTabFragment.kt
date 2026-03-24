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

import android.app.DatePickerDialog
import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.text.SimpleDateFormat
import java.util.*

class HistoryTabFragment : Fragment() {

    private var _binding: FragmentHistoryTabBinding? = null
    private val binding get() = _binding!!
    private var type: String? = null
    private var allData: List<Any> = emptyList()
    private var filteredData: List<BackendVitals> = emptyList()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    private val displayFormat = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = arguments?.getString("type")
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
        if (type == "VITALS") {
            setupGraph()
            setupFilters()
        }
        if (allData.isNotEmpty()) {
            updateUI()
        }
    }

    private fun setupRecyclerView() {
        binding.rvHistory.layoutManager = LinearLayoutManager(context)
    }

    private fun setupGraph() {
        binding.llVitalsFilter.visibility = View.VISIBLE
        binding.rvHistory.visibility = View.VISIBLE
        
        binding.lineChart.apply {
            val chartDescription = description
            chartDescription?.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            isScaleXEnabled = true
            isScaleYEnabled = true
            setPinchZoom(true)
            setDrawGridBackground(false)
            animateX(1500)
            
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                labelRotationAngle = -45f
            }

            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.LTGRAY
            }

            axisRight.isEnabled = false
            legend.isEnabled = true
        }
    }

    private fun setupFilters() {
        @Suppress("DEPRECATION")
        binding.chipGroupFilter.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.chip1Day -> filterData(1)
                R.id.chip2Days -> filterData(2)
                R.id.chip1Week -> filterData(7)
                R.id.chip1Month -> filterData(30)
                R.id.chipCustom -> showDatePickerRange()
            }
        }
    }

    private fun filterData(days: Int) {
        val vitals = allData.filterIsInstance<BackendVitals>()
        if (vitals.isEmpty()) return

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        val cutoffDate = calendar.time

        filteredData = vitals.filter { 
            val date = dateFormat.parse(it.recordedAt)
            date != null && date.after(cutoffDate)
        }.sortedBy { it.recordedAt }

        updateChart()
    }

    private fun showDatePickerRange() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            val startCalendar = Calendar.getInstance()
            startCalendar.set(year, month, dayOfMonth, 0, 0, 0)
            
            // For simplicity, we'll just ask for start date and show until now
            // Or we could trigger another picker for end date.
            // Let's do start date to now for now.
            val vitals = allData.filterIsInstance<BackendVitals>()
            filteredData = vitals.filter { 
                val date = dateFormat.parse(it.recordedAt)
                date != null && date.after(startCalendar.time)
            }.sortedBy { it.recordedAt }

            updateChart()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        
        datePickerDialog.show()
    }

    private fun updateChart() {
        if (filteredData.isEmpty()) {
            binding.lineChart.clear()
            return
        }

        val entriesBpSys = mutableListOf<Entry>()
        val entriesBpDia = mutableListOf<Entry>()
        val entriesHr = mutableListOf<Entry>()
        val entriesTemp = mutableListOf<Entry>()
        val entriesSpo2 = mutableListOf<Entry>()
        val labels = mutableListOf<String>()

        filteredData.forEachIndexed { index, vitals ->
            val bpParts = vitals.bp.split("/")
            if (bpParts.size == 2) {
                bpParts[0].trim().toFloatOrNull()?.let { entriesBpSys.add(Entry(index.toFloat(), it)) }
                bpParts[1].trim().toFloatOrNull()?.let { entriesBpDia.add(Entry(index.toFloat(), it)) }
            }
            entriesHr.add(Entry(index.toFloat(), vitals.heartRate.toFloat()))
            entriesTemp.add(Entry(index.toFloat(), vitals.temperature.toFloat()))
            entriesSpo2.add(Entry(index.toFloat(), vitals.spo2.toFloat()))
            
            val date = dateFormat.parse(vitals.recordedAt)
            labels.add(if (date != null) displayFormat.format(date) else vitals.recordedAt)
        }

        val dataSets = mutableListOf<ILineDataSet>()

        dataSets.add(createDataSet(entriesBpSys, "BP Systolic", Color.RED))
        dataSets.add(createDataSet(entriesBpDia, "BP Diastolic", Color.rgb(255, 102, 102)))
        dataSets.add(createDataSet(entriesHr, "Heart Rate", Color.BLUE))
        dataSets.add(createDataSet(entriesTemp, "Temp", Color.rgb(255, 165, 0))) // Orange
        dataSets.add(createDataSet(entriesSpo2, "SpO2", Color.GREEN))

        binding.lineChart.apply {
            data = LineData(dataSets)
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            notifyDataSetChanged()
            invalidate()
            animateX(1000)
        }
    }

    private fun createDataSet(entries: List<Entry>, label: String, color: Int): LineDataSet {
        return LineDataSet(entries, label).apply {
            this.color = color
            setCircleColor(color)
            lineWidth = 2f
            circleRadius = 4f
            setDrawCircleHole(false)
            valueTextSize = 9f
            setDrawFilled(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }
    }

    fun setData(newData: List<Any>) {
        allData = newData
        if (_binding != null) {
            updateUI()
        }
    }

    private fun updateUI() {
        when (type) {
            "VITALS" -> {
                binding.llVitalsFilter.visibility = View.VISIBLE
                binding.rvHistory.visibility = View.VISIBLE
                val adapter = VitalsColumnAdapter()
                binding.rvHistory.adapter = adapter
                adapter.updateList(allData.filterIsInstance<BackendVitals>())
                filterData(1) // Default to 1 day
            }
            "MEDICATION", "TESTS" -> {
                binding.llVitalsFilter.visibility = View.GONE
                binding.rvHistory.visibility = View.VISIBLE
                val adapter = ActionColumnAdapter()
                binding.rvHistory.adapter = adapter
                adapter.updateList(allData.filterIsInstance<PatientActionLog>())
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
