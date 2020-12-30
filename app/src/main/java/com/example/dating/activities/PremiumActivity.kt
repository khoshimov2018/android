package com.example.dating.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dating.R
import com.example.dating.databinding.ActivityCoinsBinding
import com.example.dating.databinding.ActivityPremiumBinding
import com.example.dating.viewmodels.CoinsViewModel
import com.example.dating.viewmodels.PremiumViewModel

class PremiumActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPremiumBinding
    private lateinit var premiumViewModel: PremiumViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_premium)
        binding.lifecycleOwner = this
        initViewModel()
    }

    private fun initViewModel() {
        premiumViewModel = ViewModelProvider(this).get(PremiumViewModel::class.java)
        binding.viewModel = premiumViewModel

        initObservers()
    }

    private fun initObservers() {
        premiumViewModel.getBackButtonClicked().observe(this, Observer { isPressed: Boolean ->
            if (isPressed) {
                this.onBackPressed()
            }
        })
    }
}