package ru.behetem.activities

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.behetem.R
import ru.behetem.databinding.ActivityEnterDobBinding
import ru.behetem.models.UserModel
import ru.behetem.utils.Constants
import ru.behetem.utils.getLoggedInUserFromShared
import ru.behetem.viewmodels.EnterDobViewModel
import java.util.*

class EnterDobActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEnterDobBinding
    private lateinit var enterDobViewModel: EnterDobViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enter_dob)
        binding.lifecycleOwner = this
        initViewModel()
    }

    private fun initViewModel() {
        enterDobViewModel = ViewModelProvider(this).get(EnterDobViewModel::class.java)
        binding.viewModel = enterDobViewModel

        val loggedInUser = getLoggedInUserFromShared(this)
        enterDobViewModel.setLoggedInUser(loggedInUser)

        val profileUser = intent.getParcelableExtra<UserModel>(Constants.PROFILE_USER)
        profileUser?.let {
            enterDobViewModel.setCurrentUser(it)
        }

        initObservers()
    }

    private fun initObservers() {
        enterDobViewModel.getBackButtonClicked().observe(this, Observer { isPressed: Boolean ->
            if (isPressed) {
                this.onBackPressed()
            }
        })

        enterDobViewModel.getShowDatePicker().observe(this, Observer {
            if (it) {
                enterDobViewModel.setShowDatePicker(false)
                showDatePicker()
            }
        })

        enterDobViewModel.getMoveFurther().observe(this, Observer {
            if (it) {
                enterDobViewModel.setMoveFurther(false)
                moveFurther()
            }
        })
    }

    private fun moveFurther() {
        val intent = Intent(this, AddPhotosActivity::class.java)
        intent.putExtra(Constants.PROFILE_USER, enterDobViewModel.getCurrentUser())
        startActivity(intent)
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
                    enterDobViewModel.setDateOfBirth(selectedYear, selectedMonth, selectedDay)
                }
            }, year, month, day
        )
        picker.datePicker.maxDate = c.timeInMillis
        picker.show()
    }
}