package com.marchlab.haema.di

import com.marchlab.haema.data.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    single { AppDatabase.getInstance(androidContext()) }

    single { get<AppDatabase>().journalDao() }

    single { get<AppDatabase>().bookDao() }

    single { get<AppDatabase>().movieDao() }
}
