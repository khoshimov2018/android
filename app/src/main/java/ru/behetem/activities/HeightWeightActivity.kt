package ru.behetem.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.behetem.R
import ru.behetem.databinding.ActivityHeightWeightBinding
import ru.behetem.models.UserModel
import ru.behetem.utils.Constants
import ru.behetem.utils.getLoggedInUserFromShared
import ru.behetem.viewmodels.HeightWeightViewModel

class HeightWeightActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHeightWeightBinding
    private lateinit var heightWeightViewModel: HeightWeightViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_height_weight)
        binding.lifecycleOwner = this
        initViewModel()
    }

    private fun initViewModel() {
        heightWeightViewModel = ViewModelProvider(this).get(HeightWeightViewModel::class.java)
        binding.viewModel = heightWeightViewModel

        val loggedInUser = getLoggedInUserFromShared(this)
        heightWeightViewModel.setLoggedInUser(loggedInUser)

        val profileUser = intent.getParcelableExtra<UserModel>(Constants.PROFILE_USER)
        profileUser?.let {
            heightWeightViewModel.setCurrentUser(it)
        }

        initObservers()
    }

    private fun initObservers() {
        heightWeightViewModel.getBackButtonClicked().observe(this, Observer { isPressed: Boolean ->
            if (isPressed) {
                this.onBackPressed()
            }
        })

        heightWeightViewModel.getMoveFurther().observe(this, Observer {
            if (it) {
                heightWeightViewModel.setMoveFurther(false)
                moveFurther()
            }
        })
    }

    private fun moveFurther() {
        val intent = Intent(this, ru.behetem.activities.AboutMeActivity::class.java)
        intent.putExtra(Constants.PROFILE_USER, heightWeightViewModel.getCurrentUser())
        startActivity(intent)
    }
}