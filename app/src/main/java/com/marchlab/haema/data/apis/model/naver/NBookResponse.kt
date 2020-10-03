package com.marchlab.haema.data.apis.model.naver

data class NBookResponse(
    val total: Int,
    val start: Int,
    val display: Int,
    val items: List<NBook>
)