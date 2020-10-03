package com.marchlab.haema.di

import com.marchlab.haema.BuildConfig
import com.marchlab.haema.data.apis.KakaoService
import com.marchlab.haema.data.apis.NaverService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {

    single<Interceptor> {
        HttpLoggingInterceptor().apply { level = if(BuildConfig.DEBUG) BODY else NONE }
    }

    single<Converter.Factory> { GsonConverterFactory.create() }

    single(named("kakao")) {
        OkHttpClient.Builder()
            .addInterceptor(get<Interceptor>())
            .addInterceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", getProperty("KAKAO_API_KEY"))
                    .build()

                chain.proceed(request)
            }
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(getProperty<String>("KAKAO_URL"))
            .client(get(named("kakao")))
            .addConverterFactory(get())
            .build()
            .create(KakaoService::class.java)
    }

    single(named("naver")) {
        OkHttpClient.Builder()
            .addInterceptor(get<Interceptor>())
            .addInterceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .addHeader("X-Naver-Client-Id", getProperty("NAVER_CLIENT_ID"))
                    .addHeader("X-Naver-Client-Secret", getProperty("NAVER_CLIENT_SECRET"))
                    .build()

                chain.proceed(request)
            }
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(getProperty<String>("NAVER_URL"))
            .client(get(named("naver")))
            .addConverterFactory(get())
            .build()
            .create(NaverService::class.java)
    }
}