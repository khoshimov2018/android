package ru.behetem.fragments

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_my_profile_detail.*
import kotlinx.android.synthetic.main.user_profile_fragment.*
import kotlinx.android.synthetic.main.user_profile_fragment.bottomSheet
import kotlinx.android.synthetic.main.user_profile_fragment.countLinear
import kotlinx.android.synthetic.main.user_profile_fragment.imageView
import kotlinx.android.synthetic.main.user_profile_fragment.mainLayout
import ru.behetem.R
import ru.behetem.adapters.InterestsAdapter
import ru.behetem.adapters.ReactionsAdapter
import ru.behetem.databinding.UserProfileFragmentBinding
import ru.behetem.models.UserModel
import ru.behetem.utils.dpToPx
import ru.behetem.viewmodels.UserProfileViewModel

private const val CURRENT_USER = "CURRENT_USER"

class UserProfileFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance(userModel: UserModel) =
            UserProfileFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(CURRENT_USER, userModel)
                }
            }
    }

    private lateinit var viewModel: UserProfileViewModel
    private lateinit var binding: UserProfileFragmentBinding

    private val listOfCountViews: MutableList<View> = ArrayList()
    private lateinit var listOfImages: MutableList<String>
    private var currentSelectedIndex = -1

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private var interestsAdapter: InterestsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(UserProfileViewModel::class.java)
        arguments?.let {
            val currentUser = it.getParcelable<UserModel>(CURRENT_USER)
            currentUser?.let { user ->
                viewModel.setCurrentUser(user)
                user.images?.let { images ->
                    listOfImages = images
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        setInterests()
        setReactions()
    }

    private fun initObservers() {

    }

    private fun setInterests() {
        interestsAdapter = InterestsAdapter(viewModel.getInterestsList(), viewModel)
        binding.interestsAdapter = interestsAdapter
        interestsAdapter?.notifyDataSetChanged()
    }

    private fun setReactions(){
        val reactionsList = viewModel.getReactionsList()
        reactionsList?.let {
            val reactionsAdapter = ReactionsAdapter(it)
            binding.reactionsAdapter = reactionsAdapter
            reactionsAdapter.notifyDataSetChanged()
        }
    }

    private fun initView() {
        if(this::listOfImages.isInitialized) {
            for (index in 0 until listOfImages.size) {
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

            if(listOfImages.size > 0) {
                currentSelectedIndex = 0
                showIndex(currentSelectedIndex)
            }
        }
    }

    private fun showIndex(index: Int) {
        resetCountViews()
//        imageView.setBackgroundResource(listOfImages[index])
        Glide.with(this)
            .load(listOfImages[index])
            .placeholder(R.drawable.logo)
            .into(imageView);
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