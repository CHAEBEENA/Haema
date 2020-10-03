package com.marchlab.haema.ui.category.book.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marchlab.haema.domain.model.Book
import com.marchlab.haema.domain.repository.BookRepository
import com.marchlab.haema.domain.usecase.book.DeleteBookUseCase
import com.marchlab.haema.domain.usecase.book.EditBookUseCase
import com.marchlab.haema.domain.usecase.book.LoadBookUseCase
import com.marchlab.haema.util.result.Result
import com.marchlab.haema.util.result.successOrNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class BookDetailViewModel(
    private val loadBookUseCase: LoadBookUseCase,
    private val deleteBookUseCase: DeleteBookUseCase
): ViewModel() {

    private val _book = MutableLiveData<Result<Book>>()
    val book: LiveData<Result<Book>>
        get() = _book

    fun loadBook(id: Long) {
        viewModelScope.launch { loadBookUseCase(id, _book) }
    }

    private val _deleteBookResult = MutableLiveData<Result<Unit>>()
    val deleteBookResult: LiveData<Result<Unit>>
        get() = _deleteBookResult

    fun deleteBook() {
        viewModelScope.launch {
            _book.value
                ?.successOrNull()
                ?.let { deleteBookUseCase(it.id, _deleteBookResult) }
        }
    }
}