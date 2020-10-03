package com.marchlab.haema.di

import com.marchlab.haema.ui.applock.AppLockActivity
import com.marchlab.haema.ui.applock.AppLockViewModel
import com.marchlab.haema.ui.category.CategoryActivity
import com.marchlab.haema.ui.category.CategoryDatePickerFragment
import com.marchlab.haema.ui.category.CategoryViewModel
import com.marchlab.haema.ui.category.book.add.*
import com.marchlab.haema.ui.category.book.add.AddBookViewStateDelegateImpl
import com.marchlab.haema.ui.category.book.delete.DeleteBookDialogFragment
import com.marchlab.haema.ui.category.book.detail.BookDetailActivity
import com.marchlab.haema.ui.category.book.detail.BookDetailViewModel
import com.marchlab.haema.ui.category.book.edit.*
import com.marchlab.haema.ui.category.book.edit.EditBookViewStateDelegateImpl
import com.marchlab.haema.ui.category.book.list.BookFragment
import com.marchlab.haema.ui.category.book.list.BookViewModel
import com.marchlab.haema.ui.category.movie.add.*
import com.marchlab.haema.ui.category.movie.add.AddMovieViewStateDelegateImpl
import com.marchlab.haema.ui.category.movie.detail.MovieDetailActivity
import com.marchlab.haema.ui.category.movie.detail.MovieDetailViewModel
import com.marchlab.haema.ui.category.movie.edit.*
import com.marchlab.haema.ui.category.movie.edit.EditMovieViewStateDelegateImpl
import com.marchlab.haema.ui.category.movie.list.MovieFragment
import com.marchlab.haema.ui.category.movie.list.MovieViewModel
import com.marchlab.haema.ui.imagepicker.ImagePickerViewModel
import com.marchlab.haema.ui.main.ImagePickerFragment
import com.marchlab.haema.ui.main.MainActivity
import com.marchlab.haema.ui.main.MainViewModel
import com.marchlab.haema.ui.main.billing.CheckBillingStateFragment
import com.marchlab.haema.ui.main.billing.CheckBillingStateViewModel
import com.marchlab.haema.ui.main.calendar.CalendarFragment
import com.marchlab.haema.ui.main.calendar.CalendarViewModel
import com.marchlab.haema.ui.main.journal.add.AddJournalActivity
import com.marchlab.haema.ui.main.journal.add.AddJournalViewModel
import com.marchlab.haema.ui.main.journal.date.JournalDatePickerFragment
import com.marchlab.haema.ui.main.journal.date.JournalDatePickerViewModel
import com.marchlab.haema.ui.main.journal.delete.DeleteJournalDialogFragment
import com.marchlab.haema.ui.main.journal.delete.DeleteJournalDialogViewModel
import com.marchlab.haema.ui.main.journal.edit.EditJournalActivity
import com.marchlab.haema.ui.main.journal.edit.EditJournalViewModel
import com.marchlab.haema.ui.main.journal.emotion.EmotionFragment
import com.marchlab.haema.ui.main.journal.emotion.EmotionViewModel
import com.marchlab.haema.ui.main.journal.list.JournalDialogFragment
import com.marchlab.haema.ui.main.journal.list.JournalFragment
import com.marchlab.haema.ui.main.journal.list.JournalViewModel
import com.marchlab.haema.ui.settings.PasswordActivity
import com.marchlab.haema.ui.settings.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@ExperimentalCoroutinesApi
val uiModule = module {

    /* 메인 */
    scope<MainActivity> { viewModel{ MainViewModel(get(), get()) } }

    fragment { CalendarFragment() }

    scope<CalendarFragment> { viewModel { CalendarViewModel(get()) } }

    fragment { JournalFragment() }

    scope<JournalFragment> { viewModel { JournalViewModel(get()) } }

    fragment { JournalDialogFragment() }

    fragment { DeleteJournalDialogFragment() }

    scope<DeleteJournalDialogFragment> { viewModel { DeleteJournalDialogViewModel(get()) } }

    fragment { CheckBillingStateFragment() }

    scope<CheckBillingStateFragment> { viewModel { CheckBillingStateViewModel(get()) } }

    /* 일기 작성 / 수정 */
    scope<AddJournalActivity> { viewModel { AddJournalViewModel(get(), get()) } }

    scope<EditJournalActivity> { viewModel { EditJournalViewModel(get(), get()) } }

    fragment { EmotionFragment() }

    scope<EmotionFragment> { viewModel { EmotionViewModel() } }

    fragment { ImagePickerFragment() }

    scope<ImagePickerFragment> { viewModel { ImagePickerViewModel() } }

    fragment { JournalDatePickerFragment() }

    scope<JournalDatePickerFragment> { viewModel { JournalDatePickerViewModel(get()) } }

    /* 카테고리 */
    scope<CategoryActivity> { viewModel { CategoryViewModel(get()) } }

    fragment { BookFragment() }

    scope<BookFragment> { viewModel { BookViewModel(get()) } }

    fragment { MovieFragment() }

    scope<MovieFragment>{ viewModel { MovieViewModel(get()) } }

    /* 책 작성 / 수정 */
    scope<AddBookActivity> {

        viewModel { AddBookViewModel(get(), get(), get()) }

        scoped<AddBookViewStateDelegate> { AddBookViewStateDelegateImpl() }

        scoped {
            AddBookPagerAdapter(
                get<AddBookActivity>(),
                listOf(get<AddBookSearchFragment>(), get<AddBookInfoFragment>(), get<AddBookReviewFragment>())
            )
        }
    }

    fragment { AddBookSearchFragment() }

    fragment { AddBookInfoFragment() }

    fragment { AddBookReviewFragment() }

    /* 책 상세 */
    scope<EditBookActivity> {

        viewModel { EditBookViewModel(get(), get(), get()) }

        scoped<EditBookViewStateDelegate> { EditBookViewStateDelegateImpl() }

        scoped { EditBookPagerAdapter(get<EditBookActivity>(), listOf(get<EditBookInfoFragment>(), get<EditBookReviewFragment>())) }
    }

    fragment { EditBookInfoFragment() }

    fragment { EditBookReviewFragment() }

    scope<BookDetailActivity> { viewModel { BookDetailViewModel(get(), get()) } }

    fragment { CategoryDatePickerFragment() }

    fragment { DeleteBookDialogFragment() }

    /* 영화 작성 / 수정 */

    scope<AddMovieActivity> {

        scoped<AddMovieViewStateDelegate> { AddMovieViewStateDelegateImpl() }

        viewModel { AddMovieViewModel(get(), get(), get()) }

        fragment { SearchMovieFragment() }

        fragment { AddMovieInfoFragment() }

        fragment { AddMoviePersonalFragment() }

        fragment { AddMovieReviewFragment() }

        scope<AddMovieInfoFragment> {
            fragment { ImagePickerFragment() }
        }

    }

    scope<EditMovieFragment> {

        scoped<EditMovieViewStateDelegate> { EditMovieViewStateDelegateImpl() }

        viewModel { EditMovieViewModel(get()) }

        fragment { EditMovieInfoFragment() }

        fragment { EditMoviePersonalFragment() }

        fragment { EditMovieReviewFragment() }

    }

    /* 영화 상세 */
    scope<MovieDetailActivity> {
        viewModel { MovieDetailViewModel(get(), get(), get()) }

        fragment { EditMovieFragment() }

        fragment { ImagePickerFragment() }

        fragment { CategoryDatePickerFragment() }
    }

    /* 설정 */
    scope<SettingsActivity> { viewModel { SettingsViewModel(get(), get(), get()) } }

    fragment { ExportFragment() }

    scope<ExportFragment> { viewModel { ExportViewModel(get()) } }

    scope<AppLockActivity>{
        viewModel { AppLockViewModel(get()) }
    }

    scope<PasswordActivity>{
        viewModel { PasswordViewModel(get()) }
    }

}