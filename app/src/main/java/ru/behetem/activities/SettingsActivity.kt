package ru.behetem.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.behetem.BuildConfig
import ru.behetem.R
import ru.behetem.databinding.ActivitySettingsBinding
import ru.behetem.utils.Constants
import ru.behetem.utils.openUrlInBrowser
import ru.behetem.viewmodels.SettingsViewModel

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