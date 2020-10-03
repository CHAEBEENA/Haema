package com.marchlab.haema.data.repository

import com.marchlab.haema.data.apis.model.naver.NMovie
import com.marchlab.haema.data.database.model.MovieEntity
import com.marchlab.haema.data.datasource.MovieLocalDataSource
import com.marchlab.haema.data.datasource.MovieRemoteDataSource
import com.marchlab.haema.domain.model.Movie
import com.marchlab.haema.domain.model.MovieSearchResult
import com.marchlab.haema.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.threeten.bp.LocalDate

class MovieRepositoryImpl(
    private val movieLocalDataSource: MovieLocalDataSource,
    private val movieRemoteDataSource: MovieRemoteDataSource
) : MovieRepository {

    override fun fetchAll(): Flow<List<Movie>> = movieLocalDataSource.fetchAll().map{ movies -> movies.map { it.transform() } }

    override fun fetch(id: Long): Flow<Movie> = movieLocalDataSource.fetch(id).map { it.transform() }

    private fun MovieEntity.transform() = Movie(id, posterUrl, title, release, directors, actors, LocalDate.ofEpochDay(date), place, people, rate, review)

    override suspend fun add(movie: Movie) { movieLocalDataSource.add(movie.transform()) }

    private fun Movie.transform() = MovieEntity(id, posterUrl, title, release, directors, actors, date.toEpochDay(), place, people, rate, review, System.currentTimeMillis(), System.currentTimeMillis())

    override suspend fun update(movie: Movie) { movieLocalDataSource.update(movie.transform()) }

    override suspend fun delete(id: Long) { movieLocalDataSource.delete(id) }

    override suspend fun searchMovie(query: String): List<MovieSearchResult> {

        val response = movieRemoteDataSource.searchMovie(query)

        return if(response.items.isNullOrEmpty()) emptyList()
        else response.items.filter { it.userRating != 0.toFloat() }.map { it.transform() }

    }

    private fun NMovie.transform() = MovieSearchResult(title, image, subtitle, pubDate, director, actor)
}