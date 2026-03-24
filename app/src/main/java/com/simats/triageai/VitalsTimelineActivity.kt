package com.simats.triageai

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.simats.triageai.models.Patient

class VitalsTimelineActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vitals_timeline)

        val patient = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("patient", Patient::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Patient>("patient")
        }
        if (patient != null && savedInstanceState == null) {
            val fragment = VitalsTimelineFragment.newInstance(patient)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        } else if (patient == null) {
            finish()
        }
    }
}
