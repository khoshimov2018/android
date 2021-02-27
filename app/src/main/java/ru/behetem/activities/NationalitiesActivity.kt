package ru.behetem.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.behetem.R
import ru.behetem.adapters.NationalitiesAdapter
import ru.behetem.databinding.ActivityNationalitiesBinding
import ru.behetem.models.UserModel
import ru.behetem.utils.Constants
import ru.behetem.utils.getLoggedInUserFromShared
import ru.behetem.utils.showInfoAlertDialog
import ru.behetem.utils.validateResponse
import ru.behetem.viewmodels.NationalitiesViewModel

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
                    if(nationalitiesAdapter == null) {
                        nationalitiesAdapter = NationalitiesAdapter(it, nationalitiesViewModel, nationalitiesViewModel.getChosenGender())
                        binding.adapter = nationalitiesAdapter
                    }
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
        val intent = Intent(this, WantToContinueActivity::class.java)
        intent.putExtra(Constants.PROFILE_USER, nationalitiesViewModel.getCurrentUser())
        startActivity(intent)
    }
}