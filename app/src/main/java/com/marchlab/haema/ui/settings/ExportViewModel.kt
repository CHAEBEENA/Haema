package com.marchlab.haema.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marchlab.haema.domain.model.Journal
import com.marchlab.haema.domain.usecase.base.invoke
import com.marchlab.haema.domain.usecase.journal.LoadJournalsUseCase
import com.marchlab.haema.util.result.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class ExportViewModel(
    private val loadJournalsUseCase: LoadJournalsUseCase
): ViewModel() {

    private val _snapshotJournals = MutableLiveData<Result<List<Journal>>>()
    val snapshotJournals: LiveData<Result<List<Journal>>>
        get() = _snapshotJournals

    init {
        viewModelScope.launch { loadJournalsUseCase(_snapshotJournals) }
    }
}
