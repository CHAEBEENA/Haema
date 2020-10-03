package com.marchlab.haema.domain.usecase.book

import com.marchlab.haema.domain.model.Book
import com.marchlab.haema.domain.repository.BookRepository
import com.marchlab.haema.domain.usecase.base.FlowUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

/**
 * load all books from local database
 */
@ExperimentalCoroutinesApi
class LoadBooksUseCase(
    private val bookRepository: BookRepository
): FlowUseCase<Unit, List<Book>>() {

    override suspend fun execute(parameters: Unit): Flow<List<Book>> = bookRepository.fetchAll()
}