package com.marchlab.haema.domain.usecase.setting

import com.marchlab.haema.domain.repository.BillingRepository
import com.marchlab.haema.domain.usecase.base.UseCase

class SetPurchasedUseCase(
    private val billingRepository: BillingRepository
): UseCase<Boolean, Boolean>() {

    override suspend fun execute(parameters: Boolean): Boolean = billingRepository.setPurchased(parameters)
}