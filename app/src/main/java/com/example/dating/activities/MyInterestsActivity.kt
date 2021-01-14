package com.example.dating.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dating.R
import com.example.dating.databinding.ActivityMyInterestsBinding
import com.example.dating.models.UserModel
import com.example.dating.utils.Constants
import com.example.dating.utils.getLoggedInUserFromShared
import com.example.dating.viewmodels.EnterDobViewModel
import com.example.dating.viewmodels.MyInterestsViewModel

class MyInterestsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyInterestsBinding
    private lateinit var myInterestsViewModel: MyInterestsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_interests)
        binding.lifecycleOwner = this
        initViewModel()
    }

    private fun initViewModel() {
        myInterestsViewModel = ViewModelProvider(this).get(MyInterestsViewModel::class.java)
        binding.viewModel = myInterestsViewModel

        val loggedInUser = getLoggedInUserFromShared(this)
        myInterestsViewModel.setLoggedInUser(loggedInUser)

        val profileUser = intent.getParcelableExtra<UserModel>(Constants.PROFILE_USER)
        profileUser?.let {
            myInterestsViewModel.setCurrentUser(it)
        }

        myInterestsViewModel.getInterests()

        initObservers()
    }

    private fun initObservers() {
        myInterestsViewModel.getBackButtonClicked().observe(this, Observer { isPressed: Boolean ->
            if (isPressed) {
                this.onBackPressed()
            }
        })

        myInterestsViewModel.getMoveFurther().observe(this, Observer {
            if (it) {
                myInterestsViewModel.setMoveFurther(false)
                moveFurther()
            }
        })
    }

    private fun moveFurther() {
        val intent = Intent(this, NationalitiesActivity::class.java)
        intent.putExtra(Constants.PROFILE_USER, myInterestsViewModel.getCurrentUser())
        startActivity(intent)
    }
}