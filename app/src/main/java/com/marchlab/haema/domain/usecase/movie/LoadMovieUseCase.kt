package com.marchlab.haema.domain.usecase.movie

import com.marchlab.haema.domain.model.Movie
import com.marchlab.haema.domain.repository.MovieRepository
import com.marchlab.haema.domain.usecase.base.FlowUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
class LoadMovieUseCase(
    private val movieRepository: MovieRepository
): FlowUseCase<Long, Movie>() {
    override suspend fun execute(parameters: Long): Flow<Movie> = movieRepository.fetch(parameters)
}