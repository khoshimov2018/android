package com.example.dating.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dating.R
import com.example.dating.databinding.ActivityEnterDobBinding
import com.example.dating.databinding.ActivityEnterNameBinding
import com.example.dating.viewmodels.EnterDobViewModel
import com.example.dating.viewmodels.EnterNameViewModel

class EnterDobActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEnterDobBinding
    private lateinit var enterDobViewModel: EnterDobViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enter_dob)
        binding.lifecycleOwner = this
        initViewModel()
    }

    private fun initViewModel() {
        enterDobViewModel = ViewModelProvider(this).get(EnterDobViewModel::class.java)
        binding.viewModel = enterDobViewModel

        initObservers()
    }

    private fun initObservers() {
        enterDobViewModel.getBackButtonClicked().observe(this, Observer { isPressed: Boolean ->
            if (isPressed) {
                this.onBackPressed()
            }
        })

        enterDobViewModel.getMoveFurther().observe(this, Observer {
            if (it) {
                enterDobViewModel.setMoveFurther(false)
                moveFurther()
            }
        })
    }

    private fun moveFurther() {
        val intent = Intent(this, AddPhotosActivity::class.java)
        startActivity(intent)
    }
}