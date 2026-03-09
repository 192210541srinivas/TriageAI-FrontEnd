package com.simats.triageai

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.simats.triageai.databinding.ActivityDoctorAppInfoBinding
import com.simats.triageai.databinding.ItemInfoRowBinding

class DoctorAppInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorAppInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorAppInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupUI()
    }

    private fun setupUI() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        // Version Information
        setupInfoRow(binding.rowVersion, "Version", "1.0.0")
        setupInfoRow(binding.rowBuild, "Build Number", "2026.01.100")
        setupInfoRow(binding.rowRelease, "Release Date", "January 2026")
        setupInfoRow(binding.rowEdition, "Edition", "Healthcare Professional")
        setupInfoRow(binding.rowPlatform, "Platform", "Android")

        // AI Model Information
        setupInfoRow(binding.rowModelVersion, "Model Version", "v3.2")
        setupInfoRow(binding.rowArchitecture, "Architecture", "Neural Network")
        setupInfoRow(binding.rowTraining, "Training Cases", "2.4M")
        setupInfoRow(binding.rowAccuracy, "Accuracy", "99.2%")
        setupInfoRow(binding.rowLastUpdated, "Last Updated", "January 2026")
        
        // Customize Accuracy Color
        binding.rowAccuracy.tvValue.setTextColor(android.graphics.Color.parseColor("#10B981"))

        binding.btnCheckUpdates.setOnClickListener {
            // Placeholder for check updates
        }

        binding.btnReleaseNotes.setOnClickListener {
            // Placeholder for release notes
        }
    }

    private fun setupInfoRow(rowBinding: ItemInfoRowBinding, label: String, value: String) {
        rowBinding.tvLabel.text = label
        rowBinding.tvValue.text = value
    }
}
