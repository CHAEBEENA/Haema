package com.marchlab.haema.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.marchlab.haema.data.database.model.MovieEntity

@Dao
interface MovieDao {

    @Query("SELECT * FROM movie ORDER BY date DESC")
    fun all(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movie WHERE id = :id")
    fun getById(id: Long): Flow<MovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(movie: MovieEntity)

    @Update
    suspend fun update(movie: MovieEntity)

    @Query("DELETE FROM movie WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM movie")
    suspend fun deleteAll()

  /*  @Query("SELECT * FROM movie JOIN movie_fts ON movie.id = movie_fts.docId WHERE movie_fts MATCH :query")
    suspend fun query(query: String): List<DiaryEntity>*/
}