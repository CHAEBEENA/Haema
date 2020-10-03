package com.marchlab.haema.data.repository

import com.marchlab.haema.data.apis.model.kakao.KBookDocument
import com.marchlab.haema.data.apis.model.kakao.KBookResponse
import com.marchlab.haema.data.database.model.BookEntity
import com.marchlab.haema.data.datasource.BookLocalDataSource
import com.marchlab.haema.data.datasource.BookRemoteDataSource
import com.marchlab.haema.domain.model.Book
import com.marchlab.haema.domain.model.BookSearchResult
import com.marchlab.haema.domain.repository.BookRepository
import com.marchlab.haema.domain.usecase.movie.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.threeten.bp.LocalDate

class BookRepositoryImpl(
    private val bookLocalDataSource: BookLocalDataSource,
    private val bookRemoteDataSource: BookRemoteDataSource
): BookRepository {

    override suspend fun add(book: Book) { bookLocalDataSource.add(book.transform()) }

    override suspend fun delete(id: Long) = bookLocalDataSource.delete(id)

    override suspend fun update(book: Book) = bookLocalDataSource.update(book.transform())

    override fun fetch(id: Long): Flow<Book> = bookLocalDataSource.fetch(id).map { it.transform() }

    override fun fetchAll(): Flow<List<Book>> = bookLocalDataSource.fetchAll().map { books -> books.map { it.transform() } }

    override suspend fun searchBook(query: Query): List<BookSearchResult> {
        val response = bookRemoteDataSource.searchBook(query)

        return if(response.documents.isNullOrEmpty()) emptyList()
        else response.documents.map { it.transform() }
    }

    private fun Book.transform() = BookEntity(id, title, author, publisher, imageUrl, rating, review, date.toEpochDay(), createdAt, System.currentTimeMillis())

    private fun BookEntity.transform() = Book(id, title, author, publisher, imageUrl, rating, review, LocalDate.ofEpochDay(date), createdAt)

    private fun KBookDocument.transform() = BookSearchResult(thumbnail, title, authors.joinToString(), publisher)
}