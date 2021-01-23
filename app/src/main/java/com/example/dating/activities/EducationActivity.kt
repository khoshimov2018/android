package com.example.dating.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dating.R
import com.example.dating.databinding.ActivityEducationBinding
import com.example.dating.models.UserModel
import com.example.dating.utils.Constants
import com.example.dating.utils.getLoggedInUserFromShared
import com.example.dating.viewmodels.EducationViewModel
import com.example.dating.viewmodels.NationalitiesViewModel

class EducationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEducationBinding
    private lateinit var educationViewModel: EducationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_education)
        binding.lifecycleOwner = this
        initViewModel()
    }

    private fun initViewModel() {
        educationViewModel = ViewModelProvider(this).get(EducationViewModel::class.java)
        binding.viewModel = educationViewModel

        val loggedInUser = getLoggedInUserFromShared(this)
        educationViewModel.setLoggedInUser(loggedInUser)

        val profileUser = intent.getParcelableExtra<UserModel>(Constants.PROFILE_USER)
        profileUser?.let {
            educationViewModel.setCurrentUser(it)
        }

        initObservers()
    }

    private fun initObservers() {
        educationViewModel.getBackButtonClicked().observe(this, Observer { isPressed: Boolean ->
            if (isPressed) {
                this.onBackPressed()
            }
        })

        educationViewModel.getMoveFurther().observe(this, Observer {
            if (it) {
                educationViewModel.setMoveFurther(false)
                moveFurther()
            }
        })
    }

    private fun moveFurther() {
        val intent = Intent(this, JobActivity::class.java)
        intent.putExtra(Constants.PROFILE_USER, educationViewModel.getCurrentUser())
        startActivity(intent)
    }
}