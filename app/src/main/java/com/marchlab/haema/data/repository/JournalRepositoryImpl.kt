package com.marchlab.haema.data.repository

import android.net.Uri
import com.marchlab.haema.data.database.model.JournalEntity
import kotlinx.coroutines.flow.Flow
import com.marchlab.haema.data.datasource.JournalLocalDataSource
import com.marchlab.haema.domain.model.Emotion
import com.marchlab.haema.domain.model.Journal
import com.marchlab.haema.domain.repository.JournalRepository
import kotlinx.coroutines.flow.map
import org.threeten.bp.LocalDate

class JournalRepositoryImpl(
    private val journalLocalDataSource: JournalLocalDataSource
): JournalRepository {

    override suspend fun add(journal: Journal) = journalLocalDataSource.add(journal.transform())

    override suspend fun update(journal: Journal) = journalLocalDataSource.update(journal.transform())

    override suspend fun deleteById(id: Long) = journalLocalDataSource.deleteById(id)

    override fun fetchAll(): Flow<List<Journal>> = journalLocalDataSource.fetchAll().map { journals -> journals.map { it.transform() } }

    override suspend fun fetchAllDates(): List<LocalDate> = journalLocalDataSource.fetchAllDates().map { LocalDate.ofEpochDay(it) }

    override suspend fun fetch(id: Long): Journal = journalLocalDataSource.fetch(id).transform()

    /* TODO mapper */
    private fun Journal.transform() = JournalEntity(id, emotion.state, date.toEpochDay(), imageUri?.toString(), content, createdAt, System.currentTimeMillis())

    private fun JournalEntity.transform() = Journal(id, Emotion.valueOf(emotion), imageUri?.let { Uri.parse(it) }, content, LocalDate.ofEpochDay(date), createdAt)
}