package com.marchlab.haema.domain.usecase.movie

import com.marchlab.haema.data.repository.MovieRepositoryImpl
import com.marchlab.haema.domain.model.MovieSearchResult
import com.marchlab.haema.domain.repository.MovieRepository
import com.marchlab.haema.domain.usecase.base.UseCase

typealias Query = String

class SearchMovieUseCase(
    private val movieRepository: MovieRepository
): UseCase<Query, List<MovieSearchResult>>() {

    override suspend fun execute(parameters: Query): List<MovieSearchResult> = movieRepository.searchMovie(parameters)
}