package com.marchlab.haema.data.datasource

import com.marchlab.haema.data.apis.NaverService
import com.marchlab.haema.data.apis.model.naver.NMovieResponse

interface MovieRemoteDataSource {

    suspend fun searchMovie(query: String): NMovieResponse
}

class MovieRemoteDataSourceImpl(
    private val naverService: NaverService
): MovieRemoteDataSource {

    override suspend fun searchMovie(query: String): NMovieResponse {
        return naverService.searchMovie(query)
    }
}