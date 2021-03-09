package ru.behetem.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import it.sephiroth.android.library.rangeseekbar.RangeSeekBar
import kotlinx.android.synthetic.main.dialog_fragment_filters.*
import ru.behetem.BR
import ru.behetem.R
import ru.behetem.adapters.InterestsAdapter
import ru.behetem.databinding.DialogFragmentFiltersBinding
import ru.behetem.utils.Constants
import ru.behetem.utils.showInfoAlertDialog
import ru.behetem.viewmodels.ProfilesViewModel

class FiltersDialogFragment(private val profilesViewModel: ProfilesViewModel) : DialogFragment() {

    private lateinit var binding: DialogFragmentFiltersBinding
    private var interestsAdapter: InterestsAdapter? = null
    private lateinit var ageSeekBar: RangeSeekBar
    private lateinit var growthSeekBar: RangeSeekBar
    private lateinit var weightSeekBar: RangeSeekBar

    private lateinit var growthWeightBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.dialog_fragment_filters, container, true) as DialogFragmentFiltersBinding

        val view = binding.root
        initViewModel()
        addListeners(view)

        return view
    }

    private fun addListeners(view: View) {
        ageSeekBar = view.findViewById<RangeSeekBar>(R.id.ageSeekBar)
        ageSeekBar.setOnRangeSeekBarChangeListener(object: RangeSeekBar.OnRangeSeekBarChangeListener {
            override fun onProgressChanged(seekBar: RangeSeekBar?, progressStart: Int, progressEnd: Int, fromUser: Boolean) {
                val start = progressStart + Constants.MIN_AGE_FOR_CAL
                val end = progressEnd + Constants.MIN_AGE_FOR_CAL

                profilesViewModel.updateAgeFromAndTo(start, end)
            }

            override fun onStartTrackingTouch(seekBar: RangeSeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: RangeSeekBar?) {

            }
        })

        growthSeekBar = view.findViewById<RangeSeekBar>(R.id.growthSeekBar)
        growthSeekBar.setOnRangeSeekBarChangeListener(object: RangeSeekBar.OnRangeSeekBarChangeListener {
            override fun onProgressChanged(seekBar: RangeSeekBar?, progressStart: Int, progressEnd: Int, fromUser: Boolean) {
                profilesViewModel.updateGrowthFromAndTo(progressStart, progressEnd)
            }

            override fun onStartTrackingTouch(seekBar: RangeSeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: RangeSeekBar?) {

            }
        })

        weightSeekBar = view.findViewById<RangeSeekBar>(R.id.weightSeekBar)
        weightSeekBar.setOnRangeSeekBarChangeListener(object: RangeSeekBar.OnRangeSeekBarChangeListener {
            override fun onProgressChanged(seekBar: RangeSeekBar?, progressStart: Int, progressEnd: Int, fromUser: Boolean) {
                profilesViewModel.updateWeightFromAndTo(progressStart, progressEnd)
            }

            override fun onStartTrackingTouch(seekBar: RangeSeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: RangeSeekBar?) {

            }
        })

        val bottomSheetForGrowthWeight = view.findViewById<LinearLayout>(R.id.bottomSheetForGrowthWeight)
        growthWeightBottomSheetBehavior = BottomSheetBehavior.from(bottomSheetForGrowthWeight)
        growthWeightBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                    bg.visibility = View.GONE
                else
                    bg.visibility = View.VISIBLE
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

        val bg = view.findViewById<View>(R.id.bg);
        bg.setOnClickListener {
            growthWeightBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        val close = view.findViewById<ImageView>(R.id.close)
        close.setOnClickListener {
            dialog?.dismiss()
        }
    }

    private fun initViewModel() {
        binding.lifecycleOwner = this
        binding.setVariable(BR.viewModel, profilesViewModel)

        profilesViewModel.getInterestsList().observe(viewLifecycleOwner, {
            if(it != null) {
                if(it.isEmpty()) {
                    showInfoAlertDialog(requireActivity(), getString(R.string.no_interests))
                } else {
                    interestsAdapter = InterestsAdapter(it, profilesViewModel)
                    binding.setVariable(BR.interestsAdapter, interestsAdapter)
                    interestsAdapter?.notifyDataSetChanged()
                }
            } else {
                interestsAdapter = null
            }
        })

        profilesViewModel.getShowGrowthWeightBottomSheet().observe(viewLifecycleOwner, {
            if (it) {
                profilesViewModel.setShowGrowthWeightBottomSheet(false)
                growthWeightBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val matchParent = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.setLayout(matchParent, matchParent)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.FilterDialogAnimation
    }

    override fun onDestroyView() {
        super.onDestroyView()
        profilesViewModel.resetPage()
        profilesViewModel.saveFilters()
    }
}