package com.simats.triageai

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.simats.triageai.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var adapter: OnboardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupViewPager()
        setupIndicators()
        setupListeners()
    }

    private fun setupViewPager() {
        val items = listOf(
            OnboardingItem(
                "Welcome to TriageAI",
                "Smart patient prioritization powered by artificial intelligence to help you make faster, more informed decisions.",
                R.drawable.img_onboarding_1
            ),
            OnboardingItem(
                "Intelligent AI Analysis",
                "Advanced machine learning algorithms analyze patient vitals, symptoms, and history to provide severity classification and risk assessment.",
                R.drawable.img_onboarding_2
            ),
            OnboardingItem(
                "Clinical Benefits",
                "Reduce wait times, improve patient outcomes, and optimize resource allocation with data-driven insights.",
                R.drawable.img_onboarding_3
            )
        )

        adapter = OnboardingAdapter(items)
        binding.viewPager.adapter = adapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicators(position)

                if (position == items.size - 1) {
                    binding.btnNext.text = "Get Started"
                } else {
                    binding.btnNext.text = "Next"
                }
            }
        })
    }

    private fun setupIndicators() {
        val items = adapter.itemCount
        val indicators = arrayOfNulls<ImageView>(items)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)

        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.apply {
                setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.dot_inactive))
                this.layoutParams = layoutParams
            }
            binding.dotIndicator.addView(indicators[i])
        }
    }

    private fun updateIndicators(position: Int) {
        val childCount = binding.dotIndicator.childCount
        for (i in 0 until childCount) {
            val imageView = binding.dotIndicator.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.dot_active))
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.dot_inactive))
            }
        }
    }

    private fun setupListeners() {
        binding.btnNext.setOnClickListener {
            if (binding.viewPager.currentItem + 1 < adapter.itemCount) {
                binding.viewPager.currentItem += 1
            } else {
                finishOnboarding()
            }
        }

        binding.btnSkip.setOnClickListener {
            finishOnboarding()
        }
    }

    private fun finishOnboarding() {
        startActivity(Intent(this, RoleSelectionActivity::class.java))
        finish()
    }
}
