package com.marchlab.haema.data.apis

import com.marchlab.haema.data.apis.model.kakao.KBookResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface KakaoService {

    @GET("v3/search/book")
    suspend fun searchBook(
        @Query("query") query: String,
        @Query("sort") sort: String = "accuracy", /*결과 문서 정렬 방식, accuracy(정확도순) or recency(최신순) */
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 50,
        @Query("target") target: String? = null /* 검색 필드 제한, 사용 가능 값 = title, isbn, publisher, person */
    ): KBookResponse
}