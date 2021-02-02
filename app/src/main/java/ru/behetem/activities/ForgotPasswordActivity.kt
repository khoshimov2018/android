package ru.behetem.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.behetem.R
import ru.behetem.databinding.ActivityForgotPasswordBinding
import ru.behetem.utils.getLoggedInUserFromShared
import ru.behetem.viewmodels.ForgotPasswordViewModel

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var forgotPasswordViewModel: ForgotPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
        binding.lifecycleOwner = this
        initViewModel()
    }

    private fun initViewModel() {
        forgotPasswordViewModel = ViewModelProvider(this).get(ForgotPasswordViewModel::class.java)
        binding.viewModel = forgotPasswordViewModel
        initObservers()
    }

    private fun initObservers() {
        forgotPasswordViewModel.getBackButtonClicked().observe(this, Observer { isPressed: Boolean ->
            if (isPressed) {
                this.onBackPressed()
            }
        })
    }
}