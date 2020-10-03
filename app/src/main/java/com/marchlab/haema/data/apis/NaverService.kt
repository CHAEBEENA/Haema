package com.marchlab.haema.data.apis

import com.marchlab.haema.data.apis.model.naver.NBookResponse
import com.marchlab.haema.data.apis.model.naver.NMovieResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NaverService {

    @GET("search/book.json")
    suspend fun searchBook(
        @Query("query") query: String,
        @Query("display") display: Int = 10, /* 기본값 10, 최대 100 */
        @Query("start") start: Int = 1 /* 검색의 시작 위치 기본값 1, 최대 1000 */
        /*
        @Query("sort") sort: String = "sim" /* sim(유사도순) date(출간일순) count(판매량순) */
         */
    ): NBookResponse

    @GET("search/movie.json")
    suspend fun searchMovie(
        @Query("query") query: String,
        @Query("display") display: Int = 50, /* 기본값 10 최대 100*/
        @Query("start") start: Int = 1/* 검색의 시작 위치 기본값 1, 최대 1000 */
       // @Query("genre") genre: String = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,22,23,24,25,26,27,28"
        /*
        @Query("genre") genre: String, /* 1: 드라마, 2: 판타지, ... */
        @Query("country") country: String /* KR JP US HK GB(영국) FR(프랑스) ETC */
         */
    ): NMovieResponse
}