package com.marchlab.haema.ui.category.book.edit

import androidx.lifecycle.*
import com.marchlab.haema.domain.model.Book
import org.threeten.bp.LocalDate

interface EditBookViewStateDelegate {

    val page: LiveData<Int>

    fun onPageChanged(page: Int)

    val imageUrl: LiveData<String?>

    fun setImageUrl(imageUrl: String?)

    val title: LiveData<String>

    fun setTitle(title: String)

    val author: LiveData<String>

    fun setAuthor(author: String)

    val publisher: LiveData<String>

    fun setPublisher(publisher: String)

    val rating: LiveData<Int>

    fun setRating(rating: Int)

    val date: LiveData<LocalDate>

    fun setDate(date: LocalDate)

    val review: LiveData<String>

    fun setReview(review: String)

    val canEdit: LiveData<Boolean>

    fun setBook(book: Book)
}

internal class EditBookViewStateDelegateImpl: EditBookViewStateDelegate {

    override val page = MutableLiveData(0)

    override fun onPageChanged(page: Int) = this.page.postValue(page)

    override val imageUrl = MutableLiveData<String?>()

    override fun setImageUrl(imageUrl: String?) = this.imageUrl.postValue(imageUrl)

    override val title = MutableLiveData<String>()

    override fun setTitle(title: String) = this.title.postValue(title)

    override val author = MutableLiveData<String>()

    override fun setAuthor(author: String) = this.author.postValue(author)

    override val publisher = MutableLiveData<String>()

    override fun setPublisher(publisher: String) = this.publisher.postValue(publisher)

    override val rating = MutableLiveData<Int>()

    override fun setRating(rating: Int) = this.rating.postValue(rating)

    override val date = MutableLiveData<LocalDate>()

    override fun setDate(date: LocalDate) = this.date.postValue(date)

    override val review = MutableLiveData<String>()

    override val canEdit = rating.switchMap { rating -> title.map { title -> rating > 0 && title.isNotBlank() } }

    override fun setReview(review: String) = this.review.postValue(review)

    override fun setBook(book: Book) {
        imageUrl.value = book.imageUrl
        title.value = book.title
        author.value = book.author
        publisher.value = book.publisher
        rating.value = book.rating
        date.value = book.date
        review.value = book.review
    }
}