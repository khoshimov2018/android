package com.example.dating.fragments

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.dating.R
import com.example.dating.databinding.UserProfileFragmentBinding
import com.example.dating.utils.dpToPx
import com.example.dating.utils.printLog
import com.example.dating.viewmodels.UserProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.user_profile_fragment.*

class UserProfileFragment : Fragment() {

    companion object {
        fun newInstance() = UserProfileFragment()
    }

    private lateinit var viewModel: UserProfileViewModel
    private lateinit var binding: UserProfileFragmentBinding

    private val numberOfImages = 4
    private val listOfCountViews: MutableList<View> = ArrayList()
    private val listOfImages: MutableList<Int> =
        mutableListOf(R.color.color1, R.color.color2, R.color.color3, R.color.color4)
    private var currentSelectedIndex = 0

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(UserProfileViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.user_profile_fragment, container, false)
        val view: View = binding.root
        initViewModel()

        return view
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        var downYValue = 0.0F
        val threshold = 50F

        imageView.setOnTouchListener(View.OnTouchListener { _, motionEvent ->
            val halfOfScreen = mainLayout.width / 2
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    downYValue = motionEvent.y
                }
                MotionEvent.ACTION_UP -> {
                    val yPosition = motionEvent.y
                    if (downYValue - yPosition > threshold) {
                        toggleBottomSheet()
                    } else {
                        val position = motionEvent.x
                        if (position < halfOfScreen) {
                            // go to previous
                            if (currentSelectedIndex > 0) {
                                showIndex(--currentSelectedIndex)
                            }
                        } else {
                            // go to next
                            if (currentSelectedIndex < listOfImages.size - 1) {
                                showIndex(++currentSelectedIndex)
                            }
                        }
                    }
                }
            }
            return@OnTouchListener true
        })
        initView()
    }

    private fun initViewModel() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        initObservers()
    }

    private fun initObservers() {

    }

    private fun initView() {
        for (index in 0 until numberOfImages) {
            val view = View(requireActivity())
            val layoutParams: LinearLayout.LayoutParams =
                LinearLayout.LayoutParams(0, dpToPx(requireActivity(), 2F).toInt(), 1F)
            layoutParams.setMargins(
                dpToPx(requireActivity(), 5F).toInt(),
                0,
                dpToPx(requireActivity(), 5F).toInt(),
                0
            )
            view.layoutParams = layoutParams
            view.setBackgroundResource(R.color.lightGreyColor)

            countLinear?.addView(view)
            listOfCountViews.add(view)
        }

        showIndex(currentSelectedIndex)
    }

    private fun showIndex(index: Int) {
        resetCountViews()
        imageView.setBackgroundResource(listOfImages[index])
        listOfCountViews[index].setBackgroundResource(R.color.red)
    }

    private fun resetCountViews() {
        for (view in listOfCountViews) {
            view.setBackgroundResource(R.color.lightGreyColor)
        }
    }

    private fun toggleBottomSheet() {
        val state =
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                BottomSheetBehavior.STATE_COLLAPSED
            else
                BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.state = state
    }
}