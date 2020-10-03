package com.marchlab.haema.domain.repository

import com.marchlab.haema.data.database.model.BookEntity
import com.marchlab.haema.domain.model.Book
import com.marchlab.haema.domain.model.BookSearchResult
import com.marchlab.haema.domain.usecase.movie.Query
import kotlinx.coroutines.flow.Flow

interface BookRepository {

    suspend fun add(book: Book)

    suspend fun delete(id: Long)

    suspend fun update(book: Book)

    fun fetch(id: Long): Flow<Book>

    fun fetchAll(): Flow<List<Book>>

    suspend fun searchBook(query: Query): List<BookSearchResult>
}