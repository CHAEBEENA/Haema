package com.marchlab.haema.di

import com.marchlab.haema.domain.usecase.book.*
import com.marchlab.haema.domain.usecase.journal.*
import com.marchlab.haema.domain.usecase.movie.*
import com.marchlab.haema.domain.usecase.setting.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.dsl.module

@ExperimentalCoroutinesApi
val domainModule = module {
    /* journal */
    single { AddJournalUseCase(get()) }

    single { EditJournalUseCase(get()) }

    single { DeleteJournalUseCase(get()) }

    single { LoadJournalsUseCase(get()) }

    single { LoadAllDatesUseCase(get()) }

    single { LoadJournalUseCase(get()) }

    /* movie */
    single { SearchMovieUseCase(get()) }

    single { AddMovieUseCase(get()) }

    single { LoadMoviesUseCase(get()) }

    single { LoadMovieUseCase(get()) }

    single { DeleteMovieUseCase(get()) }

    single{ EditMovieUseCase(get()) }

    /* book */
    single { LoadBookUseCase(get()) }

    single { LoadBooksUseCase(get()) }

    single { SearchBookUseCase(get()) }

    single { AddBookUseCase(get()) }

    single { EditBookUseCase(get()) }

    single { DeleteBookUseCase(get()) }

    /* billing */
    single { CheckIfPurchasedUseCase(get()) }

    single { SetPurchasedUseCase(get()) }

    /* app lock */
    single { CheckAppLockUseCase(get()) }

    single { GetPasswordUseCase(get()) }

    single { SetPasswordUseCase(get()) }

    single { RemovePasswordUseCase(get()) }
}