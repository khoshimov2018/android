package ru.behetem.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.behetem.R
import ru.behetem.databinding.ActivityReceivedReactionDetailBinding
import ru.behetem.models.ReactionModel
import ru.behetem.models.UserModel
import ru.behetem.utils.Constants
import ru.behetem.utils.getLoggedInUserFromShared
import ru.behetem.utils.showInfoAlertDialog
import ru.behetem.utils.validateResponse
import ru.behetem.viewmodels.JobViewModel
import ru.behetem.viewmodels.ReceivedReactionDetailViewModel

class ReceivedReactionDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceivedReactionDetailBinding
    private lateinit var receivedReactionDetailViewModel: ReceivedReactionDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_received_reaction_detail)
        binding.lifecycleOwner = this
        initViewModel()
        receivedReactionDetailViewModel.getUserDetail()
    }

    private fun initViewModel() {
        receivedReactionDetailViewModel = ViewModelProvider(this).get(ReceivedReactionDetailViewModel::class.java)
        binding.viewModel = receivedReactionDetailViewModel

        val loggedInUser = getLoggedInUserFromShared(this)
        receivedReactionDetailViewModel.setLoggedInUser(loggedInUser)

        val receivedReaction = intent.getParcelableExtra<ReactionModel>(Constants.RECEIVED_REACTION)
        receivedReaction?.let {
            receivedReactionDetailViewModel.setReceivedReaction(it)
        }

        initObservers()
    }

    private fun initObservers() {
        receivedReactionDetailViewModel.getBackButtonClicked().observe(this, Observer { isPressed: Boolean ->
            if (isPressed) {
                this.onBackPressed()
            }
        })

        receivedReactionDetailViewModel.getShowNoInternet().observe(this, {
            if (it) {
                receivedReactionDetailViewModel.setShowNoInternet(false)
                showInfoAlertDialog(this, getString(R.string.no_internet))
            }
        })

        receivedReactionDetailViewModel.getBaseResponse().observe(this, {
            it?.let {
                receivedReactionDetailViewModel.setBaseResponse(null)
                validateResponse(this, it)
            }
        })
    }
}