package com.marchlab.haema.domain.usecase.setting

import com.marchlab.haema.domain.repository.AppLockRepository
import com.marchlab.haema.domain.usecase.base.UseCase

class GetPasswordUseCase (
    private val appLockRepository: AppLockRepository
): UseCase<Unit, String?>() {
    override suspend fun execute(parameters: Unit): String? = appLockRepository.getPassword()
}