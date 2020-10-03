package com.marchlab.haema.domain.usecase.setting

import com.marchlab.haema.domain.repository.AppLockRepository
import com.marchlab.haema.domain.usecase.base.UseCase


class CheckAppLockUseCase (
    private val appLockRepository: AppLockRepository
): UseCase<Unit, Boolean>() {
    override suspend fun execute(parameters: Unit): Boolean = appLockRepository.checkAppLock()
}