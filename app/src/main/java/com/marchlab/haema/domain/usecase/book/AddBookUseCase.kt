package com.marchlab.haema.domain.usecase.book

import com.marchlab.haema.data.database.model.BookEntity
import com.marchlab.haema.domain.model.Book
import com.marchlab.haema.domain.repository.BookRepository
import com.marchlab.haema.domain.usecase.base.UseCase

class AddBookUseCase(
    private val bookRepository: BookRepository
): UseCase<Book, Unit>() {

    override suspend fun execute(parameters: Book) = bookRepository.add(parameters)
}