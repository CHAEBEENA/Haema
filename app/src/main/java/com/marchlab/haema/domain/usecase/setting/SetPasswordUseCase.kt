package com.marchlab.haema.domain.usecase.setting

import com.marchlab.haema.domain.repository.AppLockRepository
import com.marchlab.haema.domain.usecase.base.UseCase

class SetPasswordUseCase (
    private val appLockRepository: AppLockRepository
): UseCase<String, Unit>() {
    override suspend fun execute(parameters: String) {
        appLockRepository.setPassword(parameters)
    }
}