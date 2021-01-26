package com.example.dating.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dating.R
import com.example.dating.databinding.ActivityJobBinding
import com.example.dating.models.UserModel
import com.example.dating.utils.Constants
import com.example.dating.utils.getLoggedInUserFromShared
import com.example.dating.viewmodels.EducationViewModel
import com.example.dating.viewmodels.JobViewModel

class JobActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJobBinding
    private lateinit var jobViewModel: JobViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job)
        binding.lifecycleOwner = this
        initViewModel()
    }

    private fun initViewModel() {
        jobViewModel = ViewModelProvider(this).get(JobViewModel::class.java)
        binding.viewModel = jobViewModel

        val loggedInUser = getLoggedInUserFromShared(this)
        jobViewModel.setLoggedInUser(loggedInUser)

        val profileUser = intent.getParcelableExtra<UserModel>(Constants.PROFILE_USER)
        profileUser?.let {
            jobViewModel.setCurrentUser(it)
        }

        initObservers()
    }

    private fun initObservers() {
        jobViewModel.getBackButtonClicked().observe(this, Observer { isPressed: Boolean ->
            if (isPressed) {
                this.onBackPressed()
            }
        })

        jobViewModel.getMoveFurther().observe(this, Observer {
            if (it) {
                jobViewModel.setMoveFurther(false)
                moveFurther()
            }
        })
    }

    private fun moveFurther() {
        val intent = Intent(this, HeightWeightActivity::class.java)
        intent.putExtra(Constants.PROFILE_USER, jobViewModel.getCurrentUser())
        startActivity(intent)
    }
}