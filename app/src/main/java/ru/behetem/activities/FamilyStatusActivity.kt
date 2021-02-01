package ru.behetem.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.behetem.R
import ru.behetem.databinding.ActivityFamilyStatusBinding
import ru.behetem.models.UserModel
import ru.behetem.utils.Constants
import ru.behetem.utils.getLoggedInUserFromShared
import ru.behetem.viewmodels.FamilyStatusViewModel

class FamilyStatusActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFamilyStatusBinding
    private lateinit var familyStatusViewModel: FamilyStatusViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_family_status)
        binding.lifecycleOwner = this
        initViewModel()
    }

    private fun initViewModel() {
        familyStatusViewModel = ViewModelProvider(this).get(FamilyStatusViewModel::class.java)
        binding.viewModel = familyStatusViewModel

        val loggedInUser = getLoggedInUserFromShared(this)
        familyStatusViewModel.setLoggedInUser(loggedInUser)

        val profileUser = intent.getParcelableExtra<UserModel>(Constants.PROFILE_USER)
        profileUser?.let {
            familyStatusViewModel.setCurrentUser(it)
        }

        initObservers()
    }

    private fun initObservers() {
        familyStatusViewModel.getBackButtonClicked().observe(this, Observer { isPressed: Boolean ->
            if (isPressed) {
                this.onBackPressed()
            }
        })

        familyStatusViewModel.getMoveFurther().observe(this, Observer {
            if (it) {
                familyStatusViewModel.setMoveFurther(false)
                moveFurther()
            }
        })
    }

    private fun moveFurther() {
        val intent = Intent(this, HeightWeightActivity::class.java)
        intent.putExtra(Constants.PROFILE_USER, familyStatusViewModel.getCurrentUser())
        startActivity(intent)
    }
}