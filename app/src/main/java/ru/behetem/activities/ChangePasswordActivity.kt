package ru.behetem.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.behetem.R
import ru.behetem.databinding.ActivityChangePasswordBinding
import ru.behetem.utils.getLoggedInUserFromShared
import ru.behetem.viewmodels.ChangePasswordViewModel
import ru.behetem.viewmodels.ChooseLookingForViewModel

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var changePasswordViewModel: ChangePasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password)
        binding.lifecycleOwner = this
        initViewModel()
    }

    private fun initViewModel() {
        changePasswordViewModel = ViewModelProvider(this).get(ChangePasswordViewModel::class.java)
        binding.viewModel = changePasswordViewModel

        changePasswordViewModel.setLoggedInUser(getLoggedInUserFromShared(this))

        initObservers()
    }

    private fun initObservers() {
        changePasswordViewModel.getBackButtonClicked()
            .observe(this, Observer { isPressed: Boolean ->
                if (isPressed) {
                    this.onBackPressed()
                }
            })
    }
}