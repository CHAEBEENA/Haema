package com.marchlab.haema.domain.usecase.setting

import com.marchlab.haema.domain.repository.AppLockRepository
import com.marchlab.haema.domain.usecase.base.UseCase

class RemovePasswordUseCase (
    private val appLockRepository: AppLockRepository
): UseCase<Unit, Unit>() {
    override suspend fun execute(parameters: Unit) {
        appLockRepository.removePassword()
    }
}