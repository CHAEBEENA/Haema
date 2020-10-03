package com.marchlab.haema.ui.category.movie.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marchlab.haema.domain.model.Movie
import com.marchlab.haema.domain.model.MovieSearchResult
import com.marchlab.haema.domain.usecase.movie.AddMovieUseCase
import com.marchlab.haema.domain.usecase.movie.SearchMovieUseCase
import com.marchlab.haema.util.result.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate

class AddMovieViewModel(
    private val addMovieViewStateDelegate: AddMovieViewStateDelegate,
    private val searchMovieUseCase: SearchMovieUseCase,
    private val addMovieUseCase: AddMovieUseCase
): ViewModel(), AddMovieViewStateDelegate by addMovieViewStateDelegate {

    private val _searchMovieResult = MutableLiveData<Result<List<MovieSearchResult>>>()
    val searchMovieResult: LiveData<Result<List<MovieSearchResult>>>
        get() = _searchMovieResult

    private val _query = MutableLiveData<String>()

    fun searchMovie(query: String) {
        if(query.isBlank()) {
            _query.postValue(query)
            _searchMovieResult.postValue(Result.Success(emptyList()))
            return
        } else {
            viewModelScope.launch {
                _query.postValue(query)

                delay(500L)

                searchMovieUseCase(query, _searchMovieResult)
            }
        }

    }

    fun onMovieClick(movie: MovieSearchResult) = with(movie) {
        setImageUrl(image)
        setTitle(title)
        setRelease(pubDate)
        setDirector(director)
        setActor(actor)
    }

    private val _addMovieResult = MutableLiveData<Result<Unit>>()
    val addMovieResult: LiveData<Result<Unit>>
        get() = _addMovieResult

    fun onComplete() {
        viewModelScope.launch {
            addMovieUseCase(createMovieInstance(imageUrl.value, title.value, release.value, director.value, actor.value, date.value, place.value, people.value, rating.value, review.value), _addMovieResult)
        }
    }

    private fun createMovieInstance(imageUrl: String?, title: String?, release: String?, director: String?, actor: String?, date: LocalDate?, place: String?, people: String?, rating: Int?, review: String?): Movie {
        return Movie(0,
            imageUrl ?: "",
            title ?: "",
            release ?: "",
            director ?: "",
            actor ?: "",
            date ?: LocalDate.now(),
            place ?: "",
            people ?: "",
            rating ?: 3,
            review ?: ""
        )
    }

}