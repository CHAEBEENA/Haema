package com.marchlab.haema.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.marchlab.haema.data.database.model.JournalEntity

@Dao
interface JournalDao {

    @Query("SELECT * FROM journal ORDER BY date DESC")
    fun all(): Flow<List<JournalEntity>>

    @Query("SELECT * FROM journal")
    suspend fun snapshotAll(): List<JournalEntity>

    @Query("SELECT date FROM journal ORDER BY date DESC")
    suspend fun allDates(): List<Long>

    @Query("SELECT * FROM journal WHERE id = :id")
    suspend fun getById(id: Long): JournalEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(journal: JournalEntity)

    @Update
    suspend fun update(journal: JournalEntity)

    @Delete
    suspend fun delete(journal: JournalEntity)

    @Delete
    suspend fun delete(vararg journals: JournalEntity)

    @Query("DELETE FROM journal WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM journal")
    suspend fun deleteAll()

    @Query("SELECT * FROM journal JOIN journal_fts ON journal.id = journal_fts.docId WHERE journal_fts.content MATCH :query")
    suspend fun search(query: String): List<JournalEntity>
}