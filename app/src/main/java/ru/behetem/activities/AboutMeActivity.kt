package ru.behetem.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.behetem.R
import ru.behetem.databinding.ActivityAboutMeBinding
import ru.behetem.models.UserModel
import ru.behetem.utils.Constants
import ru.behetem.utils.getLoggedInUserFromShared
import ru.behetem.viewmodels.AboutMeViewModel

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
        val intent = Intent(this, ru.behetem.activities.IntroSliderActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}