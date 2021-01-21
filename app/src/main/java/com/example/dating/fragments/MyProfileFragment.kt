package com.example.dating.fragments

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.dating.R
import com.example.dating.activities.*
import com.example.dating.databinding.MyProfileFragmentBinding
import com.example.dating.utils.Constants
import com.example.dating.utils.getLoggedInUserFromShared
import com.example.dating.utils.showInfoAlertDialog
import com.example.dating.utils.validateResponse
import com.example.dating.viewmodels.LoginViewModel
import com.example.dating.viewmodels.MyProfileViewModel

class MyProfileFragment : Fragment() {

    companion object {
        fun newInstance() = MyProfileFragment()
    }

    private lateinit var viewModel: MyProfileViewModel
    private lateinit var binding: MyProfileFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MyProfileViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.my_profile_fragment, container, false)
        val view: View = binding.root
        initViewModel()

        return view
    }

    private fun initViewModel() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.setLoggedInUser(getLoggedInUserFromShared(requireActivity()))

        initObservers()
    }

    private fun initObservers() {
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

        viewModel.getMoveToSettings().observe(viewLifecycleOwner, {
            if(it) {
                viewModel.setMoveToSettings(false)
                moveToSettings()
            }
        })

        viewModel.getMoveToProfile().observe(viewLifecycleOwner, {
            if(it) {
                viewModel.setMoveToProfile(false)
                moveToProfile()
            }
        })

        viewModel.getMoveToCoins().observe(viewLifecycleOwner, {
            if(it) {
                viewModel.setMoveToCoins(false)
                moveToCoins()
            }
        })

        viewModel.getMoveToPremium().observe(viewLifecycleOwner, {
            if(it) {
                viewModel.setMoveToPremium(false)
                moveToPremium()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUserProfile()
    }

    private fun moveToSettings() {
        val intent = Intent(requireActivity(), SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun moveToProfile() {
        val intent = Intent(requireActivity(), MyProfileDetailActivity::class.java)
        intent.putExtra(Constants.PROFILE_USER, viewModel.getCurrentUser())

        var arrayList: ArrayList<String>? = null
        if(viewModel.getImages() != null) {
            arrayList = ArrayList(viewModel.getImages()!!)
        }

        intent.putStringArrayListExtra(Constants.USER_IMAGES, arrayList)
        startActivity(intent)
    }

    private fun moveToCoins() {
        val intent = Intent(requireActivity(), CoinsActivity::class.java)
        startActivity(intent)
    }

    private fun moveToPremium() {
        val intent = Intent(requireActivity(), PremiumActivity::class.java)
        startActivity(intent)
    }
}