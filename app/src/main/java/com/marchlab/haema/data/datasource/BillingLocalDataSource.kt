package com.marchlab.haema.data.datasource

import com.marchlab.haema.data.preference.PreferenceStorage

interface BillingLocalDataSource {

    fun setPurchased(purchased: Boolean)

    fun checkIfPurchased(): Boolean
}

class BillingLocalDataSourceImpl(
    private val preferenceStorage: PreferenceStorage
): BillingLocalDataSource {

    override fun setPurchased(purchased: Boolean) { preferenceStorage.purchased = purchased }

    override fun checkIfPurchased(): Boolean = preferenceStorage.purchased
}