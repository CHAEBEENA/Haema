package com.marchlab.haema.di

import com.marchlab.haema.data.datasource.*
import com.marchlab.haema.data.preference.HaemaPreferenceStorage
import com.marchlab.haema.data.preference.PreferenceStorage
import com.marchlab.haema.data.repository.*
import com.marchlab.haema.domain.repository.*
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataSourceModule = module {
    /* journal */
    single<JournalLocalDataSource> { JournalLocalDataSourceImpl(get()) }

    single<JournalRepository> { JournalRepositoryImpl(get()) }

    /* movie */
    single<MovieLocalDataSource> { MovieLocalDataSourceImpl(get()) }

    single<MovieRemoteDataSource> { MovieRemoteDataSourceImpl(get()) }

    single<MovieRepository> { MovieRepositoryImpl(get(), get()) }

    /* book */
    single<BookLocalDataSource> { BookLocalDataSourceImpl(get()) }

    single<BookRemoteDataSource> { BookRemoteDataSourceImpl(get()) }

    single<BookRepository> { BookRepositoryImpl(get(), get()) }

    /* billing */
    single { HaemaPreferenceStorage(androidContext()) }

    single<PreferenceStorage> { get<HaemaPreferenceStorage>() }

    single<BillingLocalDataSource> { BillingLocalDataSourceImpl(get()) }

    single<BillingRepository> { BillingRepositoryImpl(get()) }

    /* app lock */
    single<AppLockDataSource> { AppLockDataSourceImpl(get()) }

    single<AppLockRepository> { AppLockRepositoryImpl(get()) }
}