package com.marchlab.haema.ui.category.movie.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marchlab.haema.domain.model.Movie
import com.marchlab.haema.domain.usecase.movie.LoadMoviesUseCase
import com.marchlab.haema.util.result.Result
import kotlinx.coroutines.launch

class MovieViewModel(
    private val loadMoviesUseCase: LoadMoviesUseCase
) : ViewModel() {

    private val _movies = MutableLiveData<Result<List<Movie>>>()
    val movies: LiveData<Result<List<Movie>>>
        get() = _movies

    init {
        viewModelScope.launch { loadMoviesUseCase(Unit, _movies) }
    }
}
