package com.marchlab.haema.ui.main.journal.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marchlab.haema.domain.model.Journal
import com.marchlab.haema.domain.usecase.base.invoke
import com.marchlab.haema.domain.usecase.journal.DeleteJournalUseCase
import com.marchlab.haema.domain.usecase.journal.LoadJournalsUseCase
import com.marchlab.haema.util.result.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class JournalViewModel(
    private val loadJournalsUseCase: LoadJournalsUseCase
): ViewModel() {

    private val _journals = MutableLiveData<Result<List<Journal>>>()
    val journals: LiveData<Result<List<Journal>>>
        get() = _journals

    init {
        viewModelScope.launch {
            loadJournalsUseCase(_journals)
        }
    }


}
