package com.example.dating.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dating.R
import com.example.dating.databinding.ActivityChooseGenderBinding
import com.example.dating.databinding.ActivityChooseLookingForBinding
import com.example.dating.viewmodels.ChooseGenderViewModel
import com.example.dating.viewmodels.ChooseLookingForViewModel

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
        /*val intent = Intent(this, EnterNameActivity::class.java)
        startActivity(intent)*/
    }
}