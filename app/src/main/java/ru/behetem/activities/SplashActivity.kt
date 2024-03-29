package ru.behetem.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.behetem.R
import ru.behetem.models.UserModel
import ru.behetem.utils.Constants
import ru.behetem.viewmodels.SplashViewModel

class SplashActivity : AppCompatActivity() {

    private lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initViewModel()
    }

    private fun initViewModel() {
        splashViewModel = ViewModelProvider.AndroidViewModelFactory(application).create(
            SplashViewModel::class.java)
        initObservers()
    }

    private fun initObservers() {
        splashViewModel.getShouldMoveToHome().observe(this, Observer {
            Handler(Looper.getMainLooper()).postDelayed({
                if(it) {
                    moveToHome()
                } else {
                    moveToRegister()
                }
            }, Constants.SPLASH_TIME_OUT)
        })

        splashViewModel.getShouldMoveToRegistrationFlow().observe(this, Observer {
            Handler(Looper.getMainLooper()).postDelayed({
                if(it) {
                    moveToChooseGender()
                }
            }, Constants.SPLASH_TIME_OUT)
        })
        splashViewModel.checkIfUserLoggedIn()
    }

    private fun moveToHome() {
        val homeActivity = Intent(this, HomeActivity::class.java)
        startActivity(homeActivity)
        finish()
    }

    private fun moveToRegister() {
        val registerActivity = Intent(this, RegisterActivity::class.java)
        startActivity(registerActivity)
        finish()
    }

    private fun moveToChooseGender() {
        val userModel = UserModel()
        val intent = Intent(this, ChooseGenderActivity::class.java)
        intent.putExtra(Constants.PROFILE_USER, userModel)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        // Do nothing
    }
}