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
        initObservers()
    }

    private fun initObservers() {
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

    private fun moveToSettings() {
        val intent = Intent(requireActivity(), SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun moveToProfile() {
        val intent = Intent(requireActivity(), MyProfileDetailActivity::class.java)
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