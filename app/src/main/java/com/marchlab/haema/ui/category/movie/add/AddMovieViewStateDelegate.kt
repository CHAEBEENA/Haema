package com.marchlab.haema.ui.category.movie.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.marchlab.haema.domain.model.Movie
import org.threeten.bp.LocalDate

interface AddMovieViewStateDelegate{
    val page: LiveData<Int>

    val imageUrl: LiveData<String?>

    val title: LiveData<String>

    val release: LiveData<String>

    val director: LiveData<String>

    val actor: LiveData<String>

    val date: LiveData<LocalDate>

    val place: LiveData<String>

    val people: LiveData<String>

    val rating: LiveData<Int>

    val review: LiveData<String>

    val skipFilled: LiveData<Boolean>

    val movie: LiveData<Movie>

    fun onPageChange(page: Int)

    fun setImageUrl(imageUrl: String?)

    fun setTitle(title: String)

    fun setRelease(release: String)

    fun setDirector(director: String)

    fun setActor(actor: String)

    fun setDate(review: LocalDate)

    fun setPlace(place: String)

    fun setPeople(people: String)

    fun setRating(rating: Int)

    fun setReview(review: String)

    fun setMovie(movie: Movie)

    fun clear()

}

internal class AddMovieViewStateDelegateImpl:
    AddMovieViewStateDelegate {

    override val page = MutableLiveData(0)

    override val imageUrl = MutableLiveData<String?>()

    override val title = MutableLiveData<String>()

    override val release = MutableLiveData<String>()

    override val director = MutableLiveData<String>()

    override val actor = MutableLiveData<String>()

    override val date = MutableLiveData(LocalDate.now())

    override val place = MutableLiveData<String>()

    override val people = MutableLiveData<String>()

    override val rating = MutableLiveData(3)

    override val review = MutableLiveData<String>()

    override val skipFilled = people.switchMap {people ->
        place.map { place ->
            place.isNotBlank() || people.isNotBlank()
        }
    }

    override val movie = MutableLiveData<Movie>()

    override fun onPageChange(page: Int) = this.page.postValue(page)

    override fun setImageUrl(imageUrl: String?) = this.imageUrl.postValue(imageUrl)

    override fun setTitle(title: String) = this.title.postValue(title)

    override fun setRelease(release: String) = this.release.postValue(release)

    override fun setDirector(director: String) = this.director.postValue(director)

    override fun setActor(actor: String) = this.actor.postValue(actor)

    override fun setDate(date: LocalDate) = this.date.postValue(date)

    override fun setPlace(place: String) = this.place.postValue(place)

    override fun setPeople(people: String) = this.people.postValue(people)

    override fun setRating(rating: Int) = this.rating.postValue(rating)

    override fun setReview(review: String) = this.review.postValue(review)

    override fun setMovie(movie: Movie) = this.movie.postValue(movie)

    override fun clear(){
        imageUrl.postValue(null)
        title.postValue("")
        release.postValue("")
        director.postValue("")
        actor.postValue("")
        date.postValue(LocalDate.now())
        place.postValue("")
        people.postValue("")
        rating.postValue(3)
        review.postValue("")
    }
}
