package com.example.dating.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dating.BuildConfig
import com.example.dating.R
import com.example.dating.databinding.ActivityAddPhotosBinding
import com.example.dating.databinding.ActivitySettingsBinding
import com.example.dating.utils.Constants
import com.example.dating.utils.openUrlInBrowser
import com.example.dating.viewmodels.AddPhotosViewModel
import com.example.dating.viewmodels.CoinsViewModel
import com.example.dating.viewmodels.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        binding.lifecycleOwner = this
        initViewModel()
    }

    private fun initViewModel() {
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        binding.viewModel = settingsViewModel

        settingsViewModel.setVersionName(BuildConfig.VERSION_NAME)

        initObservers()
    }

    private fun initObservers() {
        settingsViewModel.getBackButtonClicked().observe(this, Observer { isPressed: Boolean ->
            if (isPressed) {
                this.onBackPressed()
            }
        })

        settingsViewModel.getMoveToCoins().observe(this, {
            if(it) {
                settingsViewModel.setMoveToCoins(false)
                moveToCoins()
            }
        })

        settingsViewModel.getMoveToPremium().observe(this, {
            if(it) {
                settingsViewModel.setMoveToPremium(false)
                moveToPremium()
            }
        })

        settingsViewModel.getAboutUsClicked().observe(this, {
            openAboutUs()
        })

        settingsViewModel.getHelpClicked().observe(this, {
            openHelp()
        })

        settingsViewModel.getFeedbackClicked().observe(this, {
            openFeedback()
        })
    }

    private fun moveToCoins() {
        val intent = Intent(this, CoinsActivity::class.java)
        startActivity(intent)
    }

    private fun moveToPremium() {
        val intent = Intent(this, PremiumActivity::class.java)
        startActivity(intent)
    }

    private fun openAboutUs() {
        openUrlInBrowser(this, Constants.ABOUT_US_URL)
    }

    private fun openHelp() {
        openUrlInBrowser(this, Constants.HELP_URL)
    }

    private fun openFeedback() {
        openUrlInBrowser(this, Constants.FEEDBACk_URL)
    }
}