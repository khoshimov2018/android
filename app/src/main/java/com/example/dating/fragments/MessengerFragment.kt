package com.example.dating.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.dating.R
import com.example.dating.databinding.MessengerFragmentBinding
import com.example.dating.viewmodels.MessengerViewModel

class MessengerFragment : Fragment() {

    companion object {
        fun newInstance() = MessengerFragment()
    }

    private lateinit var viewModel: MessengerViewModel
    private lateinit var binding: MessengerFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MessengerViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.messenger_fragment, container, false)
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