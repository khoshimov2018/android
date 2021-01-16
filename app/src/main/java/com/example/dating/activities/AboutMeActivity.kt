package com.example.dating.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dating.R
import com.example.dating.databinding.ActivityAboutMeBinding
import com.example.dating.models.UserModel
import com.example.dating.utils.Constants
import com.example.dating.utils.getLoggedInUserFromShared
import com.example.dating.viewmodels.AboutMeViewModel
import com.example.dating.viewmodels.JobViewModel

class AboutMeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutMeBinding
    private lateinit var aboutMeViewModel: AboutMeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about_me)
        binding.lifecycleOwner = this
        initViewModel()
    }

    private fun initViewModel() {
        aboutMeViewModel = ViewModelProvider(this).get(AboutMeViewModel::class.java)
        binding.viewModel = aboutMeViewModel

        val loggedInUser = getLoggedInUserFromShared(this)
        aboutMeViewModel.setLoggedInUser(loggedInUser)

        val profileUser = intent.getParcelableExtra<UserModel>(Constants.PROFILE_USER)
        profileUser?.let {
            aboutMeViewModel.setCurrentUser(it)
        }

        initObservers()
    }

    private fun initObservers() {
        aboutMeViewModel.getBackButtonClicked().observe(this, Observer { isPressed: Boolean ->
            if (isPressed) {
                this.onBackPressed()
            }
        })

        aboutMeViewModel.getMoveFurther().observe(this, Observer {
            if (it) {
                aboutMeViewModel.setMoveFurther(false)
                moveFurther()
            }
        })
    }

    private fun moveFurther() {
        val intent = Intent(this, IntroSliderActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}