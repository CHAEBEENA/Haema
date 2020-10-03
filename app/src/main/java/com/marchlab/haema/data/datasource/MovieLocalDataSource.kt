package com.marchlab.haema.data.datasource

import com.marchlab.haema.data.database.dao.MovieDao
import com.marchlab.haema.data.database.model.MovieEntity
import com.marchlab.haema.domain.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface MovieLocalDataSource {

    fun fetchAll(): Flow<List<MovieEntity>>

    fun fetch(id: Long): Flow<MovieEntity>

    suspend fun add(movie: MovieEntity)

    suspend fun update(movie: MovieEntity)

    suspend fun delete(id: Long)

}

class MovieLocalDataSourceImpl(
    private val movieDao: MovieDao
): MovieLocalDataSource {

    override fun fetchAll() = movieDao.all()

    override fun fetch(id: Long) = movieDao.getById(id)

    override suspend fun add(movie: MovieEntity) = movieDao.add(movie)

    override suspend fun update(movie: MovieEntity) = movieDao.update(movie)

    override suspend fun delete(id: Long) = movieDao.delete(id)

}