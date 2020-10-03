package com.marchlab.haema.domain.usecase.journal

import com.marchlab.haema.domain.model.Journal
import com.marchlab.haema.domain.repository.JournalRepository
import com.marchlab.haema.domain.usecase.base.UseCase

class DeleteJournalUseCase(
    private val journalRepository: JournalRepository
): UseCase<Long, Unit>() {

    override suspend fun execute(parameters: Long) = journalRepository.deleteById(parameters)
}