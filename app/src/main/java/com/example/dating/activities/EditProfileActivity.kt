package com.example.dating.activities

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dating.R
import com.example.dating.adapters.InterestsAdapter
import com.example.dating.adapters.NationalitiesAdapter
import com.example.dating.databinding.ActivityEditProfileBinding
import com.example.dating.models.UserModel
import com.example.dating.utils.Constants
import com.example.dating.utils.getLoggedInUserFromShared
import com.example.dating.utils.isInternetAvailable
import com.example.dating.utils.showInfoAlertDialog
import com.example.dating.viewmodels.EditProfileViewModel
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var editProfileViewModel: EditProfileViewModel

    private var interestsAdapter: InterestsAdapter? = null
    private var nationalitiesAdapter: NationalitiesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        binding.lifecycleOwner = this
        initViewModel()
    }

    private fun initViewModel() {
        editProfileViewModel = ViewModelProvider(this).get(EditProfileViewModel::class.java)
        binding.viewModel = editProfileViewModel

        editProfileViewModel.setLoggedInUser(getLoggedInUserFromShared(this))

        val profileUser = intent.getParcelableExtra<UserModel>(Constants.PROFILE_USER)
        profileUser?.let {
            editProfileViewModel.setCurrentUser(it)
        }

        editProfileViewModel.getInterests()
        editProfileViewModel.getNationalities()

        initObservers()
    }

    private fun initObservers() {
        editProfileViewModel.getErrorResId().observe(this, {
            if(it != null) {
                editProfileViewModel.setErrorResId(null)
                showInfoAlertDialog(this, getString(it))
            }
        })

        editProfileViewModel.getShowDatePicker().observe(this, Observer {
            if (it) {
                editProfileViewModel.setShowDatePicker(false)
                showDatePicker()
            }
        })

        editProfileViewModel.getInterestsList().observe(this, {
            if(it != null) {
                if(it.isEmpty()) {
                    showInfoAlertDialog(this, getString(R.string.no_interests))
                } else {
                    interestsAdapter = InterestsAdapter(it, editProfileViewModel)
                    binding.interestsAdapter = interestsAdapter
                    interestsAdapter?.notifyDataSetChanged()
                }
            } else {
                interestsAdapter = null
            }
        })

        editProfileViewModel.getNationalitiesList().observe(this, {
            if(it != null) {
                if(it.isEmpty()) {
                    showInfoAlertDialog(this, getString(R.string.no_nationalities))
                } else {
                    nationalitiesAdapter = NationalitiesAdapter(it, editProfileViewModel)
                    binding.nationalitiesAdapter = nationalitiesAdapter
                    nationalitiesAdapter?.notifyDataSetChanged()
                }
            } else {
                nationalitiesAdapter = null
            }
        })

        editProfileViewModel.getBackButtonClicked().observe(this, Observer { isPressed: Boolean ->
            if (isPressed) {
                this.onBackPressed()
            }
        })
    }

    private fun showDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val picker = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                run {
                    editProfileViewModel.setDateOfBirth(selectedYear, selectedMonth, selectedDay)
                }
            }, year, month, day
        )
        picker.datePicker.maxDate = c.timeInMillis
        picker.show()
    }

    override fun onBackPressed() {
        if (isInternetAvailable(this)) {
            if(editProfileViewModel.updateProfile()) {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }
}