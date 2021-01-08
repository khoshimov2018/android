package com.example.dating.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dating.R
import com.example.dating.databinding.ActivityAddPhotosBinding
import com.example.dating.databinding.ActivityChooseGenderBinding
import com.example.dating.models.UserModel
import com.example.dating.utils.Constants
import com.example.dating.viewmodels.AddPhotosViewModel
import com.example.dating.viewmodels.ChooseGenderViewModel

class AddPhotosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPhotosBinding
    private lateinit var addPhotosViewModel: AddPhotosViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_photos)
        binding.lifecycleOwner = this
        initViewModel()
    }

    private fun initViewModel() {
        addPhotosViewModel = ViewModelProvider(this).get(AddPhotosViewModel::class.java)
        binding.viewModel = addPhotosViewModel

        val loggedInUser = intent.getParcelableExtra<UserModel>(Constants.LOGGED_IN_USER)
        loggedInUser?.let {
            addPhotosViewModel.setCurrentUser(it)
        }

        initObservers()
    }

    private fun initObservers() {
        addPhotosViewModel.getBackButtonClicked().observe(this, Observer { isPressed: Boolean ->
            if (isPressed) {
                this.onBackPressed()
            }
        })

        addPhotosViewModel.getMoveFurther().observe(this, Observer {
            if (it) {
                addPhotosViewModel.setMoveFurther(false)
                moveFurther()
            }
        })
    }

    private fun moveFurther() {
        val intent = Intent(this, ChooseLookingForActivity::class.java)
        intent.putExtra(Constants.LOGGED_IN_USER, addPhotosViewModel.getCurrentUser())
        startActivity(intent)
    }
}