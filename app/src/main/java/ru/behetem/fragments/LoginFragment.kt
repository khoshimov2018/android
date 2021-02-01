package ru.behetem.fragments

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import ru.behetem.R
import ru.behetem.activities.ForgotPasswordActivity
import ru.behetem.activities.HomeActivity
import ru.behetem.databinding.LoginFragmentBinding
import ru.behetem.viewmodels.LoginViewModel

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: LoginFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.login_fragment, container, false)
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
        viewModel.getMoveToHome().observe(viewLifecycleOwner, {
            if (it) {
                viewModel.setMoveToHome(false)
                moveToHome()
            }
        })

        viewModel.getMoveToForgotPassword().observe(viewLifecycleOwner, {
            if (it) {
                viewModel.setMoveToForgotPassword(false)
                moveToForgotPassword()
            }
        })
    }

    private fun moveToHome() {
        val intent = Intent(requireActivity(), HomeActivity::class.java)
        startActivity(intent)
        ActivityCompat.finishAffinity(requireActivity())
    }

    private fun moveToForgotPassword() {
        val intent = Intent(requireActivity(), ForgotPasswordActivity::class.java)
        startActivity(intent)
    }
}