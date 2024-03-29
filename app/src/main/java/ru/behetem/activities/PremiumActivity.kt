package ru.behetem.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.billingclient.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.behetem.R
import ru.behetem.databinding.ActivityPremiumBinding
import ru.behetem.utils.printLog
import ru.behetem.utils.showInfoAlertDialog
import ru.behetem.viewmodels.PremiumViewModel

class PremiumActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPremiumBinding
    private lateinit var premiumViewModel: PremiumViewModel
    private lateinit var billingClient: BillingClient
    private var skuDetailsList: MutableList<SkuDetails>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_premium)
        binding.lifecycleOwner = this
        initViewModel()
        initBilling()
    }

    private fun initViewModel() {
        premiumViewModel = ViewModelProvider(this).get(PremiumViewModel::class.java)
        binding.viewModel = premiumViewModel

        initObservers()
    }

    private fun initObservers() {
        premiumViewModel.getContinueClicked().observe(this, {
            if (it) {
                premiumViewModel.setContinueClicked(false)
                initiatePurchaseFlow(premiumViewModel.getCurrentPlan())
            }
        })

        premiumViewModel.getBackButtonClicked().observe(this, { isPressed: Boolean ->
            if (isPressed) {
                this.onBackPressed()
            }
        })
    }

    private fun initBilling() {
        val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
            // To be implemented in a later section.
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    handlePurchase(purchase)
                }
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
            } else {
                // Handle any other error codes.
            }
        }

        billingClient = BillingClient.newBuilder(this)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    querySkuDetails()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    fun querySkuDetails() {
        val skuList = ArrayList<String>()
        skuList.add("1_month")
        skuList.add("3_months")
        skuList.add("6_months")
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)
        billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
            // Process the result.
            this.skuDetailsList = skuDetailsList
            printLog("details $skuDetailsList")
            /*if (skuDetailsList != null) {
                for(skuDetail in skuDetailsList) {
                    printLog("sku detail $skuDetail")
                }
            }*/
        }
    }

    private fun initiatePurchaseFlow(plan: Int) {
        when (plan) {
            0 -> {
                startFlow(1)
            }
            1 -> {
                startFlow(0)
            }
            2 -> {
                startFlow(2)
            }
            else -> {
                showInfoAlertDialog(this, getString(R.string.something_went_wrong))
            }
        }
    }

    private fun startFlow(index: Int) {
        val skuDetails = skuDetailsList?.get(index)
        if (skuDetails != null) {
            val flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build()
            val responseCode = billingClient.launchBillingFlow(this, flowParams).responseCode
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                /*val ackPurchaseResult = withContext(Dispatchers.IO) {
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams.build())
                }*/
            }
        }
    }
}