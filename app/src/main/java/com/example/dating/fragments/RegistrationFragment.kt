package com.example.dating.fragments

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.dating.R
import com.example.dating.activities.ChooseGenderActivity
import com.example.dating.databinding.RegistrationFragmentBinding
import com.example.dating.models.UserModel
import com.example.dating.utils.Constants
import com.example.dating.viewmodels.RegistrationViewModel
import kotlinx.android.synthetic.main.registration_fragment.*

class RegistrationFragment : Fragment() {

    companion object {
        fun newInstance() = RegistrationFragment()
    }

    private lateinit var viewModel: RegistrationViewModel
    private lateinit var binding: RegistrationFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RegistrationViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.registration_fragment, container, false)
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
        viewModel.getMoveFurther().observe(viewLifecycleOwner, {
            if(it) {
                viewModel.setMoveFurther(false)
                moveFurther()
            }
        })
    }

    private fun moveFurther() {
        val userModel = UserModel()
        val intent = Intent(requireActivity(), ChooseGenderActivity::class.java)
        intent.putExtra(Constants.PROFILE_USER, userModel)
        startActivity(intent)
    }
}