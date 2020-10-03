package com.marchlab.haema.domain.usecase.movie

import com.marchlab.haema.domain.model.Movie
import com.marchlab.haema.domain.repository.MovieRepository
import com.marchlab.haema.domain.usecase.base.UseCase

class EditMovieUseCase(
    private val movieRepository: MovieRepository
): UseCase<Movie, Unit>(){
    override suspend fun execute(parameters: Movie) = movieRepository.update(parameters)

}