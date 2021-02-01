package com.example.dating.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dating.R
import com.example.dating.databinding.ActivityResetPasswordBinding
import com.example.dating.utils.getLoggedInUserFromShared
import com.example.dating.viewmodels.ForgotPasswordViewModel
import com.example.dating.viewmodels.ResetPasswordViewModel

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var resetPasswordViewModel: ResetPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reset_password)
        binding.lifecycleOwner = this
        initViewModel()
    }

    private fun initViewModel() {
        resetPasswordViewModel = ViewModelProvider(this).get(ResetPasswordViewModel::class.java)
        binding.viewModel = resetPasswordViewModel

        resetPasswordViewModel.setLoggedInUser(getLoggedInUserFromShared(this))

        initObservers()
    }

    private fun initObservers() {
        resetPasswordViewModel.getBackButtonClicked()
            .observe(this, Observer { isPressed: Boolean ->
                if (isPressed) {
                    this.onBackPressed()
                }
            })
    }
}