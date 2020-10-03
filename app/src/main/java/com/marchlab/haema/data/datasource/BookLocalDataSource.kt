package com.marchlab.haema.data.datasource

import com.marchlab.haema.data.database.dao.BookDao
import com.marchlab.haema.data.database.model.BookEntity
import com.marchlab.haema.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface BookLocalDataSource {

    suspend fun add(book: BookEntity)

    suspend fun delete(id: Long)

    suspend fun update(book: BookEntity)

    fun fetch(id: Long): Flow<BookEntity>

    fun fetchAll(): Flow<List<BookEntity>>
}

class BookLocalDataSourceImpl(
    private val bookDao: BookDao
): BookLocalDataSource {

    override suspend fun add(book: BookEntity) = bookDao.add(book)

    override suspend fun delete(id: Long) = bookDao.deleteById(id)

    override suspend fun update(book: BookEntity) = bookDao.update(book)

    override fun fetch(id: Long): Flow<BookEntity> = bookDao.getById(id)

    override fun fetchAll() = bookDao.all()
}