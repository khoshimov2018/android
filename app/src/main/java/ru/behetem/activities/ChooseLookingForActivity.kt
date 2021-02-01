package ru.behetem.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.behetem.R
import ru.behetem.databinding.ActivityChooseLookingForBinding
import ru.behetem.models.UserModel
import ru.behetem.utils.Constants
import ru.behetem.utils.getLoggedInUserFromShared
import ru.behetem.viewmodels.ChooseLookingForViewModel

class ChooseLookingForActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseLookingForBinding
    private lateinit var chooseLookingForViewModel: ChooseLookingForViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_looking_for)
        binding.lifecycleOwner = this
        initViewModel()
    }

    private fun initViewModel() {
        chooseLookingForViewModel = ViewModelProvider(this).get(ChooseLookingForViewModel::class.java)
        binding.viewModel = chooseLookingForViewModel

        val loggedInUser = getLoggedInUserFromShared(this)
        chooseLookingForViewModel.setLoggedInUser(loggedInUser)

        val profileUser = intent.getParcelableExtra<UserModel>(Constants.PROFILE_USER)
        profileUser?.let {
            chooseLookingForViewModel.setCurrentUser(it)
        }

        initObservers()
    }

    private fun initObservers() {
        chooseLookingForViewModel.getBackButtonClicked().observe(this, Observer { isPressed: Boolean ->
            if (isPressed) {
                this.onBackPressed()
            }
        })

        chooseLookingForViewModel.getMoveFurther().observe(this, Observer {
            if (it) {
                chooseLookingForViewModel.setMoveFurther(false)
                moveFurther()
            }
        })
    }

    private fun moveFurther() {
        val intent = Intent(this, MyInterestsActivity::class.java)
        intent.putExtra(Constants.PROFILE_USER, chooseLookingForViewModel.getCurrentUser())
        startActivity(intent)
        finishAffinity()
    }
}