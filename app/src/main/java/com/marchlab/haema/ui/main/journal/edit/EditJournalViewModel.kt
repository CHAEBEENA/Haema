package com.marchlab.haema.ui.main.journal.edit

import android.net.Uri
import androidx.lifecycle.*
import com.marchlab.haema.domain.model.Emotion
import com.marchlab.haema.domain.model.Journal
import com.marchlab.haema.domain.usecase.journal.EditJournalUseCase
import com.marchlab.haema.domain.usecase.journal.LoadJournalUseCase
import com.marchlab.haema.util.result.Result
import com.marchlab.haema.util.result.succeeded
import com.marchlab.haema.util.result.successOrNull
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate

class EditJournalViewModel(
    private val loadJournalUseCase: LoadJournalUseCase,
    private val editJournalUseCase: EditJournalUseCase
): ViewModel() {

    private val _journal = MutableLiveData<Result<Journal>>()
    val journal: LiveData<Result<Journal>>
        get() = _journal

    private val _id = MediatorLiveData<Long>()
    val id: LiveData<Long>
        get() = _id

    private val _date = MediatorLiveData<LocalDate>()
    val date: LiveData<LocalDate>
        get() = _date

    private val _emotion = MediatorLiveData<Emotion>()
    val emotion: LiveData<Emotion>
        get() = _emotion

    private val _uri = MediatorLiveData<Uri?>()
    val uri: LiveData<Uri?>
        get() = _uri

    private val _content = MediatorLiveData<String>()
    val content: LiveData<String>
        get() = _content

    private val _createdAt = MediatorLiveData<Long>()
    val createdAt: LiveData<Long>
        get() = _createdAt

    private val _editedJournal = MediatorLiveData<Journal>()
    val editedJournal: LiveData<Journal>
        get() = _editedJournal

    private val _editJournalResult = MutableLiveData<Result<Unit>>()
    val editJournalResult: LiveData<Result<Unit>>
        get() = _editJournalResult

    init {
        _uri.addSource(_journal) { journal ->
            if(journal.succeeded) {
                journal.successOrNull()?.let {
                    _id.value = it.id
                    _date.value = it.date
                    _emotion.value = it.emotion
                    _uri.value = it.imageUri
                    _content.value = it.content
                    _createdAt.value = it.createdAt
                }

                _editedJournal.addSource(id) { }

                _editedJournal.addSource(date) {
                    _editedJournal.postValue(
                        Journal(requireNotNull(id.value), requireNotNull(emotion.value), uri.value, requireNotNull(content.value), it, requireNotNull(createdAt.value))
                    )
                }

                _editedJournal.addSource(emotion) {
                    _editedJournal.postValue(
                        Journal(requireNotNull(id.value), it, uri.value, requireNotNull(content.value), requireNotNull(date.value), requireNotNull(createdAt.value))
                    )
                }

                _editedJournal.addSource(uri) {
                    _editedJournal.postValue(
                        Journal(requireNotNull(id.value), requireNotNull(emotion.value), it, requireNotNull(content.value), requireNotNull(date.value), requireNotNull(createdAt.value))
                    )
                }

                _editedJournal.addSource(content) {
                    _editedJournal.postValue(
                        Journal(requireNotNull(id.value), requireNotNull(emotion.value), uri.value, it, requireNotNull(date.value), requireNotNull(createdAt.value))
                    )
                }

                _editedJournal.addSource(createdAt) {}

                _uri.removeSource(_journal)
            }
        }
    }

    fun loadJournal(id: Long) {
        viewModelScope.launch { loadJournalUseCase(id, _journal) }
    }

    fun setDate(date: LocalDate) = _date.postValue(date)

    fun setEmotion(emotion: Emotion) = _emotion.postValue(emotion)

    fun setImageUri(uri: Uri?) = _uri.postValue(uri)

    fun setContent(content: String) = _content.postValue(content)

    fun onComplete(journal: Journal) {
        viewModelScope.launch {
            editJournalUseCase(journal, _editJournalResult)
        }
    }
}