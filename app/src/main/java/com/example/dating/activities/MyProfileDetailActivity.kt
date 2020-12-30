package com.example.dating.activities

import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dating.R
import com.example.dating.databinding.ActivityMyProfileDetailBinding
import com.example.dating.utils.dpToPx
import com.example.dating.utils.printLog
import com.example.dating.viewmodels.MyProfileDetailViewModel
import kotlinx.android.synthetic.main.activity_my_profile_detail.*

class MyProfileDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyProfileDetailBinding
    private lateinit var myProfileDetailViewModel: MyProfileDetailViewModel

    private val numberOfImages = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_profile_detail)
        binding.lifecycleOwner = this

        initViewModel()
        initView()
    }

    private fun initViewModel() {
        myProfileDetailViewModel = ViewModelProvider(this).get(MyProfileDetailViewModel::class.java)
        binding.viewModel = myProfileDetailViewModel

        initObservers()
    }

    private fun initObservers() {
        myProfileDetailViewModel.getBackButtonClicked()
            .observe(this, Observer { isPressed: Boolean ->
                if (isPressed) {
                    this.onBackPressed()
                }
            })
    }

    private fun initView() {
        for(index in 0 until numberOfImages) {
            val view = View(this)
            val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(0, dpToPx(this, 2F).toInt(), 1F)
            layoutParams.setMargins(dpToPx(this, 5F).toInt(), 0, dpToPx(this, 5F).toInt(), 0)
            view.layoutParams = layoutParams
            view.setBackgroundResource(R.color.lightGreyColor)

            countLinear.addView(view)
        }
        countLinear.requestLayout()
    }
}