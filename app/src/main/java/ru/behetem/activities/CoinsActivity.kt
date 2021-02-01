package ru.behetem.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.behetem.R
import ru.behetem.databinding.ActivityCoinsBinding
import ru.behetem.viewmodels.CoinsViewModel

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