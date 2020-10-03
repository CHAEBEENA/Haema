package com.marchlab.haema.domain.usecase.journal

import com.marchlab.haema.domain.repository.JournalRepository
import com.marchlab.haema.domain.usecase.base.FlowUseCase
import com.marchlab.haema.domain.usecase.base.UseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate

@ExperimentalCoroutinesApi
class LoadAllDatesUseCase(
    private val journalRepository: JournalRepository
): UseCase<Unit, List<LocalDate>>() {

    override suspend fun execute(parameters: Unit): List<LocalDate> = journalRepository.fetchAllDates()
}