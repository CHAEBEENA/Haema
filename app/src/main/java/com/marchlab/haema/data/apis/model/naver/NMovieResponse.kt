package com.marchlab.haema.data.apis.model.naver

data class NMovieResponse(
    val total: Int,
    val start: Int,
    val display: Int,
    val items: List<NMovie>
)