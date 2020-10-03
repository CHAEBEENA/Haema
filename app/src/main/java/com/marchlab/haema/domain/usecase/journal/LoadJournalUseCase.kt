package com.marchlab.haema.domain.usecase.journal

import com.marchlab.haema.domain.model.Journal
import com.marchlab.haema.domain.repository.JournalRepository
import com.marchlab.haema.domain.usecase.base.UseCase

class LoadJournalUseCase(
    private val journalRepository: JournalRepository
): UseCase<Long, Journal>() {

    override suspend fun execute(parameters: Long): Journal = journalRepository.fetch(parameters)
}