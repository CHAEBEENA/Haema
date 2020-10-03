package com.marchlab.haema.domain.usecase.movie

import com.marchlab.haema.domain.model.Movie
import com.marchlab.haema.domain.repository.MovieRepository
import com.marchlab.haema.domain.usecase.base.UseCase

class DeleteMovieUseCase(
    private val movieRepository: MovieRepository
): UseCase<Long, Unit>() {
    override suspend fun execute(parameters: Long) = movieRepository.delete(parameters)

}