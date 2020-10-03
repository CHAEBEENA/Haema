package com.marchlab.haema.ui.category.movie.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marchlab.haema.domain.model.Movie
import com.marchlab.haema.domain.usecase.movie.DeleteMovieUseCase
import com.marchlab.haema.domain.usecase.movie.EditMovieUseCase
import com.marchlab.haema.domain.usecase.movie.LoadMovieUseCase
import com.marchlab.haema.util.result.Result
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    private val loadMovieUseCase: LoadMovieUseCase,
    private val deleteMovieUseCase: DeleteMovieUseCase,
    private val editMovieUseCase: EditMovieUseCase
) : ViewModel() {

    private val _movie = MutableLiveData<Result<Movie>>()
    val movie: LiveData<Result<Movie>>
        get() = _movie

    private val _movieId = MutableLiveData<Long>()

    fun loadMovie(id: Long) {
        viewModelScope.launch {
            loadMovieUseCase(id, _movie)
        }
    }

    fun setMovieId(id: Long) {
        _movieId.postValue(id)
    }

    private val _deleteMovieResult = MutableLiveData<Result<Unit>>()
    val deleteMovieResult: LiveData<Result<Unit>>
        get() = _deleteMovieResult

    fun deleteMovie() {
        viewModelScope.launch {
            deleteMovieUseCase(_movieId.value?:-1L, _deleteMovieResult)
        }
    }

    private val _editMovieResult = MutableLiveData<Result<Unit>>()
    val editMovieResult: LiveData<Result<Unit>>
        get() = _editMovieResult

    fun editMovie(movie: Movie) {
        Log.d("coco-devv",movie.toString())
        viewModelScope.launch {
            editMovieUseCase(movie, _editMovieResult)
        }
    }

}
