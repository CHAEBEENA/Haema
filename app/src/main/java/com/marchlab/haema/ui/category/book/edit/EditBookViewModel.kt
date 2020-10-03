package com.marchlab.haema.ui.category.book.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marchlab.haema.domain.model.Book
import com.marchlab.haema.domain.usecase.book.EditBookUseCase
import com.marchlab.haema.domain.usecase.book.LoadBookUseCase
import com.marchlab.haema.util.result.Result
import com.marchlab.haema.util.result.successOrNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import timber.log.Timber
import timber.log.debug

@ExperimentalCoroutinesApi
class EditBookViewModel(
    private val editBookViewStateDelegate: EditBookViewStateDelegate,
    private val loadBookUseCase: LoadBookUseCase,
    private val editBookUseCase: EditBookUseCase
) : ViewModel(),
    EditBookViewStateDelegate by editBookViewStateDelegate {

    private val _book = MutableLiveData<Result<Book>>()
    val book: LiveData<Result<Book>>
        get() = _book

    fun loadBook(id: Long) {
        viewModelScope.launch { loadBookUseCase(id, _book) }
    }

    private val _editBookResult = MutableLiveData<Result<Unit>>()
    val editBookResult: LiveData<Result<Unit>>
        get() = _editBookResult

    fun saveEditedBook() {
        viewModelScope.launch {
            val editedBook = Book(
                requireNotNull(book.value?.successOrNull()?.id),
                requireNotNull(title.value),
                requireNotNull(author.value),
                requireNotNull(publisher.value),
                imageUrl.value ?: "",
                requireNotNull(rating.value),
                requireNotNull(review.value),
                requireNotNull(date.value),
                requireNotNull(book.value?.successOrNull()?.createdAt)
            )

            editBookUseCase(editedBook, _editBookResult)
        }
    }
}