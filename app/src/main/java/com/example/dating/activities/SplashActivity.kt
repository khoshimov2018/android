package com.example.dating.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.dating.R
import com.example.dating.utils.Constants

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        handle()
    }

    private fun handle() {
        Handler(Looper.getMainLooper()).postDelayed({
            moveToRegister()
        }, Constants.SPLASH_TIME_OUT)
    }

    private fun moveToRegister() {
        val registerActivity = Intent(this, RegisterActivity::class.java)
        startActivity(registerActivity)
        finish()
    }
}