package com.marchlab.haema.data.repository

import com.marchlab.haema.data.datasource.BillingLocalDataSource
import com.marchlab.haema.domain.repository.BillingRepository

class BillingRepositoryImpl(
    private val billingLocalDataSource: BillingLocalDataSource
): BillingRepository {

    override fun setPurchased(purchased: Boolean): Boolean {

        billingLocalDataSource.setPurchased(purchased)

        return billingLocalDataSource.checkIfPurchased()
    }

    override fun checkIfPurchased(): Boolean = billingLocalDataSource.checkIfPurchased()

}