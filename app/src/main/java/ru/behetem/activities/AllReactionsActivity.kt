package ru.behetem.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.behetem.R
import ru.behetem.adapters.BigReceivedReactionAdapter
import ru.behetem.adapters.ReceivedReactionAdapter
import ru.behetem.databinding.ActivityAllReactionsBinding
import ru.behetem.utils.getLoggedInUserFromShared
import ru.behetem.utils.showInfoAlertDialog
import ru.behetem.utils.validateResponse
import ru.behetem.viewmodels.AllReactionsViewModel
import ru.behetem.viewmodels.ChangePasswordViewModel

class AllReactionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllReactionsBinding
    private lateinit var allReactionsViewModel: AllReactionsViewModel

    private var bigReceivedReactionAdapter: BigReceivedReactionAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_all_reactions)
        binding.lifecycleOwner = this
        initViewModel()
    }

    private fun initViewModel() {
        allReactionsViewModel = ViewModelProvider(this).get(AllReactionsViewModel::class.java)
        binding.viewModel = allReactionsViewModel

        allReactionsViewModel.setLoggedInUser(getLoggedInUserFromShared(this))

        initObservers()
    }

    override fun onResume() {
        super.onResume()
        allReactionsViewModel.getReactions()
    }

    override fun onPause() {
        super.onPause()
        bigReceivedReactionAdapter = null
    }

    private fun initObservers() {
        allReactionsViewModel.getReceivedReactionsList().observe(this, {
            if(it != null) {
                if(bigReceivedReactionAdapter == null) {
                    bigReceivedReactionAdapter = BigReceivedReactionAdapter(it, allReactionsViewModel)
                    binding.bigReceivedReactionAdapter = bigReceivedReactionAdapter
                }
                bigReceivedReactionAdapter?.notifyDataSetChanged()
            }
        })

        allReactionsViewModel.getBackButtonClicked()
            .observe(this, Observer { isPressed: Boolean ->
                if (isPressed) {
                    this.onBackPressed()
                }
            })

        allReactionsViewModel.getShowNoInternet().observe(this, {
            if(it) {
                allReactionsViewModel.setShowNoInternet(false)
                showInfoAlertDialog(this, getString(R.string.no_internet))
            }
        })

        allReactionsViewModel.getBaseResponse().observe(this, {
            it?.let {
                allReactionsViewModel.setBaseResponse(null)
                validateResponse(this, it)
            }
        })
    }
}