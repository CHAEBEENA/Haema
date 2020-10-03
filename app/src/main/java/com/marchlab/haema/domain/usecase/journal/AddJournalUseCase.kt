package com.marchlab.haema.domain.usecase.journal

import com.marchlab.haema.domain.model.Journal
import com.marchlab.haema.domain.repository.JournalRepository
import com.marchlab.haema.domain.usecase.base.UseCase

class AddJournalUseCase(
    private val journalRepository: JournalRepository
): UseCase<Journal, Unit>() {

    override suspend fun execute(parameters: Journal) = journalRepository.add(parameters)
}