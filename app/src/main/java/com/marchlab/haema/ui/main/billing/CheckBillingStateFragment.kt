package com.marchlab.haema.ui.main.billing

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.observe
import com.android.billingclient.api.*
import com.marchlab.haema.R
import com.marchlab.haema.util.extensions.isSuccessful
import com.marchlab.haema.util.extensions.launchOnLifecycleScope
import com.marchlab.haema.util.result.successOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel
import timber.log.Timber
import timber.log.debug

class CheckBillingStateFragment: Fragment(R.layout.fragment_check_billing_state), BillingClientStateListener, PurchasesUpdatedListener {

    private lateinit var mBillingClient: BillingClient

    private val viewModel by lifecycleScope.viewModel<CheckBillingStateViewModel>(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setupObserver()
    }

    private fun init() {
        launchOnLifecycleScope(Dispatchers.IO) {
            initializeBillingClient()
            startConnection()
        }
    }

    private fun setupObserver() {
        viewModel.isPurchased.observe(viewLifecycleOwner) {
            it.successOrNull()
                ?.let { parentFragmentManager.commit { remove(this@CheckBillingStateFragment) } }
        }
    }

    private fun initializeBillingClient() {
        mBillingClient = BillingClient.newBuilder(requireContext())
            .setListener(this)
            .enablePendingPurchases()
            .build()
    }

    private fun startConnection() = mBillingClient.startConnection(this@CheckBillingStateFragment)

    override fun onBillingServiceDisconnected() = parentFragmentManager.commit { remove(this@CheckBillingStateFragment) }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        launchOnLifecycleScope {
            if(billingResult.isSuccessful) {
                val queryPurchasesResult = withContext(Dispatchers.IO) { mBillingClient.queryPurchases(BillingClient.SkuType.INAPP) }

                if(queryPurchasesResult.billingResult.isSuccessful) {
                    val purchasedList = queryPurchasesResult.purchasesList
                        ?.filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }
                        ?.filter {
                            if(!it.isAcknowledged) withContext(Dispatchers.IO) { acknowledgePurchase(it).isSuccessful }
                            else it.isAcknowledged
                        }

                    if(!purchasedList.isNullOrEmpty()) {
                        viewModel.setPurchased(true)
                        return@launchOnLifecycleScope
                    }
                } else
                    parentFragmentManager.commit { remove(this@CheckBillingStateFragment) }
            } else
                parentFragmentManager.commit { remove(this@CheckBillingStateFragment) }
        }
    }

    private suspend fun acknowledgePurchase(purchase: Purchase): BillingResult {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        return mBillingClient.acknowledgePurchase(params)
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        Timber.debug { "check billing state fragment; onPurchasesUpdated purchases = $purchases" }
    }
}