package ru.behetem.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.behetem.R
import ru.behetem.databinding.ActivityResetPasswordBinding
import ru.behetem.utils.getLoggedInUserFromShared
import ru.behetem.viewmodels.ResetPasswordViewModel

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