package ru.behetem.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import it.sephiroth.android.library.rangeseekbar.RangeSeekBar
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

        val close = view.findViewById<ImageView>(R.id.close)
        close.setOnClickListener {
            dialog?.dismiss()
        }
        return view
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
        profilesViewModel.saveFilters()
    }
}