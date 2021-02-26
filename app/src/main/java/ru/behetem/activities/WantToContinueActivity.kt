package ru.behetem.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_want_to_continue.*
import ru.behetem.R
import ru.behetem.models.UserModel
import ru.behetem.utils.Constants

class WantToContinueActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_want_to_continue)

        val profileUser = intent.getParcelableExtra<UserModel>(Constants.PROFILE_USER)

        skip.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

        continueButton.setOnClickListener {
            val intent = Intent(this, NationalitiesActivity::class.java)
            intent.putExtra(Constants.PROFILE_USER, profileUser)
            startActivity(intent)
        }
    }
}