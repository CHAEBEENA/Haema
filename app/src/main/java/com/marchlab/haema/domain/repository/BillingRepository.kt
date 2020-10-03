package com.marchlab.haema.domain.repository

interface BillingRepository {

    fun setPurchased(purchased: Boolean): Boolean

    fun checkIfPurchased(): Boolean
}