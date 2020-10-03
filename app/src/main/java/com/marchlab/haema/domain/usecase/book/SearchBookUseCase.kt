package com.marchlab.haema.domain.usecase.book

import com.marchlab.haema.domain.model.BookSearchResult
import com.marchlab.haema.domain.repository.BookRepository
import com.marchlab.haema.domain.usecase.base.UseCase
import kotlinx.coroutines.delay

class SearchBookUseCase(
    private val bookRepository: BookRepository
): UseCase<String, List<BookSearchResult>>() {

    override suspend fun execute(parameters: String): List<BookSearchResult> {
        return bookRepository.searchBook(parameters)
    }
}