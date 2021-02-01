package ru.behetem.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.behetem.R
import ru.behetem.adapters.InterestsAdapter
import ru.behetem.databinding.ActivityMyInterestsBinding
import ru.behetem.models.UserModel
import ru.behetem.utils.Constants
import ru.behetem.utils.getLoggedInUserFromShared
import ru.behetem.utils.showInfoAlertDialog
import ru.behetem.utils.validateResponse
import ru.behetem.viewmodels.MyInterestsViewModel

class MyInterestsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyInterestsBinding
    private lateinit var myInterestsViewModel: MyInterestsViewModel

    private var interestsAdapter: InterestsAdapter? = null

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
        myInterestsViewModel.getInterestsList().observe(this, {
            if(it != null) {
                if(it.isEmpty()) {
                    showInfoAlertDialog(this, getString(R.string.no_interests))
                } else {
                    interestsAdapter = InterestsAdapter(it, myInterestsViewModel)
                    binding.adapter = interestsAdapter
                    interestsAdapter?.notifyDataSetChanged()
                }
            } else {
                interestsAdapter = null
            }
        })

        myInterestsViewModel.getShowNoInternet().observe(this, {
            if(it) {
                myInterestsViewModel.setShowNoInternet(false)
                showInfoAlertDialog(this, getString(R.string.no_internet))
            }
        })

        myInterestsViewModel.getBaseResponse().observe(this, {
            it?.let {
                myInterestsViewModel.setBaseResponse(null)
                validateResponse(this, it)
            }
        })

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