package com.example.dating.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dating.R
import com.example.dating.databinding.ActivityAddPhotosBinding
import com.example.dating.databinding.ActivityCoinsBinding
import com.example.dating.viewmodels.AddPhotosViewModel
import com.example.dating.viewmodels.CoinsViewModel

class CoinsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoinsBinding
    private lateinit var coinsViewModel: CoinsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_coins)
        binding.lifecycleOwner = this
        initViewModel()
    }

    private fun initViewModel() {
        coinsViewModel = ViewModelProvider(this).get(CoinsViewModel::class.java)
        binding.viewModel = coinsViewModel

        initObservers()
    }

    private fun initObservers() {
        coinsViewModel.getBackButtonClicked().observe(this, Observer { isPressed: Boolean ->
            if (isPressed) {
                this.onBackPressed()
            }
        })
    }
}