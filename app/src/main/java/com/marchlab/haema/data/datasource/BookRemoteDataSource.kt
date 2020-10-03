package com.marchlab.haema.data.datasource

import com.marchlab.haema.data.apis.KakaoService
import com.marchlab.haema.data.apis.NaverService
import com.marchlab.haema.data.apis.model.kakao.KBookResponse
import com.marchlab.haema.data.apis.model.naver.NBookResponse

interface BookRemoteDataSource {

    suspend fun searchBook(query: String): KBookResponse
}

class BookRemoteDataSourceImpl(
    private val kakaoService: KakaoService
): BookRemoteDataSource {

    override suspend  fun searchBook(query: String): KBookResponse = kakaoService.searchBook(query)
}