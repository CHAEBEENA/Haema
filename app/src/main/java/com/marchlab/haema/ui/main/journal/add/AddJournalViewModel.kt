package com.marchlab.haema.ui.main.journal.add

import android.net.Uri
import androidx.lifecycle.*
import com.marchlab.haema.domain.model.Emotion
import com.marchlab.haema.domain.model.Journal
import com.marchlab.haema.domain.usecase.base.invoke
import com.marchlab.haema.domain.usecase.journal.AddJournalUseCase
import com.marchlab.haema.domain.usecase.journal.EditJournalUseCase
import com.marchlab.haema.domain.usecase.journal.LoadAllDatesUseCase
import com.marchlab.haema.util.result.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate

@ExperimentalCoroutinesApi
class AddJournalViewModel(
    private val loadAllDatesUseCase: LoadAllDatesUseCase,
    private val addJournalUseCase: AddJournalUseCase
): ViewModel() {

    private val _unavailableDates = MutableLiveData<Result<List<LocalDate>>>()
    val unavailableDates: LiveData<Result<List<LocalDate>>>
        get() = _unavailableDates

    init {
        viewModelScope.launch { loadAllDatesUseCase(_unavailableDates) }
    }

    private val _date = MutableLiveData<LocalDate>()
    val date: LiveData<LocalDate>
        get() = _date

    fun setDate(date: LocalDate) = _date.postValue(date)

    private val _emotion = MutableLiveData<Emotion>()
    val emotion: LiveData<Emotion>
        get() = _emotion

    fun setEmotion(emotion: Emotion) = _emotion.postValue(emotion)

    private val _uri = MutableLiveData<Uri?>()
    val uri: LiveData<Uri?>
        get() = _uri

    fun setUri(uri: Uri?) = _uri.postValue(uri)

    private val _addJournalResult = MutableLiveData<Result<Unit>>()
    val addJournalResult: LiveData<Result<Unit>>
        get() = _addJournalResult

    fun addJournal(journal: Journal) {
        viewModelScope.launch { addJournalUseCase(journal, _addJournalResult) }
    }
}