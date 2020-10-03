package com.marchlab.haema.domain.repository

import com.marchlab.haema.domain.model.Journal
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate

interface JournalRepository {

    suspend fun add(journal: Journal)

    suspend fun update(journal: Journal)

    suspend fun deleteById(id: Long)

    fun fetchAll(): Flow<List<Journal>>

    suspend fun fetchAllDates(): List<LocalDate>

    suspend fun fetch(id: Long): Journal
}