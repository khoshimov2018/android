package com.example.dating.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.dating.R
import com.example.dating.databinding.LoginFragmentBinding
import com.example.dating.databinding.ProfilesFragmentBinding
import com.example.dating.viewmodels.ProfilesViewModel

class ProfilesFragment : Fragment() {

    companion object {
        fun newInstance() = ProfilesFragment()
    }

    private lateinit var viewModel: ProfilesViewModel
    private lateinit var binding: ProfilesFragmentBinding

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

        return view
    }

    private fun initViewModel() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        initObservers()
    }

    private fun initObservers() {

    }
}