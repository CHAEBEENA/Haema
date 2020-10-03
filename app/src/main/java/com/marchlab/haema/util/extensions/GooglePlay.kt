package com.marchlab.haema.util.extensions

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase

val BillingResult.isSuccessful: Boolean get() = responseCode == BillingClient.BillingResponseCode.OK