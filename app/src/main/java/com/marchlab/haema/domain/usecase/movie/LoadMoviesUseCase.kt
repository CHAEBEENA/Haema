package com.marchlab.haema.domain.usecase.movie

import com.marchlab.haema.domain.model.Movie
import com.marchlab.haema.domain.repository.MovieRepository
import com.marchlab.haema.domain.usecase.base.FlowUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

/**
 * load all movies from local database
 */
@ExperimentalCoroutinesApi
class LoadMoviesUseCase(
    private val movieRepository: MovieRepository
): FlowUseCase<Unit, List<Movie>>() {

    override suspend fun execute(parameters: Unit): Flow<List<Movie>> = movieRepository.fetchAll()
}

