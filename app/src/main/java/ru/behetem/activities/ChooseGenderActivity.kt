package ru.behetem.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.behetem.R
import ru.behetem.databinding.ActivityChooseGenderBinding
import ru.behetem.models.UserModel
import ru.behetem.utils.Constants
import ru.behetem.viewmodels.ChooseGenderViewModel

class ChooseGenderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseGenderBinding
    private lateinit var chooseGenderViewModel: ChooseGenderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_gender)
        binding.lifecycleOwner = this
        initViewModel()
    }

    private fun initViewModel() {
        chooseGenderViewModel = ViewModelProvider(this).get(ChooseGenderViewModel::class.java)
        binding.viewModel = chooseGenderViewModel

        val profileUser = intent.getParcelableExtra<UserModel>(Constants.PROFILE_USER)
        profileUser?.let {
            chooseGenderViewModel.setCurrentUser(it)
        }

        initObservers()
    }

    private fun initObservers() {
        chooseGenderViewModel.getBackButtonClicked().observe(this, Observer { isPressed: Boolean ->
            if (isPressed) {
                this.onBackPressed()
            }
        })

        chooseGenderViewModel.getMoveFurther().observe(this, Observer {
            if (it) {
                chooseGenderViewModel.setMoveFurther(false)
                moveFurther()
            }
        })
    }

    private fun moveFurther() {
        val intent = Intent(this, EnterNameActivity::class.java)
        intent.putExtra(Constants.PROFILE_USER, chooseGenderViewModel.getCurrentUser())
        startActivity(intent)
    }
}