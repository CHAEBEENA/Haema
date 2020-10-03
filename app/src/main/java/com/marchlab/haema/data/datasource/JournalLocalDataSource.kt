package com.marchlab.haema.data.datasource

import com.marchlab.haema.data.database.dao.JournalDao
import com.marchlab.haema.data.database.model.JournalEntity
import kotlinx.coroutines.flow.Flow

interface JournalLocalDataSource {

    suspend fun add(journal: JournalEntity)

    suspend fun update(journal: JournalEntity)

    suspend fun deleteById(id: Long)

    fun fetchAll(): Flow<List<JournalEntity>>

    suspend fun fetchAllDates(): List<Long>

    suspend fun fetch(id: Long): JournalEntity
}

class JournalLocalDataSourceImpl(
    private val journalDao: JournalDao
): JournalLocalDataSource {

    override suspend fun add(journal: JournalEntity) = journalDao.add(journal)

    override suspend fun update(journal: JournalEntity) = journalDao.update(journal)

    override suspend fun deleteById(id: Long) = journalDao.deleteById(id)

    override fun fetchAll(): Flow<List<JournalEntity>> = journalDao.all()

    override suspend fun fetchAllDates(): List<Long> = journalDao.allDates()

    override suspend fun fetch(id: Long): JournalEntity = journalDao.getById(id)
}