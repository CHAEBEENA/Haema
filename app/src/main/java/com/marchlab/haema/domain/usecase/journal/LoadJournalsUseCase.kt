package com.marchlab.haema.domain.usecase.journal

import com.marchlab.haema.domain.model.Journal
import com.marchlab.haema.domain.repository.JournalRepository
import com.marchlab.haema.domain.usecase.base.FlowUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

/**
 * load all journals from local database
 */
@ExperimentalCoroutinesApi
class LoadJournalsUseCase(
    private val journalRepository: JournalRepository
): FlowUseCase<Unit, List<Journal>>() {

    override suspend fun execute(parameters: Unit): Flow<List<Journal>> = journalRepository.fetchAll()
}