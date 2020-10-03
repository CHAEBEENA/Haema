package com.marchlab.haema.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.observe
import com.android.billingclient.api.*
import com.marchlab.haema.R
import com.marchlab.haema.databinding.FragmentBillingBinding
import com.marchlab.haema.util.extensions.isSuccessful
import com.marchlab.haema.util.extensions.launchOnLifecycleScope
import com.marchlab.haema.util.extensions.viewBinding
import com.marchlab.haema.util.result.successOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel
import timber.log.Timber
import timber.log.debug

class BillingFragment: Fragment(R.layout.fragment_billing), BillingClientStateListener, PurchasesUpdatedListener {

    private val binding by viewBinding(FragmentBillingBinding::bind)

    private val viewModel by lifecycleScope.viewModel<BillingViewModel>(this)

    private lateinit var mBillingClient: BillingClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setupObserver()
    }

    private fun init() {
        mBillingClient = BillingClient.newBuilder(requireContext())
            .setListener(this@BillingFragment)
            .enablePendingPurchases()
            .build()

        launchOnLifecycleScope(Dispatchers.IO) { startConnection() }
    }

    private fun setupObserver() {
        viewModel.isPurchased.observe(viewLifecycleOwner) { result ->
            result.successOrNull()?.let {
                parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf())
                parentFragmentManager.commit { remove(this@BillingFragment) }
            }
        }
    }

    private fun startConnection() = mBillingClient.startConnection(this)

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        if(billingResult.isSuccessful) {
            launchOnLifecycleScope {
                val queryPurchasesResult = withContext(Dispatchers.IO) { mBillingClient.queryPurchases(BillingClient.SkuType.INAPP) }

                if(queryPurchasesResult.billingResult.isSuccessful) {
                    val purchasedList = queryPurchasesResult.purchasesList
                        ?.filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }
                        ?.filter {
                            if(!it.isAcknowledged) withContext(Dispatchers.IO) { acknowledgePurchases(it).isSuccessful }
                            else it.isAcknowledged
                        }

                    if(!purchasedList.isNullOrEmpty()) {
                        viewModel.setPurchased(true)
                        return@launchOnLifecycleScope
                    }
                }

                val querySkuDetailsResult = withContext(Dispatchers.IO) { querySkuDetails() }

                if(querySkuDetailsResult.billingResult.isSuccessful)
                    querySkuDetailsResult.skuDetailsList?.forEach { launchBillingFlow(it) }
                else {
                    Timber.debug { "querySkuDetailsResult; ${querySkuDetailsResult.billingResult.responseCode}, ${querySkuDetailsResult.billingResult.debugMessage}" }
                    dismiss("네트워크 연결에 문제가 발생하였습니다 :-(")
                }

            }
        } else {
            Timber.debug { "onBillingSetupFinished; ${billingResult.responseCode}, ${billingResult.debugMessage}" }
            dismiss("네트워크 연결에 문제가 발생하였습니다 :-(")
        }
    }

    override fun onBillingServiceDisconnected() {
        Timber.debug { "onBillingServiceDisconnected;" }
        dismiss("네트워크 연결에 문제가 발생하였습니다 :-(")
    }

    private fun dismiss(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
        parentFragmentManager.commit { remove(this@BillingFragment) }
    }

    private suspend fun querySkuDetails(): SkuDetailsResult {
        val params = SkuDetailsParams.newBuilder()
            .setType(BillingClient.SkuType.INAPP)
            .setSkusList(listOf(getString(R.string.sku_id)))
            .build()

        return mBillingClient.querySkuDetails(params)
    }

    private fun launchBillingFlow(skuDetails: SkuDetails) {
        val params = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()

        mBillingClient.launchBillingFlow(requireActivity(), params)
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        when(billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                launchOnLifecycleScope(Dispatchers.IO) {
                    purchases
                        ?.filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }
                        ?.filter {
                            if(!it.isAcknowledged) withContext(Dispatchers.IO) { acknowledgePurchases(it).isSuccessful }
                            else it.isAcknowledged
                        }
                        ?.let {
                            if(it.isNotEmpty()) viewModel.setPurchased(true)
                            else dismiss("일시적인 오류가 발생하였습니다 :(")
                        }
                }
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                launchOnLifecycleScope {
                    val queryPurchasesResult = withContext(Dispatchers.IO) { mBillingClient.queryPurchases(BillingClient.SkuType.INAPP) }

                    if(queryPurchasesResult.billingResult.isSuccessful) {
                        val purchasedList = queryPurchasesResult.purchasesList
                            ?.filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }
                            ?.filter {
                                if(!it.isAcknowledged) withContext(Dispatchers.IO) { acknowledgePurchases(it).isSuccessful }
                                else it.isAcknowledged
                            }

                        if(!purchasedList.isNullOrEmpty()) viewModel.setPurchased(true)
                        else dismiss("이미 구매한 상품입니다 :-)")

                    } else
                        dismiss("이미 구매한 상품입니다 :-)")
                }
            }
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {
                launchOnLifecycleScope(Dispatchers.IO) { startConnection() }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                parentFragmentManager.commit { remove(this@BillingFragment) }
            }
            else -> {
                Timber.debug { "onPurchasesUpdated; ${billingResult.responseCode}, ${billingResult.debugMessage}" }
                dismiss("일시적인 오류가 발생하였습니다 :(")
            }
        }
    }

    private suspend fun acknowledgePurchases(purchase: Purchase): BillingResult {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        return mBillingClient.acknowledgePurchase(params)
    }

    companion object {
        const val REQUEST_KEY = "BILLING_REQUEST_KEY"
    }
}
