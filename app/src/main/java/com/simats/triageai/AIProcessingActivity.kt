package com.simats.triageai

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.simats.triageai.databinding.ActivityAiProcessingBinding
import com.simats.triageai.models.PatientRequest
import com.simats.triageai.models.PatientResponse
import kotlinx.coroutines.launch

class AIProcessingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAiProcessingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAiProcessingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        startAnimations()
        startTriageProcess()
    }

    private fun startAnimations() {
        val pulseAnimation = android.view.animation.AnimationUtils
            .loadAnimation(this, R.anim.anim_pulse)
        binding.cardIcon.startAnimation(pulseAnimation)
        binding.viewGlow.startAnimation(pulseAnimation)

        val rotateAnimation = android.view.animation.AnimationUtils
            .loadAnimation(this, R.anim.anim_rotate)
        binding.imgOrbit.startAnimation(rotateAnimation)
    }

    private fun startTriageProcess() {

        // 🔹 Get Data From Previous Screens
        val name = intent.getStringExtra("NAME") ?: ""
        val email = intent.getStringExtra("EMAIL") ?: ""
        val age = intent.getIntExtra("AGE", 0)
        val gender = intent.getStringExtra("GENDER") ?: ""
        val phone = intent.getStringExtra("PHONE") ?: ""
        val address = intent.getStringExtra("ADDRESS") ?: ""

        val systolic = intent.getIntExtra("SYSTOLIC", 0)
        val diastolic = intent.getIntExtra("DIASTOLIC", 0)
        val heartRate = intent.getIntExtra("HEART_RATE", 0)
        val temperature = intent.getFloatExtra("TEMPERATURE", 0f)
        val spo2 = intent.getIntExtra("SPO2", 0)
        val respiratoryRate = intent.getIntExtra("RESPIRATORY_RATE", 0)

        val rawSymptoms = intent.getStringArrayListExtra("SYMPTOMS") ?: arrayListOf()
        val conditions = intent.getStringArrayListExtra("CHRONIC_CONDITIONS") ?: arrayListOf()
        val avpu = intent.getStringExtra("AVPU") ?: "Alert"
        val chiefComplaint = intent.getStringExtra("CHIEF_COMPLAINT") ?: ""

        // 🔹 Separate general symptoms from trauma types
        val generalSymptoms = mutableListOf<String>()
        val traumaTypes = mutableListOf<String>()

        rawSymptoms.forEach { symptom ->
            if (symptom.startsWith("Trauma: ")) {
                traumaTypes.add(symptom.replace("Trauma: ", ""))
            } else {
                generalSymptoms.add(symptom)
            }
        }

        // 🔹 Make Email Unique (avoid duplicate constraint error)
        val timestamp = System.currentTimeMillis()
        val uniqueEmail = if (email.contains("@")) {
            val parts = email.split("@")
            "${parts[0]}$timestamp@${parts[1]}"
        } else {
            "$email$timestamp@test.com"
        }

        // 🔹 Create Request
        val patientRequest = PatientRequest(
            full_name = name,
            age = age,
            gender = gender,
            phone = phone,
            address = address,
            email = uniqueEmail,
            systolic = systolic,
            diastolic = diastolic,
            heart_rate = heartRate,
            temperature = temperature,
            spo2 = spo2,
            respiratory_rate = respiratoryRate,
            avpu = avpu,
            chief_complaint = chiefComplaint,
            symptoms = generalSymptoms,
            trauma_types = traumaTypes,
            medical_conditions = conditions
        )

        updateStep1()

        val adminId = getSharedPreferences("TriageAI", MODE_PRIVATE)
            .getInt("user_id", -1)

        if (adminId == -1) {
            handleError("Session expired. Please login again.")
            return
        }

        lifecycleScope.launch {
            try {

                kotlinx.coroutines.delay(1000)
                updateStep2()

                kotlinx.coroutines.delay(1000)
                updateStep3()

                val response = ApiClient.apiService
                    .addPatient(adminId, patientRequest)

                if (response.isSuccessful && response.body() != null) {

                    val result = response.body()!!

                    updateStep4()
                    kotlinx.coroutines.delay(1000)
                    updateStep5()

                    val caseType = result.caseType ?: "NON-URGENT"
                    val probabilities = result.probabilities
                    val explanation = ArrayList(result.explanation ?: emptyList<String>())
                    
                    // 🔹 Use risk_score from backend if available, otherwise calculate from probabilities
                    val riskScore = result.riskScore ?: 0
                    val probCritical = if (probabilities?.critical != null) (probabilities.critical * 100).toInt() else riskScore
                    val probUrgent = ((probabilities?.urgent ?: 0.0) * 100).toInt()
                    val probNonUrgent = ((probabilities?.nonUrgent ?: 0.0) * 100).toInt()

                    android.util.Log.d("AIProcessing", "AI Result: $caseType, Score: $riskScore%, Probs: $probabilities")
                    
                    // Detailed Debug Toast
                    val debugMsg = "Score: $riskScore%, BP: $systolic/$diastolic, HR: $heartRate, Temp: $temperature"
                    Toast.makeText(this@AIProcessingActivity, debugMsg, Toast.LENGTH_LONG).show()

                    navigateToResult(
                        caseType = caseType,
                        riskScore = riskScore,
                        probCritical = probCritical,
                        probUrgent = probUrgent,
                        probNonUrgent = probNonUrgent,
                        explanation = explanation,
                        chiefComplaint = chiefComplaint ?: "Medical Evaluation",
                        systolic = systolic,
                        diastolic = diastolic,
                        heartRate = heartRate,
                        spo2 = spo2,
                        temperature = temperature.toDouble()
                    )

                } else {
                    val errorBody = response.errorBody()?.string()
                    handleError(
                        "Failed to add patient:\n${response.message()}\n$errorBody"
                    )
                }

            } catch (e: Exception) {
                handleError("Network error: ${e.message}")
            }
        }
    }

    private fun updateStep1() {
        binding.tvProgress.text = "10%\nRegistering Patient..."
        binding.progressBar.progress = 10
    }

    private fun updateStep2() {
        binding.tvProgress.text = "30%\nProcessing Data..."
        binding.progressBar.progress = 30
    }

    private fun updateStep3() {
        binding.tvProgress.text = "50%\nSyncing History..."
        binding.progressBar.progress = 50
    }

    private fun updateStep4() {
        binding.tvProgress.text = "80%\nAI Analysis..."
        binding.progressBar.progress = 80

        binding.pbStep4.visibility = android.view.View.GONE
        binding.imgStep4.visibility = android.view.View.VISIBLE
    }

    private fun updateStep5() {
        binding.tvProgress.text = "100%\nAnalysis Complete"
        binding.progressBar.progress = 100

        binding.pbStep5.visibility = android.view.View.GONE
        binding.imgStep5.visibility = android.view.View.VISIBLE
    }

    private fun navigateToResult(
        caseType: String,
        riskScore: Int,
        probCritical: Int,
        probUrgent: Int,
        probNonUrgent: Int,
        explanation: ArrayList<String>,
        chiefComplaint: String,
        systolic: Int,
        diastolic: Int,
        heartRate: Int,
        spo2: Int,
        temperature: Double
    ) {

        val targetActivity = when (caseType.uppercase()) {
            "CRITICAL" -> CriticalResultActivity::class.java
            "URGENT" -> UrgentResultActivity::class.java
            else -> NonUrgentResultActivity::class.java
        }

        val intent = Intent(this, targetActivity)

        intent.putExtra("RISK_SCORE", riskScore)
        intent.putExtra("TRIAGE_STATUS", caseType)
        intent.putExtra("CASE_TYPE", caseType)
        
        // Pass AI probabilities and explanation
        intent.putExtra("PROB_CRITICAL", probCritical)
        intent.putExtra("PROB_URGENT", probUrgent)
        intent.putExtra("PROB_NONURGENT", probNonUrgent)
        intent.putStringArrayListExtra("EXPLANATION", explanation)

        // Pass Patient Info for Findings Card
        intent.putExtra("CHIEF_COMPLAINT", chiefComplaint)
        intent.putExtra("SYSTOLIC", systolic)
        intent.putExtra("DIASTOLIC", diastolic)
        intent.putExtra("HEART_RATE", heartRate)
        intent.putExtra("SPO2", spo2)
        intent.putExtra("TEMPERATURE", temperature)

        binding.root.postDelayed({
            startActivity(intent)
            overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            finish()
        }, 1500)
    }

    private fun handleError(message: String) {
        android.util.Log.e("AIProcessing", "Triage Process Error: $message")
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        
        // Give the user time to read the message before finishing
        binding.root.postDelayed({
            if (!isFinishing) {
                finish()
            }
        }, 3000)
    }
}