package ru.behetem.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_intro.*
import ru.behetem.R

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        clickToContinue.setOnClickListener {
            moveToHome()
        }
    }

    private fun moveToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finishAffinity()
    }
}