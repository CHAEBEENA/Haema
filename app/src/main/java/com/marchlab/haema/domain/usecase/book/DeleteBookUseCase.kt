package com.marchlab.haema.domain.usecase.book

import com.marchlab.haema.domain.repository.BookRepository
import com.marchlab.haema.domain.usecase.base.UseCase

class DeleteBookUseCase(
    private val bookRepository: BookRepository
): UseCase<Long, Unit>() {

    override suspend fun execute(parameters: Long) = bookRepository.delete(parameters)
}