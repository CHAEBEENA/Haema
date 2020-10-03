package com.marchlab.haema.ui.main.journal.delete

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marchlab.haema.domain.usecase.journal.DeleteJournalUseCase
import com.marchlab.haema.util.result.Result
import kotlinx.coroutines.launch

class DeleteJournalDialogViewModel(
    private val deleteJournalUseCase: DeleteJournalUseCase
): ViewModel() {

    private val _deleteJournalResult = MutableLiveData<Result<Unit>>()
    val deleteJournalResult: LiveData<Result<Unit>>
        get() = _deleteJournalResult

    fun delete(id: Long) {
        viewModelScope.launch { deleteJournalUseCase(id, _deleteJournalResult) }
    }
}
