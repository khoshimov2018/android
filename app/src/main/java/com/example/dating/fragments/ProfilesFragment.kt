package com.example.dating.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.dating.R
import com.example.dating.databinding.ProfilesFragmentBinding
import com.example.dating.utils.getLoggedInUserFromShared
import com.example.dating.utils.showInfoAlertDialog
import com.example.dating.utils.validateResponse
import com.example.dating.viewmodels.ProfilesViewModel

private const val NUM_PAGES = 5

class ProfilesFragment : Fragment() {

    companion object {
        fun newInstance() = ProfilesFragment()
    }

    private lateinit var viewModel: ProfilesViewModel
    private lateinit var binding: ProfilesFragmentBinding

    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfilesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.profiles_fragment, container, false)
        val view: View = binding.root
        initViewModel()

        viewPager = view.findViewById(R.id.pager)

        // The pager adapter, which provides the pages to the view pager widget.
        val pagerAdapter = ScreenSlidePagerAdapter(requireActivity())
        viewPager.adapter = pagerAdapter

        return view
    }

    private fun initViewModel() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        initObservers()

        viewModel.setLoggedInUser(getLoggedInUserFromShared(requireActivity()))
        viewModel.getFilters()
    }

    private fun initObservers() {
        viewModel.getFilterModelLiveData().observe(viewLifecycleOwner, {
            if(it != null) {
                viewModel.getUsers()
            }
        })

        viewModel.getShowNoInternet().observe(viewLifecycleOwner, {
            if(it) {
                viewModel.setShowNoInternet(false)
                showInfoAlertDialog(requireActivity(), getString(R.string.no_internet))
            }
        })

        viewModel.getBaseResponse().observe(viewLifecycleOwner, {
            it?.let {
                viewModel.setBaseResponse(null)
                validateResponse(requireActivity(), it)
            }
        })
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment = UserProfileFragment()
    }
}