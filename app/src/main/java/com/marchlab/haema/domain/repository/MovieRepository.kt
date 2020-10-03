package com.marchlab.haema.domain.repository

import com.marchlab.haema.data.database.model.BookEntity
import com.marchlab.haema.data.database.model.MovieEntity
import com.marchlab.haema.domain.model.Movie
import com.marchlab.haema.domain.model.MovieSearchResult
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    fun fetchAll(): Flow<List<Movie>>

    fun fetch(id: Long): Flow<Movie>

    suspend fun add(movie: Movie)

    suspend fun update(movie: Movie)

    suspend fun delete(id: Long)

    suspend fun searchMovie(query: String): List<MovieSearchResult>
}