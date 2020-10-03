package com.marchlab.haema.ui.category.book.list

import androidx.lifecycle.*
import com.marchlab.haema.domain.model.Book
import com.marchlab.haema.domain.usecase.base.invoke
import com.marchlab.haema.domain.usecase.book.LoadBooksUseCase
import com.marchlab.haema.util.result.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class BookViewModel(
    private val loadBooksUseCase: LoadBooksUseCase
): ViewModel() {

    private val _books = MutableLiveData<Result<List<Book>>>()
    val books: LiveData<Result<List<Book>>>
        get() = _books

    init {
        viewModelScope.launch { loadBooksUseCase(_books) }
    }
}