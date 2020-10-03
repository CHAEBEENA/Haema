package com.marchlab.haema.domain.usecase.book

import com.marchlab.haema.domain.model.Book
import com.marchlab.haema.domain.repository.BookRepository
import com.marchlab.haema.domain.usecase.base.FlowUseCase
import com.marchlab.haema.domain.usecase.base.UseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
class LoadBookUseCase(
    private val bookRepository: BookRepository
): FlowUseCase<Long, Book>() {

    override suspend fun execute(parameters: Long): Flow<Book> = bookRepository.fetch(parameters)
}