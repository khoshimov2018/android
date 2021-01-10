package com.example.dating.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dating.R
import com.example.dating.databinding.ActivityChooseGenderBinding
import com.example.dating.databinding.ActivityEnterNameBinding
import com.example.dating.models.UserModel
import com.example.dating.utils.Constants
import com.example.dating.viewmodels.ChooseGenderViewModel
import com.example.dating.viewmodels.EnterNameViewModel

class EnterNameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEnterNameBinding
    private lateinit var enterNameViewModel: EnterNameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enter_name)
        binding.lifecycleOwner = this
        initViewModel()
    }

    private fun initViewModel() {
        enterNameViewModel = ViewModelProvider(this).get(EnterNameViewModel::class.java)
        binding.viewModel = enterNameViewModel

        val profileUser = intent.getParcelableExtra<UserModel>(Constants.PROFILE_USER)
        profileUser?.let {
            enterNameViewModel.setCurrentUser(it)
        }

        initObservers()
    }

    private fun initObservers() {
        enterNameViewModel.getBackButtonClicked().observe(this, Observer { isPressed: Boolean ->
            if (isPressed) {
                this.onBackPressed()
            }
        })

        enterNameViewModel.getMoveFurther().observe(this, Observer {
            if (it) {
                enterNameViewModel.setMoveFurther(false)
                moveFurther()
            }
        })
    }

    private fun moveFurther() {
        val intent = Intent(this, EnterDobActivity::class.java)
        intent.putExtra(Constants.PROFILE_USER, enterNameViewModel.getCurrentUser())
        startActivity(intent)
    }
}