package ru.behetem.fragments

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import ru.behetem.R
import ru.behetem.activities.AllReactionsActivity
import ru.behetem.adapters.ReceivedReactionAdapter
import ru.behetem.databinding.MessengerFragmentBinding
import ru.behetem.utils.getLoggedInUserFromShared
import ru.behetem.utils.showInfoAlertDialog
import ru.behetem.utils.validateResponse
import ru.behetem.viewmodels.MessengerViewModel

class MessengerFragment : Fragment() {

    companion object {
        fun newInstance() = MessengerFragment()
    }

    private lateinit var viewModel: MessengerViewModel
    private lateinit var binding: MessengerFragmentBinding

    private var receivedReactionAdapter: ReceivedReactionAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MessengerViewModel::class.java)
        viewModel.setLoggedInUser(getLoggedInUserFromShared(requireActivity()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        viewModel.getReceivedReactionsList().observe(requireActivity(), {
            if(it != null) {
                if(receivedReactionAdapter == null) {
                    receivedReactionAdapter = ReceivedReactionAdapter(it, viewModel)
                    binding.receivedReactionsAdapter = receivedReactionAdapter
                }
                receivedReactionAdapter?.notifyDataSetChanged()
            }
        })

        viewModel.getAllClicked().observe(requireActivity(), {
            if(it) {
                viewModel.setAllClicked(false)
                openAllReactions()
            }
        })

        viewModel.getShowNoInternet().observe(requireActivity(), {
            if(it) {
                viewModel.setShowNoInternet(false)
                showInfoAlertDialog(requireActivity(), getString(R.string.no_internet))
            }
        })

        viewModel.getBaseResponse().observe(requireActivity(), {
            it?.let {
                viewModel.setBaseResponse(null)
                validateResponse(requireActivity(), it)
            }
        })
    }

    private fun openAllReactions() {
        val intent = Intent(requireActivity(), AllReactionsActivity::class.java)
        startActivity(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getReactions()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        receivedReactionAdapter = null
    }
}