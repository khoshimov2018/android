package com.example.dating.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dating.R
import com.example.dating.adapters.InterestsAdapter
import com.example.dating.adapters.NationalitiesAdapter
import com.example.dating.databinding.ActivityNationalitiesBinding
import com.example.dating.models.UserModel
import com.example.dating.utils.Constants
import com.example.dating.utils.getLoggedInUserFromShared
import com.example.dating.utils.showInfoAlertDialog
import com.example.dating.utils.validateResponse
import com.example.dating.viewmodels.MyInterestsViewModel
import com.example.dating.viewmodels.NationalitiesViewModel

class NationalitiesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNationalitiesBinding
    private lateinit var nationalitiesViewModel: NationalitiesViewModel

    private var nationalitiesAdapter: NationalitiesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_nationalities)
        binding.lifecycleOwner = this
        initViewModel()
    }

    private fun initViewModel() {
        nationalitiesViewModel = ViewModelProvider(this).get(NationalitiesViewModel::class.java)
        binding.viewModel = nationalitiesViewModel

        val loggedInUser = getLoggedInUserFromShared(this)
        nationalitiesViewModel.setLoggedInUser(loggedInUser)

        val profileUser = intent.getParcelableExtra<UserModel>(Constants.PROFILE_USER)
        profileUser?.let {
            nationalitiesViewModel.setCurrentUser(it)
        }

        nationalitiesViewModel.getNationalities()

        initObservers()
    }

    private fun initObservers() {
        nationalitiesViewModel.getNationalitiesList().observe(this, {
            if(it != null) {
                if(it.isEmpty()) {
                    showInfoAlertDialog(this, getString(R.string.no_nationalities))
                } else {
                    nationalitiesAdapter = NationalitiesAdapter(it, nationalitiesViewModel)
                    binding.adapter = nationalitiesAdapter
                    nationalitiesAdapter?.notifyDataSetChanged()
                }
            } else {
                nationalitiesAdapter = null
            }
        })

        nationalitiesViewModel.getShowNoInternet().observe(this, {
            if(it) {
                nationalitiesViewModel.setShowNoInternet(false)
                showInfoAlertDialog(this, getString(R.string.no_internet))
            }
        })

        nationalitiesViewModel.getBaseResponse().observe(this, {
            it?.let {
                nationalitiesViewModel.setBaseResponse(null)
                validateResponse(this, it)
            }
        })

        nationalitiesViewModel.getBackButtonClicked().observe(this, Observer { isPressed: Boolean ->
            if (isPressed) {
                this.onBackPressed()
            }
        })

        nationalitiesViewModel.getMoveFurther().observe(this, Observer {
            if (it) {
                nationalitiesViewModel.setMoveFurther(false)
                moveFurther()
            }
        })
    }

    private fun moveFurther() {
        val intent = Intent(this, EducationActivity::class.java)
        intent.putExtra(Constants.PROFILE_USER, nationalitiesViewModel.getCurrentUser())
        startActivity(intent)
    }
}