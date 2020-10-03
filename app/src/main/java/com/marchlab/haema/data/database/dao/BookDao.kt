package com.marchlab.haema.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.marchlab.haema.data.database.model.BookEntity

@Dao
interface BookDao {
    @Query("SELECT * FROM book ORDER BY date DESC")
    fun all(): Flow<List<BookEntity>>

    @Query("SELECT * FROM book")
    suspend fun snapshotAll(): List<BookEntity>

    @Query("SELECT * FROM book WHERE id = :id")
    fun getById(id: Long): Flow<BookEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(book: BookEntity)

    @Update
    suspend fun update(book: BookEntity)

    @Query("DELETE FROM book WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Delete
    suspend fun delete(book: BookEntity)

    @Delete
    suspend fun delete(vararg books: BookEntity)

    @Query("DELETE FROM book")
    suspend fun deleteAll()

    @Query("SELECT * FROM book JOIN book_fts ON book.id = book_fts.docId WHERE book_fts MATCH :query")
    suspend fun query(query: String): List<BookEntity>
}