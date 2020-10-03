package com.marchlab.haema.ui.category.book.add

import androidx.lifecycle.*
import com.marchlab.haema.domain.model.Book
import com.marchlab.haema.domain.model.BookSearchResult
import com.marchlab.haema.domain.usecase.book.AddBookUseCase
import com.marchlab.haema.domain.usecase.book.SearchBookUseCase
import com.marchlab.haema.util.result.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate

class AddBookViewModel(
    private val addBookViewStateDelegate: AddBookViewStateDelegate,
    private val searchBookUseCase: SearchBookUseCase,
    private val addBookUseCase: AddBookUseCase
) : ViewModel(),
    AddBookViewStateDelegate by addBookViewStateDelegate {

    private val _searchBookResult = MutableLiveData<Result<List<BookSearchResult>>>()
    val searchBookResult: LiveData<Result<List<BookSearchResult>>>
        get() = _searchBookResult

    fun search(query: String) {
        viewModelScope.launch { searchBookUseCase(query, _searchBookResult) }
    }

    fun onBookSelected(book: BookSearchResult) = with(book) {
        setImageUrl(imageUrl.takeIf { it.isNotBlank() })
        setTitle(title)
        setAuthor(authors)
        setPublisher(publisher)
    }

    private val _addBookResult = MutableLiveData<Result<Unit>>()
    val addBookResult: LiveData<Result<Unit>>
        get() = _addBookResult

    fun onComplete() {
        viewModelScope.launch {
            val book = Book(
                0L,
                requireNotNull(title.value),
                requireNotNull(author.value),
                requireNotNull(publisher.value),
                imageUrl.value ?: "",
                requireNotNull(rating.value),
                requireNotNull(review.value),
                requireNotNull(date.value),
                System.currentTimeMillis()
            )

            addBookUseCase(book, _addBookResult)
        }
    }
}