package com.marchlab.haema.domain.usecase.setting

import com.android.billingclient.api.SkuDetails
import com.marchlab.haema.domain.repository.BillingRepository
import com.marchlab.haema.domain.usecase.base.UseCase

class CheckIfPurchasedUseCase(
    private val billingRepository: BillingRepository
): UseCase<Unit, Boolean>() {

    override suspend fun execute(parameters: Unit): Boolean = billingRepository.checkIfPurchased()
}